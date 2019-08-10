package com.momodupi.piggybank;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity {


    static Integer imgbtn_anim[] = {0,0,0};

    private Toolbar mTopToolbar;
    public String type_input = null;

    private GridView typegridview;
    private GridViewAdatper tpyrgridview_act;

    //private View rootView;
    private LinearLayout btmFrame;
    private LinearLayout panelFrame;

    //private DatabaseHelper dbbasehelper;
    //private SQLiteDatabase sqliteDatabase;

    private ImageButton typeBtn;
    private EditText numText;
    private ImageButton saveBtn;

    private Animation outAnimation;
    private Animation inAnimation;

    private SwipeRefreshLayout messageFrame;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private AccountTypes accounttype;

    private Robot robot;


    private InputMethodManager inputMethodManager;
    private TypeKeyboard typeKeyboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        robot = new Robot(this, "book");

        accounttype = new AccountTypes();
        tpyrgridview_act = new GridViewAdatper(MainActivity.this, accounttype.getTpyeString(), accounttype.getTpyeIcon());
        typegridview = (GridView) findViewById(R.id.type_grid);
        typegridview.setAdapter(tpyrgridview_act);

        messageFrame = (SwipeRefreshLayout) findViewById(R.id.message_frame);
        messageFrame.setColorSchemeColors(
                getResources().getColor(R.color.refreshcolor1),
                getResources().getColor(R.color.refreshcolor2));


        //rootView = findViewById(R.id.backgroundlayout);
        btmFrame = findViewById(R.id.btm_frame);
        panelFrame = findViewById(R.id.panel_frame);


        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleview);


        typeBtn = (ImageButton) findViewById(R.id.type_button);
        typeBtn.setImageResource(R.mipmap.decision);


        numText = (EditText) findViewById(R.id.input_edittext);
        numText.requestFocus();


        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.requestFocus();
        saveBtn.setImageResource(R.mipmap.ellipsis);
        saveBtn.setTag(R.mipmap.ellipsis);

        // Create the Animation objects.
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.message_list);
        messagesView.setAdapter(messageAdapter);

        messageFrame.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String h_time = robot.getBotHistoryTime();
                h_time = h_time.split(" ")[0] + " 00:00:00";
                Log.d("time", h_time);

                try {
                    Date date = simpleDateFormat.parse(h_time);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DATE, -1);
                    String ph_time = simpleDateFormat.format(calendar.getTime());
                    //Log.d("time", ph_time + "  &&&&  " + h_time);
                    robot.getHistroy(messageAdapter, messagesView, ph_time);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "(´ﾟДﾟ`)", Toast.LENGTH_SHORT).show();
                }

                messageFrame.setRefreshing(false);
            }
        });


        messagesView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //numText.clearFocus();
                inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Log.d("STATE", "touch messageview");
                return false;
            }
        });


        typegridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //type_btn.setImageResource(gridViewImageId[position]);
                type_input = accounttype.getTpyeString()[position];
                imgbtn_anim[0] = R.id.type_button;
                imgbtn_anim[1] = (Integer) typeBtn.getTag();
                imgbtn_anim[2] = accounttype.getTpyeIcon()[position];
                typeBtn.startAnimation(outAnimation);
                Toast.makeText(MainActivity.this, "Select: " + type_input + "!", Toast.LENGTH_SHORT).show();
            }
        });
        /**/

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num_str = numText.getText().toString();
                //Float amount = Float.parseFloat(str);
                if (!num_str.isEmpty() && type_input != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String datetime = simpleDateFormat.format(new java.util.Date());

                    sendMessage(view, num_str, datetime, type_input, "master");
                    robot.read(type_input, datetime, num_str);

                    sendMessage(view, robot.reply(), robot.getInputTime(), robot.getInputTpye(), "bot");

                    numText.setText(null);
                    inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                else {
                    Toast.makeText(MainActivity.this, "(´ﾟДﾟ`)", Toast.LENGTH_SHORT).show();
                    numText.setText(null);
                }
            }
        });

        numText.addTextChangedListener(new TextWatcher() {
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

        typeKeyboard = new TypeKeyboard(this, numText, panelFrame, typeBtn, messageFrame);


        /*
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        */
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
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
            View view = new View(this);
            sendMessage(view, robot.showAllData("ALL", "123", "234"), robot.getInputTime(), robot.getInputTpye(), "bot");
            return true;
        }
        else if (id == R.id.action_clear) {
            //sqliteDatabase.delete("book", null, null);
            robot.deleteDataBase();
        }
        else if (id == R.id.action_about) {
            //robot.getHistroy(messageAdapter, messagesView, "2019-08-08 03:30:00");
        }

        return super.onOptionsItemSelected(item);
    }


    public void sendMessage(View view, String num_str, String time_str, String type_str, String sender) {
        if (num_str.length() > 0) {
            Message msg_s = new Message(num_str, time_str, type_str, sender);
            messageAdapter.add(msg_s);
            messagesView.setSelection(messagesView.getCount() - 1);
        }
    }


}
