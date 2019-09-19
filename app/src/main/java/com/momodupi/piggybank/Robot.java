package com.momodupi.piggybank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


class Robot {
    private Context botcontext;
    private String book;
    private String starttime;
    private String histroytime;
    private String oldesttime;

    private String reply_str;

    private String input_type;
    private String input_time;
    private float input_amount;

    private boolean isInputCorrect;

    private SQLiteDatabase sqliteDatabase;

    private AccountTypes accountTypes;

    private float[] type_total;


    Robot(Context context, String bookname){
        this.book = bookname;
        this.botcontext = context;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.starttime = simpleDateFormat.format(new Date());
        this.histroytime = this.starttime;

        DatabaseHelper dbbasehelper;
        dbbasehelper = new DatabaseHelper(context, this.book, null, 1);
        sqliteDatabase = dbbasehelper.getWritableDatabase();

        //String cmd = String.format("SELECT * FROM %s ORDER BY %s DESC", this.book, "book_time");
        //sqliteDatabase.execSQL(cmd);

        accountTypes = new AccountTypes(context);
        type_total = new float[accountTypes.getTypeString().length];

        //sqliteDatabase.rawQuery("SELECT MIN(" + dbbasehelper.COLUMNSNAME[1] + ") FROM " + this.book, null);
        /*
        Cursor cursor = this.sqliteDatabase.query(this.book,
                new String[] { "book_type", "book_time", "book_amount", "book_reply"},
                "book_type=? AND book_time=? AND book_amount=? AND book_reply=?",
                new String[] { this.input_type, this.input_time, String.valueOf(this.input_amount), this.reply_str },
                null, null, null);


         */

        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM " + this.book + " ORDER BY book_time ASC LIMIT 2", null);

        if (cursor.getCount() >= 2) {
            cursor.moveToFirst();
            cursor.moveToNext();
            this.oldesttime = cursor.getString(1);
            Log.d("old time", cursor.getString(1));
            cursor.close();
        }
        else {
            this.oldesttime = this.getCurrentTime();
        }
    }


    void read(String type, String time, String amount) {
        this.input_type = type;
        this.input_time = time;
        this.input_amount = Float.parseFloat(amount);

        this.isInputCorrect = !(!this.isTypeLegal(this.input_type) || this.input_time.isEmpty() || this.input_amount <= 0 );
    }

