package com.momodupi.piggybank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.widget.GridView.*;


public class MainActivity extends AppCompatActivity {

    String[] gridViewString = {
            "Restaurant", "Car", "Mobile", "Housing", "Electronics", "Foods",
    };

    int[] gridViewImageId = {
            R.mipmap.baseline_drive_eta_black_48, R.mipmap.baseline_drive_eta_black_48,
            R.mipmap.baseline_restaurant_black_48, R.mipmap.baseline_restaurant_black_48,
            R.mipmap.baseline_restaurant_black_48, R.mipmap.baseline_restaurant_black_48,
    };

    private Toolbar mTopToolbar;
    public String type_input = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridViewActivity tpyrgridview_act = new GridViewActivity(MainActivity.this, gridViewString, gridViewImageId);
        GridView tpyrgridview = (GridView) findViewById(R.id.type_grid);
        tpyrgridview.setAdapter(tpyrgridview_act);



        final DatabaseHelper dbbasehelper = new DatabaseHelper(this, "book", null, 1);
        final SQLiteDatabase sqliteDatabase = dbbasehelper.getWritableDatabase();

        final ImageView type_imgview = (ImageView) findViewById(R.id.type_imageView);
        type_imgview.setImageResource(R.mipmap.baseline_restaurant_black_48);

        final EditText num_text = (EditText) findViewById(R.id.input_edittext);

        final Button saveBtn = (Button) findViewById(R.id.input_btn);
        saveBtn.requestFocus();

        tpyrgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                type_imgview.setImageResource(gridViewImageId[position]);
                type_input = gridViewString[position];
                Toast.makeText(MainActivity.this, "press " + type_input + "!", Toast.LENGTH_SHORT).show();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String str = num_text.getText().toString();
                //Float amount = Float.parseFloat(str);
                if (str != null) {

                    ContentValues values = new ContentValues();
                    values.put("book_type", type_input);
                    values.put("book_date", "1111-11-11");
                    values.put("book_time", "11-11-11");
                    values.put("book_amount", str);
                    sqliteDatabase.insert("book", null, values);
                    //String sql = "insert into book (book_type, book_date, book_time, book_amount) values ('fruit', '2222-22-22', '22-22', str)";
                    //sqliteDatabase.execSQL(sql);

                    Cursor cursor = sqliteDatabase.query("book", new String[] { "book_type", "book_date", "book_time", "book_amount"},
                            "book_date=?", new String[] { "1111-11-11" }, null, null, null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String str_type = cursor.getString(0);
                        String str_date = cursor.getString(1);
                        String str_time = cursor.getString(2);
                        float str_amount = cursor.getFloat(3);
                        // do something useful with these
                        cursor.moveToNext();
                        Log.d("STATE", str_type + " ," + str_date + " ," + str_time + " ," + str_amount);
                    }
                    cursor.close();

                    // 参数1：（String）表名
                    Toast.makeText(MainActivity.this, "press " + str + "!", Toast.LENGTH_SHORT).show();
                    num_text.setText(null);
                }
                else {
                    Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
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

}
