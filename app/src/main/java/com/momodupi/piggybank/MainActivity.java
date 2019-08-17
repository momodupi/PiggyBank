package com.momodupi.piggybank;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {


    static Integer imgbtn_anim[] = {0,0,0};

    private Toolbar mTopToolbar;
    public String type_input = null;

    private GridView typegridview;
    private GridViewAdatper tpyrgridview_act;

    private LinearLayout btmFrame;
    private LinearLayout panelFrame;

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

    private String edittime = "noedit";
    private int editposition;

    private InputMethodManager inputMethodManager;
    private TypeKeyboard typeKeyboard;

    private Uri selectedfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        robot = new Robot(this, DatabaseHelper.BOOKNAME);
        //robot.deleteDataBase();

        accounttype = new AccountTypes(this);
        tpyrgridview_act = new GridViewAdatper(MainActivity.this, accounttype.getTpyeString(), accounttype.getTpyeIcon());
        typegridview = (GridView) findViewById(R.id.type_grid);
        typegridview.setAdapter(tpyrgridview_act);

        messageFrame = (SwipeRefreshLayout) findViewById(R.id.message_frame);
        messageFrame.setColorSchemeColors(
                getResources().getColor(R.color.refreshcolor1),
                getResources().getColor(R.color.refreshcolor2));

        btmFrame = findViewById(R.id.btm_frame);
        panelFrame = findViewById(R.id.panel_frame);


        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        typeBtn = (ImageButton) findViewById(R.id.type_button);
        typeBtn.setImageResource(R.mipmap.unknown);


        numText = (EditText) findViewById(R.id.input_edittext);
        numText.requestFocus();


        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setImageResource(R.mipmap.etransfer);
        saveBtn.setTag(R.mipmap.etransfer);

        // Create the Animation objects.
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.message_list);
        messagesView.setAdapter(messageAdapter);


        robot.getToday(messageAdapter, messagesView);


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
                                robot.delteItem(selectemsg.getType(), selectemsg.getTime(), selectemsg.getText());
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

                                robot.delteItem(selectemsg.getType(), selectemsg.getTime(), selectemsg.getText());
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

                    if (!edittime.equals("noedit")) {
                        datetime = edittime;
                        sendMessageToPosition(num_str, datetime, type_input, "master", editposition);
                        robot.read(type_input, datetime, num_str);

                        sendMessageToPosition(robot.reply(), robot.getInputTime(), robot.getInputTpye(), "bot", editposition+1);
                        Log.d("send", "edit: true");
                        edittime = "noedit";
                    }
                    else {
                        sendMessage(num_str, datetime, type_input, "master");
                        robot.read(type_input, datetime, num_str);

                        sendMessage(robot.reply(), robot.getInputTime(), robot.getInputTpye(), "bot");
                        Log.d("send", "edit: false");
                    }

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

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mTopToolbar);
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
        int id = item.getItemId();
        Log.d("item", item.toString());

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
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Uri select = data.getData();
            Log.d("path", select.toString());
            String path = FileUtil.getFullPathFromTreeUri(select, this);
            //String path = select.getPath();
            sendMessage(robot.exportDataBaes(path), null, "ALL", "bot");
        }
        else if(requestCode == 2 && resultCode == RESULT_OK) {
            Uri select = data.getData();
            Log.d("path", select.toString());
            String path = FileUtil.getFullPathFromUri(select, this);
            Log.d("path", path);

            messageAdapter = null;
            messageAdapter = new MessageAdapter(this);
            messagesView.setAdapter(messageAdapter);

            sendMessage(robot.importDataBase(path), null, "ALL", "bot");
            robot.getToday(messageAdapter, messagesView);
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
