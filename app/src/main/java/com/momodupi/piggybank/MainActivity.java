package com.momodupi.piggybank;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    static Integer[] imgbtn_anim = {0,0,0};

    public String type_input = null;

    private ImageButton typeBtn;
    private EditText numText;
    private ImageButton saveBtn;
    private ImageButton timeBtn;

    private Animation outAnimation;
    private Animation inAnimation;

    private SwipeRefreshLayout messageFrame;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private AccountTypes accounttype;

    private Robot robot;

    private String edittime = "noedit";
    private int editposition;

    private InputMethodManager inputMethodManager;
    private TypeKeyboard typeKeyboard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView typegridview;
        GridViewAdatper tpyrgridview_act;
        LinearLayout panelFrame;
        Toolbar toolbar;

        robot = new Robot(this, DatabaseHelper.BOOKNAME);
        //robot.deleteDataBase();

        accounttype = new AccountTypes(this);
        tpyrgridview_act = new GridViewAdatper(MainActivity.this, accounttype.getTypeString(), accounttype.getTypeIcon());
        typegridview = findViewById(R.id.type_grid);
        typegridview.setAdapter(tpyrgridview_act);

        messageFrame = findViewById(R.id.message_frame);
        messageFrame.setColorSchemeColors(
                this.getColor(R.color.chartlightblue500),
                this.getColor(R.color.chartgray500));

        //btmFrame = findViewById(R.id.btm_frame);
        panelFrame = findViewById(R.id.panel_frame);


        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        typeBtn = findViewById(R.id.type_button);
        typeBtn.setImageResource(R.mipmap.unknown);


        numText = findViewById(R.id.input_edittext);
        numText.requestFocus();


        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setImageResource(R.mipmap.etransfer);
        saveBtn.setTag(R.mipmap.etransfer);

        timeBtn = findViewById(R.id.time_btn);
        timeBtn.setTag(R.mipmap.transparent);
        timeBtn.setImageResource(R.mipmap.transparent);
        timeBtn.setVisibility(View.INVISIBLE);

        // Create the Animation objects.
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.message_list);
        messagesView.setAdapter(messageAdapter);


        robot.showToday(messageAdapter, messagesView);

        messageFrame.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String h_time = robot.getBotHistoryTime();
                h_time = h_time.split(" ")[0] + " 00:00:00";
                Log.d("time", h_time);

                try {
                    Date date = simpleDateFormat.parse(h_time);
                    Calendar calendar = Calendar.getInstance();
                    if (date != null) {
                        calendar.setTime(date);
                    }

                    //calendar.add(Calendar.DATE, -1);
                    //String ph_time = simpleDateFormat.format(calendar.getTime());
                    //Log.d("time", ph_time + "  &&&&  " + h_time);
                    //robot.showHistory(messageAdapter, messagesView, ph_time);

                    int historysize = messageAdapter.getCount();
                    for (int pd=1; pd<=7; pd++) {
                        calendar.add(Calendar.DATE, -1);
                        String ph_time = simpleDateFormat.format(calendar.getTime());
                        //Log.d("time", ph_time + "  &&&&  " + h_time);
                        robot.showHistory(messageAdapter, messagesView, ph_time);
                    }
                    Log.d("history size", historysize + "");
                    messageAdapter.notifyDataSetChanged();
                    messagesView.setSelection(messageAdapter.getCount() - historysize);
                    messagesView.smoothScrollToPosition(messageAdapter.getCount() - historysize - 2);

                    Log.d("move to", messageAdapter.getCount() - historysize + "");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.loadingfailed), Toast.LENGTH_SHORT).show();
                }

                messageFrame.setRefreshing(false);
            }
        });

        messagesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, int i, long l) {

                Context wrapper = new ContextThemeWrapper(getBaseContext(), R.style.mActionBarTheme);
                PopupMenu popup = new PopupMenu(wrapper, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popmenu, popup.getMenu());

                editposition = i;

                final Message selectemsg = (Message) messageAdapter.getItem(i);
                Log.d("message", "user: " + selectemsg.getUser() + "  type: "
                        + selectemsg.getType() + "  time: " + selectemsg.getTime() + "  amount: " + selectemsg.getText());

                if (selectemsg.getUser().equals("master")) {
                    popup.show();

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            if (id == R.id.popdelete) {
                                robot.deleteItem(selectemsg.getType(), selectemsg.getTime(), selectemsg.getText());
                                messageAdapter.remove(selectemsg);
                            }
                            else if (id == R.id.popedit) {
                                edittime = selectemsg.getTime();
                                numText.setText(selectemsg.getText());
                                type_input = selectemsg.getType();
                                imgbtn_anim[0] = R.id.type_button;
                                imgbtn_anim[1] = (Integer) typeBtn.getTag();
                                imgbtn_anim[2] = accounttype.findIconbySring(selectemsg.getType());
                                typeBtn.startAnimation(outAnimation);

                                timeBtn.setVisibility(View.VISIBLE);
                                imgbtn_anim[0] = R.id.time_btn;
                                imgbtn_anim[1] = (Integer) timeBtn.getTag();
                                imgbtn_anim[2] = R.mipmap.time;
                                timeBtn.startAnimation(outAnimation);

                                robot.deleteItem(selectemsg.getType(), selectemsg.getTime(), selectemsg.getText());
                                messageAdapter.remove(selectemsg);
                            }
                            return false;
                        }
                    });
                }
                return false;
            }
        });


        typegridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //type_btn.setImageResource(gridViewImageId[position]);
                type_input = accounttype.getTypeString()[position];
                imgbtn_anim[0] = R.id.type_button;
                imgbtn_anim[1] = (Integer) typeBtn.getTag();
                imgbtn_anim[2] = accounttype.getTypeIcon()[position];
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

                    if (!edittime.equals("noedit")) {
                        //datetime = edittime;
                        //sendMessageToPosition(num_str, datetime, type_input, "master", editposition);
                        //robot.read(type_input, datetime, num_str);
                        sendMessageToPosition(num_str, edittime, type_input, "master", editposition);
                        robot.read(type_input, edittime, num_str);

                        sendMessageToPosition(robot.reply(), robot.getInputTime(), robot.getInputTpye(), "bot", editposition+1);

                        Log.d("send", "edit: true");
                        edittime = "noedit";

                        messageAdapter = new MessageAdapter(MainActivity.this);
                        messagesView.setAdapter(messageAdapter);

                        robot.showToday(messageAdapter, messagesView);
                    }
                    else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String datetime = simpleDateFormat.format(new java.util.Date());

                        sendMessage(num_str, datetime, type_input, "master");
                        robot.read(type_input, datetime, num_str);

                        sendMessage(robot.reply(), robot.getInputTime(), robot.getInputTpye(), "bot");
                        Log.d("send", "edit: false");
                    }

                    imgbtn_anim[0] = R.id.time_btn;
                    imgbtn_anim[1] = (Integer) timeBtn.getTag();
                    imgbtn_anim[2] = R.mipmap.transparent;
                    timeBtn.startAnimation(outAnimation);
                    timeBtn.setVisibility(View.INVISIBLE);

                    numText.setText(null);
                    inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (view.getWindowToken() != null) {
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "(´ﾟДﾟ`)", Toast.LENGTH_SHORT).show();
                    numText.setText(null);
                }
            }
        });

        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int yy = c.get(Calendar.YEAR);
                int mm = c.get(Calendar.MONTH);
                int dd = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            edittime = year + "-" + String.format("%02d", monthOfYear+1) + "-" + String.format("%02d", dayOfMonth) + " " + edittime.split(" ")[1];

                            int h = c.get(Calendar.HOUR_OF_DAY);
                            int m = c.get(Calendar.MINUTE);
                            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                                    new TimePickerDialog.OnTimeSetListener() {

                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            edittime = edittime.split(" ")[0] + " " + String.format("%02d", hourOfDay+1) + ":" + String.format("%02d", minute) + ":00";
                                        }
                                    }, h, m, false);
                            timePickerDialog.show();
                        }
                    }, yy, mm, dd);
                datePickerDialog.show();

                Log.d("edittime", edittime);
            }
        });

        numText.addTextChangedListener(new TextWatcher() {
            boolean text_empty_flag = true;
            //Integer savbtn_tag = (Integer) saveBtn.getTag();
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text_empty_flag = (charSequence.length() != 0);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0 && text_empty_flag) {
                    imgbtn_anim[0] = R.id.save_btn;
                    imgbtn_anim[1] = R.mipmap.etransfer;
                    imgbtn_anim[2] = R.mipmap.transfer;
                    saveBtn.startAnimation(outAnimation);
                }
                else if (charSequence.length() == 0) {
                    imgbtn_anim[0] = R.id.save_btn;
                    imgbtn_anim[1] = R.mipmap.transfer;
                    imgbtn_anim[2] = R.mipmap.etransfer;
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
                if (!imgbtn_anim[2].equals(imgbtn_anim[1])) {
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

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }


    @Override
    public void onBackPressed() {
        if (!typeKeyboard.interceptBackPress()) {
            super.onBackPressed();
        }
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
        //int id = item.getItemId();
        //Log.d("item", item.toString());

        Intent intent;

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_chart:
                intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();

                overridePendingTransition(R.anim.rightin, R.anim.leftout);

                return true;

            case R.id.action_settings:
                sendMessage(robot.showSomeData("ALL", "2019-01-01 00:00:00", robot.getCurrentTime()), robot.getInputTime(), "ALL", "bot");
                return true;

            case R.id.action_backup:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    sendMessage(getResources().getString(R.string.needpermission), null, "ALL", "bot");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    startActivityForResult(intent, 1);
                }
                return true;

            case R.id.action_import:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    sendMessage(getResources().getString(R.string.needpermission), null, "ALL", "bot");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                } else {
                    intent = new Intent().setType("*/*").setAction(Intent.ACTION_OPEN_DOCUMENT);
                    startActivityForResult(Intent.createChooser(intent, "Select a file"), 2);
                }
                return true;

            case R.id.action_about:
                Toast.makeText(MainActivity.this, R.string.diag_message, Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri select = data.getData();
            //Log.d("path", select.toString());
            String path = FileUtil.getFullPathFromTreeUri(select, this);
            //String path = select.getPath();
            sendMessage(robot.exportDataBaes(this, path), null, "ALL", "bot");
        }
        else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri select = data.getData();
            //Log.d("path", select.toString());
            String path = FileUtil.getFullPathFromUri(select, this);
            //Log.d("path", path);

            messageAdapter = null;
            messageAdapter = new MessageAdapter(this);
            messagesView.setAdapter(messageAdapter);

            sendMessage(robot.importDataBase(this, path), null, "ALL", "bot");
            robot.showToday(messageAdapter, messagesView);
        }
    }



    public void sendMessage(String num_str, String time_str, String type_str, String sender) {
        if (num_str.length() > 0) {
            Message msg_s = new Message(num_str, time_str, type_str, sender);
            messageAdapter.add(msg_s);
            messagesView.setSelection(messagesView.getCount() - 1);
        }
    }

    public void sendMessageToPosition(String num_str, String time_str, String type_str, String sender, int pos) {
        if (num_str.length() > 0) {
            Message msg_s = new Message(num_str, time_str, type_str, sender);
            messageAdapter.addtoppostition(msg_s, pos);
            messagesView.setSelection(messagesView.getCount() - 1);
        }
    }
}


