package com.momodupi.piggybank;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Random;


public class MainActivity extends AppCompatActivity {


    static Integer imgbtn_anim[] = {0,0,0};

    private Toolbar mTopToolbar;
    public String type_input = null;

    private GridView typegridview;
    private GridViewAdatper tpyrgridview_act;

    private LinearLayout btm_frame;
    private LinearLayout btm_box;

    private DatabaseHelper dbbasehelper;
    private SQLiteDatabase sqliteDatabase;

    private ImageButton type_btn;
    private EditText num_text;
    private ImageButton saveBtn;

    private Animation outAnimation;
    private Animation inAnimation;

    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private AccountTypes accounttype;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences preferences = getSharedPreferences("piggypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("kaomojipref", MODE_PRIVATE).edit();
        if (!preferences.getBoolean("nonvirgin", false)) {
            editor.putInt("keyboard_height", 0);
            Log.d("STATE", "I'm virgin");
        }
        editor.putBoolean("nonvirgin", true);
        editor.apply();

        accounttype = new AccountTypes();
        tpyrgridview_act = new GridViewAdatper(MainActivity.this, accounttype.getTpyeString(), accounttype.getTpyeIcon());
        typegridview = (GridView) findViewById(R.id.type_grid);
        typegridview.setAdapter(tpyrgridview_act);


        btm_frame = findViewById(R.id.btm_frame);
        btm_box = findViewById(R.id.btm_box);


        dbbasehelper = new DatabaseHelper(this, "book", null, 1);
        sqliteDatabase = dbbasehelper.getWritableDatabase();

        type_btn = (ImageButton) findViewById(R.id.type_button);
        type_btn.setImageResource(R.mipmap.decision);


        num_text = (EditText) findViewById(R.id.input_edittext);


        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.requestFocus();
        saveBtn.setImageResource(R.mipmap.ellipsis);
        saveBtn.setTag(R.mipmap.ellipsis);

        // Obtain a reference to the Activity Context

        // Create the Animation objects.
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);


        typegridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //type_btn.setImageResource(gridViewImageId[position]);
                type_input = accounttype.getTpyeString()[position];
                imgbtn_anim[0] = R.id.type_button;
                imgbtn_anim[1] = (Integer) type_btn.getTag();
                imgbtn_anim[2] = accounttype.getTpyeIcon()[position];
                type_btn.startAnimation(outAnimation);
                Toast.makeText(MainActivity.this, "Select: " + type_input + "!", Toast.LENGTH_SHORT).show();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String num_str = num_text.getText().toString();
                //Float amount = Float.parseFloat(str);
                if (num_str != null && type_input != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                    String datetime = simpleDateFormat.format(new java.util.Date());

                    ContentValues values = new ContentValues();
                    values.put("book_type", type_input);
                    values.put("book_time", datetime);
                    values.put("book_amount", num_str);

                    sendMessage(view, num_str, datetime, type_input, true);

                    sqliteDatabase.insert("book", null, values);

                    //String sql = "insert into book (book_type, book_date, book_time, book_amount) values ('fruit', '2222-22-22', '22-22', str)";
                    //sqliteDatabase.execSQL(sql);
                    /**/
                    Cursor cursor = sqliteDatabase.query("book",
                            new String[] { "book_type", "book_time", "book_amount"},
                            "book_type=? AND book_time=? AND book_amount=?",
                            new String[] { type_input, datetime, num_str },
                            null, null, null);

                    //cursor = sqliteDatabase.rawQuery("select * from book",null);
                    cursor.moveToFirst();

                    String checktype = null;
                    String checktime = null;
                    float checknum = 0;

                    while (!cursor.isAfterLast()) {
                        checktype = cursor.getString(0);
                        checktime = cursor.getString(1);
                        checknum = cursor.getFloat(2);
                        // do something useful with these
                        cursor.moveToNext();
                    }
                    cursor.close();

                    Log.d("sqlite read", (checktype.equals(type_input))  + " " + checktime.equals(datetime) + " " + (checknum==Float.parseFloat(num_str)));
                    if ((checktype.equals(type_input))  && checktime.equals(datetime) && (checknum==Float.parseFloat(num_str))) {
                        sendMessage(view, String.valueOf(checknum), checktime, checktype, false);
                        Log.d("sqlite read", "message checked");
                    }
                    else {
                        Log.d("sqlite read", "message wrong");
                    }

                    num_text.setText(null);
                }
                else {
                    Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
                    num_text.setText(null);
                }
            }
        });

        num_text.addTextChangedListener(new TextWatcher() {
            boolean text_empty_flag = true;
            Integer savbtn_tag = (Integer) saveBtn.getTag();
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    text_empty_flag = false;
                }
                else {
                    text_empty_flag = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0 && text_empty_flag == true) {
                    imgbtn_anim[0] = R.id.save_btn;
                    imgbtn_anim[1] = R.mipmap.ellipsis;
                    imgbtn_anim[2] = R.mipmap.moneytransfer;
                    saveBtn.startAnimation(outAnimation);
                }
                else if (charSequence.length() == 0) {
                    imgbtn_anim[0] = R.id.save_btn;
                    imgbtn_anim[1] = R.mipmap.moneytransfer;
                    imgbtn_anim[2] = R.mipmap.ellipsis;
                    saveBtn.startAnimation(outAnimation);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ImageButton btn = findViewById(imgbtn_anim[0]);
                if (imgbtn_anim[2] != imgbtn_anim[1]) {
                    btn.setImageResource(imgbtn_anim[2]);
                    btn.setTag(imgbtn_anim[2]);
                    btn.startAnimation(inAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.message_list);
        messagesView.setAdapter(messageAdapter);

        /*
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    public void sendMessage(View view, String num_str, String time_str, String type_str, boolean sender) {
        if (num_str.length() > 0) {
            Message msg_s = new Message(num_str, time_str, type_str, sender);
            messageAdapter.add(msg_s);
            messagesView.setSelection(messagesView.getCount() - 1);
        }
    }

}