    private boolean messageProcess() {
        ContentValues values = new ContentValues();
        values.put("book_type", this.input_type);
        values.put("book_time", this.input_time);
        values.put("book_amount", String.valueOf(this.input_amount));

        if (this.isInputCorrect) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String h_time = this.getCurrentTime();
            h_time = h_time.split(" ")[0] + " 00:00:00";
            //Log.d("time", h_time);

            try {
                Date date = simpleDateFormat.parse(h_time);
                Calendar calendar = Calendar.getInstance();
                if (date != null) {
                    calendar.setTime(date);
                }
                // compare with the data with 4 month ago
                calendar.add(Calendar.MONTH, -4);
                String ph_time = simpleDateFormat.format(calendar.getTime());

                List<structure_Database> history_list = this.getData(this.input_type, ph_time, h_time);

                if (history_list == null) {
                    this.reply_str = this.getRandomAnswer(0);
                    //Log.d("reply", this.reply_str);
                }
                else {
                    String [] type_list = accountTypes.getTypeString();

                    int cnt = 0;
                    for (String t : type_list) {
                        if (t.equals(this.input_type)) {
                            break;
                        }
                        cnt++;
                    }
                    type_total[cnt] = 0;

                    for (structure_Database sd : history_list) {
                        type_total[cnt] += sd.getAmount();
                    }

                    float avg = type_total[cnt]/history_list.size();
                    this.reply_str = this.getRandomAnswer((this.input_amount - avg)/avg);
                    //Log.d("persentage", String.valueOf((this.input_amount - avg)/avg));
                    //Log.d("reply", this.reply_str);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }

        values.put("book_reply", this.reply_str);

        this.sqliteDatabase.insert(this.book, null, values);

        Cursor cursor = this.sqliteDatabase.query(this.book,
                new String[] { "book_type", "book_time", "book_amount", "book_reply"},
                "book_type=? AND book_time=? AND book_amount=? AND book_reply=?",
                new String[] { this.input_type, this.input_time, String.valueOf(this.input_amount), this.reply_str },
                null, null, null);

        //cursor = sqliteDatabase.rawQuery("select * from books",null);
        cursor.moveToFirst();

        String checktype = null;
        String checktime = null;
        float checknum = 0;
        String checkreply = null;

        while (!cursor.isAfterLast()) {
            checktype = cursor.getString(0);
            checktime = cursor.getString(1);
            checknum = cursor.getFloat(2);
            checkreply = cursor.getString(3);

            cursor.moveToNext();
        }
        cursor.close();

        Log.d("read", checktype + "  " + checktime + "  " + checknum + "  " + checkreply);
        //Log.d("sqlite read", (checktype.equals(type_input))  + " " + checktime.equals(datetime) + " " + (checknum==Float.parseFloat(num_str)));
        if (checktype != null && checktime != null && checkreply != null) {
            return ((checktype.equals(this.input_type)) && checktime.equals(this.input_time) && (checknum == this.input_amount && (checkreply.equals(this.reply_str))));
        }
        else {
            return false;
        }
    }


    String reply() {
        if (this.messageProcess()) {
            return this.reply_str;
        }
        else {
            return botcontext.getResources().getString(R.string.typemistake);
        }
    }

    String getInputTime() {
        return this.input_time;
    }

    String getInputTpye() {
        return this.input_type;
    }

    String getBotStartTime() {
        return this.starttime;
    }

    String getBotHistoryTime() {
        return this.histroytime;
    }

    String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(new java.util.Date());
    }

    void deleteDataBase() {
        this.sqliteDatabase.delete(this.book, null, null);
    }

    void deleteItem(String type, String time, String amount) {
        this.sqliteDatabase.delete(this.book, "book_type=? AND book_time=? AND book_amount=?",
                new String[] {type, time, amount });
    }

    private boolean isTypeLegal(String type) {
        return Arrays.asList(accountTypes.getTypeString()).contains(type);
    }


    List<structure_Database> getData(String type, String starttime, String endtime) {
        List<structure_Database> savelist = new ArrayList<>();

        Cursor cursor;

        if (isTypeLegal(type))  {
            cursor = this.sqliteDatabase.query(this.book,
                    new String[] { "book_type", "book_time", "book_amount", "book_reply"},
                    "book_type=? AND book_time BETWEEN ? AND ?",
                    new String[] { type, starttime, endtime },
                    null, null, "book_time ASC");
        }
        else {
            cursor = sqliteDatabase.query(this.book,
                    new String[] { "book_type", "book_time", "book_amount", "book_reply"},
                    "book_time BETWEEN ? AND ?", new String[] { starttime, endtime },
                    null, null, "book_time ASC");
        }

        cursor.moveToFirst();

        String checktype;
        String checktime;
        float checknum;
        String checkreply;
        this.reply_str = "";

        while (!cursor.isAfterLast()) {
            checktype = cursor.getString(0);
            checktime = cursor.getString(1);
            checknum = cursor.getFloat(2);
            checkreply = cursor.getString(3);

            savelist.add(new structure_Database(checktype, checktime, checknum, checkreply));
            cursor.moveToNext();
        }
        cursor.close();

        Log.d("return", String.valueOf(savelist.size()));
        return savelist;
    }

    void showToday(MessageAdapter msa, ListView msgv) {
        String h_time = this.starttime.split(" ")[0] + " 00:00:00";

        List<structure_Database> todaydata = this.getData("ALL", h_time, this.starttime);
        Log.d("data", " " + h_time + "    " + this.starttime);

        Message msg_s;
        Collections.reverse(todaydata);

        for(structure_Database msg: todaydata) {
            //structure_Database msg = todaydata.get(pos);
            String amount_str = String.valueOf(msg.getAmount());

            msg_s = new Message(msg.getReply(), msg.getTime(), msg.getType(), "bot");
            msa.addtotop(msg_s);
            //msgv.setSelection(msa.getCount() - 1);

            msg_s = new Message(amount_str, msg.getTime(), msg.getType(), "master");
            msa.addtotop(msg_s);
            //msgv.setSelection(msa.getCount() - 1);
        }

        if (todaydata.size() != 0) {
            this.histroytime = h_time;
        }
        msg_s = new Message(null, this.histroytime, null, "date");
        msa.addtotop(msg_s);
        msgv.smoothScrollToPosition(msa.getCount() - 1);
    }


    void showHistory(MessageAdapter msa, ListView msgv, String rqsttime) {
        Log.d("history time", rqsttime + "  " + this.oldesttime + "   " + this.histroytime);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            if (!simpleDateFormat.parse(rqsttime).before(simpleDateFormat.parse(this.oldesttime))) {

                if (simpleDateFormat.parse(rqsttime).before(simpleDateFormat.parse(this.histroytime))) {
                    //Log.d("time",this.histroytime + "  " + rqsttime);

                    List<structure_Database> historydata = this.getData("ALL", rqsttime, this.histroytime);

                    Message msg_s;
                    Collections.reverse(historydata);

                    for (structure_Database msg : historydata) {
                        //structure_Database msg = historydata.get(pos);
                        String amount_str = String.valueOf(msg.getAmount());

                        msg_s = new Message(msg.getReply(), msg.getTime(), msg.getType(), "bot");
                        msa.addtotop(msg_s);
                        msgv.setSelection(msa.getCount() - 1);

                        msg_s = new Message(amount_str, msg.getTime(), msg.getType(), "master");
                        msa.addtotop(msg_s);
                        msgv.setSelection(msa.getCount() - 1);
                    }

                    this.histroytime = rqsttime;
                    //Log.d("time", "history: " + this.histroytime);
                    msg_s = new Message(null, this.histroytime, null, "date");
                    msa.addtotop(msg_s);
                    msgv.smoothScrollToPosition(historydata.size());
                }
            }
            else {
                Toast.makeText(botcontext, botcontext.getString(R.string.nomoredata), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getActivity().getApplicationContext(), Resources.getSystem().getString(R.string.loadingfailed), Toast.LENGTH_SHORT).show();
        }
    }


    String showSomeData(String type, String starttime, String endtime) {
        List<structure_Database> datalist = this.getData(type, starttime, endtime);

        StringBuilder buffer = new StringBuilder();
        for(structure_Database msg: datalist) {
            //structure_Database msg = datalist.get(pos);
            String amount_str = String.valueOf(msg.getAmount());

            if (type.equals("ALL")) {
                String str = msg.getType() + "\n" + botcontext.getResources().getString(R.string.moneyunit)
                        + amount_str + " at " + msg.getTime().substring(0, 16) + "\n\n";
                buffer.append(str);
                /*
                this.reply_str += msg.getType() + "\n" + botcontext.getResources().getString(R.string.moneyunit)
                        + amount_str + " at " + msg.getTime().substring(0, 16) + "\n\n";

                 */
            }
            else {
                String str = amount_str + " at " + msg.getTime().substring(0, 16) + "\n\n";
                buffer.append(str);
                //this.reply_str += amount_str + " at " + msg.getTime().substring(0, 16) + "\n\n";
            }
        }
        return this.reply_str + buffer.toString() + "(=ﾟωﾟ)=";
    }

    private String getRandomAnswer(float persentage) {

        String[] answer;
        if (persentage > 0.5) {
            answer = botcontext.getResources().getStringArray(R.array.highprice_answer);
        }
        else if (persentage < 0.2) {
            answer = botcontext.getResources().getStringArray(R.array.lowprice_answer);
        }
        else {
            answer = botcontext.getResources().getStringArray(R.array.mediumprice_answer);
        }

        return answer[(int) Math.floor(Math.random() * answer.length)];
    }

    String exportDataBaes(Context context, String path) {
        List<structure_Database> alldata = this.getData("ALL", "2000-00-00 00:00:00", this.getCurrentTime());
        StringBuilder buffer = new StringBuilder();

        buffer.append("type, time, amount, relay\r\n");
        //Log.d("path", path);

        for(structure_Database msg: alldata){
            String amount_str = String.valueOf(msg.getAmount());
            String addstr = msg.getType() + "," + msg.getTime() +
                    "," + amount_str + ","+ msg.getReply() + "\r\n";
            buffer.append(addstr);
        }

        try {
            String data = buffer.toString();
            String filename = "book_" + this.getCurrentTime().split(" ")[0] + ".csv";

            File file = new File(path, filename);

            //Log.d("export status", file.getAbsolutePath());
            FileOutputStream outputStream = new FileOutputStream(file);

            outputStream.write(data.getBytes());
            outputStream.close();
            //Log.d("export status", "successd!");

            return context.getResources().getString(R.string.backups);

        } catch (Exception e) {
            //Log.d("export status", "faile!");
            e.printStackTrace();
            return context.getResources().getString(R.string.backupf);
        }
    }

    String importDataBase(Context context, String path) {
        Log.d("path", path);

        try {
            FileReader file = new FileReader(path);
            BufferedReader buffer = new BufferedReader(file);
            String buf_line;

            this.sqliteDatabase.execSQL("DELETE FROM " + this.book);

            while ((buf_line = buffer.readLine()) != null) {

                String[] buf_str = buf_line.split(",");

                ContentValues values = new ContentValues();
                values.put("book_type", buf_str[0]);
                values.put("book_time", buf_str[1]);
                values.put("book_amount", buf_str[2]);
                values.put("book_reply", buf_str[3]);
                //Log.d("read", buf_str[0]);

                this.sqliteDatabase.insert(this.book, null, values);
            }
            //sqliteDatabase.setTransactionSuccessful();
            //sqliteDatabase.endTransaction();
            this.starttime = this.getCurrentTime();
            this.histroytime = this.starttime;

            Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM " + this.book + " ORDER BY book_time ASC LIMIT 2", null);

            if (cursor.getCount() >= 2) {
                cursor.moveToFirst();
                cursor.moveToNext();
                this.oldesttime = cursor.getString(1);
                Log.d("old time", cursor.getString(1));
                cursor.close();
            }
            else {
                this.oldesttime = this.getCurrentTime();
            }

            return context.getResources().getString(R.string.recoverys);
        }
        catch (Exception e) {
            //Log.d("export status", "faile!");
            e.printStackTrace();
            return context.getResources().getString(R.string.recoveryf);
        }
    }

}



class structure_Database {
    private String type;
    private String time;
    private float amount;
    private String reply;

    structure_Database(String type, String time, float amount, String reply) {
        this.type = type;
        this.time = time;
        this.amount = amount;
        this.reply = reply;
    }

    String getType() {
        return this.type;
    }

    String getTime() {
        return this.time;
    }

    String getReply() {
        return this.reply;
    }

    float getAmount() {
        return this.amount;
    }
}
