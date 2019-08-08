package com.momodupi.piggybank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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

        dbbasehelper = new DatabaseHelper(context, "book", null, 1);
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

        this.sqliteDatabase.insert("book", null, values);

        Cursor cursor = this.sqliteDatabase.query("book",
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
            // do something useful with these
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

    public String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new java.util.Date());
    }

    public void deleteDataBase() {
        this.sqliteDatabase.delete("book", null, null);
    }

    public boolean isTypeLegal(String type) {
        AccountTypes accountTypes = new AccountTypes();
        return Arrays.asList(accountTypes.getTpyeString()).contains(type);
    }

    public void getHistroy(MessageAdapter msa, ListView msgv, String rqsttime) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("time",this.histroytime + "  " + rqsttime);
        try {
            if (simpleDateFormat.parse(rqsttime).before(simpleDateFormat.parse(this.histroytime))) {
                Log.d("time",this.histroytime + "  " + rqsttime);
                Cursor cursor;
                cursor = this.sqliteDatabase.query("book",
                        null,
                        "book_time BETWEEN ? AND ?",
                        new String[] { "2019-08-01 00:00:00", rqsttime },
                        null, null, null);


                //cursor = sqliteDatabase.rawQuery("select * from book",null);
                cursor.moveToLast();

                String checktype = null;
                String checktime = null;
                float checknum = 0;
                String checkreply = null;

                Message msg_s;
                int current_pos = 0;

                while (!cursor.isBeforeFirst()) {
                    checktype = cursor.getString(0);
                    checktime = cursor.getString(1);
                    checknum = cursor.getFloat(2);
                    checkreply = cursor.getString(3);

                    msg_s = new Message(checkreply, checktime, checktype, false);
                    msa.addtotop(msg_s);
                    msgv.setSelection(msgv.getCount() - 1);

                    msg_s = new Message(String.valueOf(checknum), checktime, checktype, true);
                    msa.addtotop(msg_s);
                    msgv.setSelection(msgv.getCount() - 1);

                    current_pos += 2;
                    msgv.setSelection(current_pos);

                    cursor.moveToPrevious();
                }
                cursor.close();
                this.histroytime = rqsttime;
            }
            else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.botcontext, "(´ﾟДﾟ`)", Toast.LENGTH_SHORT).show();
        }

    }


    public String showAllData(String type, String starttime, String endtime) {

        Cursor cursor;
        if (isTypeLegal(type))  {
            cursor = this.sqliteDatabase.query("book",
                    new String[] { "book_type", "book_time", "book_amount", "book_reply"},
                    "book_type=?",
                    new String[] { type },
                    null, null, null);
        }
        else {
            cursor = sqliteDatabase.query("book",
                    new String[] { "book_type", "book_time", "book_amount", "book_reply"},
                    null, null, null, null, null);
        }


        //cursor = sqliteDatabase.rawQuery("select * from book",null);
        cursor.moveToFirst();

        String checktype = null;
        String checktime = null;
        float checknum = 0;
        String checkreply = null;
        this.reply_str = type + "\n";

        while (!cursor.isAfterLast()) {
            //checktype = cursor.getString(0);
            checktime = cursor.getString(1);
            checknum = cursor.getFloat(2);
            //checkreply = cursor.getString(3);
            // do something useful with these
            this.reply_str += checktime + ": $" + checknum + "\n";

            cursor.moveToNext();
        }
        cursor.close();

        return reply_str;
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

}


