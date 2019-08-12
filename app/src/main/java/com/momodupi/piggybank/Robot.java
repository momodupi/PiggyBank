package com.momodupi.piggybank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Robot {

    private String book;
    private String starttime;
    private String histroytime;

    private String reply_str;

    private String input_type;
    private String input_time;
    private float input_amount;

    private boolean isInputCorrect;

    private DatabaseHelper dbbasehelper;
    private SQLiteDatabase sqliteDatabase;

    private Context botcontext;


    public Robot(Context context, String bookname){
        this.book = bookname;
        this.botcontext = context;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.starttime = simpleDateFormat.format(new java.util.Date());
        this.histroytime = this.starttime;

        dbbasehelper = new DatabaseHelper(context, this.book, null, 1);
        sqliteDatabase = dbbasehelper.getWritableDatabase();
    }


    public void read(String type, String time, String amount) {
        this.input_type = type;
        this.input_time = time;
        this.input_amount = Float.parseFloat(amount);

        //AccountTypes accountTypes = new AccountTypes();

        if (!this.isTypeLegal(this.input_type) || this.input_time.isEmpty() || this.input_amount <= 0 ) {
            this.isInputCorrect = false;
        }
        else {
            this.isInputCorrect = true;
        }
    }

    public boolean messageProcess() {
        ContentValues values = new ContentValues();
        values.put("book_type", this.input_type);
        values.put("book_time", this.input_time);
        values.put("book_amount", String.valueOf(this.input_amount));

        if (this.isInputCorrect) {
            this.reply_str = this.getRandomAnswer();
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

        //cursor = sqliteDatabase.rawQuery("select * from book",null);
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

        //Log.d("sqlite read", (checktype.equals(type_input))  + " " + checktime.equals(datetime) + " " + (checknum==Float.parseFloat(num_str)));
        if ((checktype.equals(this.input_type))  && checktime.equals(this.input_time) && (checknum == this.input_amount && (checkreply.equals(this.reply_str)))) {
            //sendMessage(view, botreply, checktime, checktype, false);
            //Log.d("sqlite read", "message checked");
            return true;
        }
        else {
            //Log.d("sqlite read", "message wrong");
            return false;
        }
    }


    public String reply() {
        if (this.messageProcess()) {
            return this.reply_str;
        }
        else {
            return "I can't understand!";
        }
    }

    public String getInputTime() {
        return this.input_time;
    }

    public String getInputTpye() {
        return this.input_type;
    }

    public String getBotStartTime() {
        return this.starttime;
    }

    public String getBotHistoryTime() {
        return this.histroytime;
    }

    public String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new java.util.Date());
    }

    public void deleteDataBase() {
        this.sqliteDatabase.delete(this.book, null, null);
    }

    public void delteItem(String type, String time, String amount) {
        this.sqliteDatabase.delete(this.book, "book_type=? AND book_time=? AND book_amount=?",
                new String[] {type, time, amount });
    }

    public boolean isTypeLegal(String type) {
        AccountTypes accountTypes = new AccountTypes();
        return Arrays.asList(accountTypes.getTpyeString()).contains(type);
    }


    private List<structure_Database> getData(String type, String starttime, String endtime) {
        List<structure_Database> savelist = new ArrayList<structure_Database>();

        Cursor cursor;

        if (isTypeLegal(type))  {
            cursor = this.sqliteDatabase.query(this.book,
                    new String[] { "book_type", "book_time", "book_amount", "book_reply"},
                    "book_type=? AND book_time BETWEEN ? AND ?",
                    new String[] { type, starttime, endtime },
                    null, null, null);
        }
        else {
            cursor = sqliteDatabase.query(this.book,
                    new String[] { "book_type", "book_time", "book_amount", "book_reply"},
                    "book_time BETWEEN ? AND ?", new String[] { starttime, endtime },
                    null, null, null);
        }

        cursor.moveToFirst();

        String checktype = null;
        String checktime = null;
        float checknum = 0;
        String checkreply = null;
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

        return savelist;
    }

    public void getToday(MessageAdapter msa, ListView msgv) {
        String h_time = this.starttime.split(" ")[0] + " 00:00:00";

        List<structure_Database> todaydata = this.getData("ALL", h_time, this.starttime);
        Log.d("data", " " + h_time + "    " + this.starttime);

        Message msg_s;

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

            msg_s = new Message(null, this.histroytime, null, "date");
            msa.addtotop(msg_s);
            msgv.smoothScrollToPosition(todaydata.size() - 1);
        }
    }


    public void getHistroy(MessageAdapter msa, ListView msgv, String rqsttime) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (simpleDateFormat.parse(rqsttime).before(simpleDateFormat.parse(this.histroytime))) {
                Log.d("time",this.histroytime + "  " + rqsttime);

                List<structure_Database> historydata = this.getData("ALL", rqsttime, this.histroytime);

                Message msg_s;

                for(structure_Database msg: historydata) {
                    //structure_Database msg = historydata.get(pos);
                    String amount_str = String.valueOf(msg.getAmount());

                    msg_s = new Message(msg.getReply(), msg.getTime(), msg.getType(), "bot");
                    msa.addtotop(msg_s);
                    //msgv.setSelection(msa.getCount() - 1);

                    msg_s = new Message(amount_str, msg.getTime(), msg.getType(), "master");
                    msa.addtotop(msg_s);
                    //msgv.setSelection(msa.getCount() - 1);
                }

                if (historydata.size() != 0) {
                    this.histroytime = rqsttime;
                    Log.d("time", "history: " + this.histroytime);

                    msg_s = new Message(null, this.histroytime, null, "date");
                    msa.addtotop(msg_s);
                    msgv.smoothScrollToPosition(historydata.size() - 1);
                }
            }
            else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.botcontext, "(´ﾟДﾟ`)", Toast.LENGTH_SHORT).show();
        }
    }


    public String showSomeData(String type, String starttime, String endtime) {
        List<structure_Database> datalist = this.getData(type, starttime, endtime);

        for(structure_Database msg: datalist) {
            //structure_Database msg = datalist.get(pos);
            String amount_str = String.valueOf(msg.getAmount());

            if (type.equals("ALL")) {
                this.reply_str += msg.getType() + "\n$" + amount_str + " at " + msg.getTime().substring(0, 16) + "\n\n";
            }
            else {
                this.reply_str += amount_str + " at " + msg.getTime().substring(0, 16) + "\n\n";
            }
        }
        return reply_str + "(=ﾟωﾟ)=";
    }

    public String getRandomAnswer() {

        String[][] answer = {{"Got it!", "Sure!", "Yes, I got it.", "Understand.",
                "OK!", "I see.", "Perfect!", "Received."},
                {"Wow!", "OK!", "No problem!", "Yes!", "Roger.", "Hum!"},
                {"Woooooow!", "Are you serious?", "Ouch!", "Mmm...", "Woo-Hoo!",
                        "It's shocking!", "Surprising!"}};

        int answer_flag = 0;

        switch (this.input_type) {
            case "Restaurant": {
                if (this.input_amount <= 10) {
                    answer_flag = 0;
                }
                else if (this.input_amount <= 20) {
                    answer_flag = 1;
                }
                else {
                    answer_flag = 2;
                }
            }
            break;
            case "Rent": {
                answer_flag = 1;
            }
            break;
            case "Mobile Payment": {
                answer_flag = 0;
            }
            break;
            case "Fuel": {
                if (this.input_amount <= 20) {
                    answer_flag = 0;
                }
                else {
                    answer_flag = 1;
                }
            }
            break;
            default: {
                if (this.input_amount <= 20) {
                    answer_flag = 0;
                }
                else if (this.input_amount <= 50) {
                    answer_flag = 1;
                }
                else {
                    answer_flag = 2;
                }
            }
        }
        Log.d("answer", "flag: " + answer_flag);
        return answer[answer_flag][(int) Math.floor(Math.random() * answer.length)];
    }

    public void exportDataBaes() {
        List<structure_Database> alldata = this.getData("ALL", "2000-00-00 00:00:00", this.getCurrentTime());
        StringBuffer buffer = new StringBuffer();

        buffer.append("type, time, amount, relay\r\n");

        for(structure_Database msg: alldata){
            String amount_str = String.valueOf(msg.getAmount());
            buffer.append(msg.getType() + "," + msg.getTime() +
                    "," + amount_str + ","+ msg.getReply() + "\r\n");
        }

        try {
            String data = buffer.toString();
            String filename = "book_" + this.getCurrentTime().split(" ")[0] + ".csv";

            //Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            //intent.setData(Uri.parse("package:" + botcontext.getPackageName()));
            //botcontext.startActivity(intent);

            String path = botcontext.getExternalFilesDir("test").getPath();
            Log.d("path", path);
            File file = new File(path, filename);
            Log.d("export status", file.getAbsolutePath());
            FileOutputStream outputStream = new FileOutputStream(file);


            outputStream.write(data.getBytes());
            outputStream.close();
            Log.d("export status", "successd!");
        } catch (Exception e) {
            Log.d("export status", "faile!");
            e.printStackTrace();
        }
    }

}



class structure_Database {
    private String type;
    private String time;
    private float amount;
    private String reply;

    public structure_Database(String type, String time, float amount, String reply) {
        this.type = type;
        this.time = time;
        this.amount = amount;
        this.reply = reply;
    }

    public String getType() {
        return this.type;
    }

    public String getTime() {
        return this.time;
    }

    public String getReply() {
        return this.reply;
    }

    public float getAmount() {
        return this.amount;
    }
}
