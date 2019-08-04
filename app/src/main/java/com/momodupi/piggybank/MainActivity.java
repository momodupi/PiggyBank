package com.momodupi.piggybank;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.widget.GridView.*;



public class MainActivity extends AppCompatActivity {

    String[] gridViewString = {
            "Restaurant", "Car Service", "Hotel", "Rent", "Electronics", "Fruits",
            "Clothing", "Personal Care", "Courses", "Party",
            "Fuel", "Software", "Season", "Airplane", "Railway", "Treatment",
            "Supplement", "Water", "Tissue", "Movie", "Network",
            "Game", "Tools", "Exercise", "Drinks", "Cooking", "Shopping", "Books",
            "Accidents", "Mobile Payment", "Other"
    };

    int[] gridViewImageId = {
            R.mipmap.restaurant, R.mipmap.carservice,
            R.mipmap.hotel, R.mipmap.rent,
            R.mipmap.light, R.mipmap.watermelon,
            R.mipmap.clothes, R.mipmap.dispenser,
            R.mipmap.classroom,
            R.mipmap.party, R.mipmap.gas,
            R.mipmap.software, R.mipmap.spice,
            R.mipmap.airplane, R.mipmap.train,
            R.mipmap.treatment, R.mipmap.supplement,
            R.mipmap.water, R.mipmap.tissue,
            R.mipmap.movie, R.mipmap.internethub,
            R.mipmap.gamecontroller, R.mipmap.tools,
            R.mipmap.dumbbell, R.mipmap.cocktail,
            R.mipmap.cooking, R.mipmap.buying,
            R.mipmap.book, R.mipmap.bang,
            R.mipmap.mobilepayment, R.mipmap.decision
    };

    private Toolbar mTopToolbar;
    public String type_input = null;


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



        GridViewActivity tpyrgridview_act = new GridViewActivity(MainActivity.this, gridViewString, gridViewImageId);
        final GridView typegridview = (GridView) findViewById(R.id.type_grid);
        typegridview.setAdapter(tpyrgridview_act);


        final LinearLayout btm_frame = findViewById(R.id.btm_frame);
        final LinearLayout btm_box = findViewById(R.id.btm_box);


        final DatabaseHelper dbbasehelper = new DatabaseHelper(this, "book", null, 1);
        final SQLiteDatabase sqliteDatabase = dbbasehelper.getWritableDatabase();

        final Button type_btn = (Button) findViewById(R.id.type_button);
        type_btn.setBackgroundResource(R.mipmap.decision);


        final EditText num_text = (EditText) findViewById(R.id.input_edittext);


        final Button saveBtn = (Button) findViewById(R.id.input_btn);
        saveBtn.requestFocus();


        typegridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type_btn.setBackgroundResource(gridViewImageId[position]);
                type_input = gridViewString[position];
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

                    sqliteDatabase.insert("book", null, values);

                    //String sql = "insert into book (book_type, book_date, book_time, book_amount) values ('fruit', '2222-22-22', '22-22', str)";
                    //sqliteDatabase.execSQL(sql);
                    /**/
                    Cursor cursor = sqliteDatabase.query("book", new String[] { "book_type", "book_time", "book_amount"},
                            "book_type=?", new String[] { "Restaurant" }, null, null, null);
                    cursor = sqliteDatabase.rawQuery("select * from book",null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String str_type = cursor.getString(0);
                        String str_time = cursor.getString(1);
                        float str_amount = cursor.getFloat(2);
                        // do something useful with these
                        cursor.moveToNext();
                        Log.d("sqlite read", str_type + " ," + str_time + " ," + str_amount);
                    }
                    cursor.close();

                    num_text.setText(null);
                }
                else {
                    Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
                    num_text.setText(null);
                }
            }
        });




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

}
