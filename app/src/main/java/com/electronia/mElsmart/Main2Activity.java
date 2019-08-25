package com.electronia.mElsmart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import com.electronia.mElsmart.R;

import java.util.Calendar;

import static com.electronia.mElsmart.MainActivity.TAG;

public class Main2Activity extends AppCompatActivity {
    Controllerdb db = new Controllerdb(this);
    SQLiteDatabase database;
    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private Toolbar toolbar;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public ImageView imageview;
    public TextView BadgeNo,Name,txtLastPunch;




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            toolbar.setTitle(item.getTitle());
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_home:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_Profile:
                    viewPager.setCurrentItem(3);
                    return true;



            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.snack_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_snack_bar:
                showSnackBar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showSnackBar() {
        Snackbar.make(navigation, "Some text", Snackbar.LENGTH_LONG).show();

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.view_pager);
        drawerLayout = findViewById(R.id.drawer_layout);



        navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        imageview=headerView.findViewById(R.id.imageView);
        BadgeNo=headerView.findViewById(R.id.txtBadgeno);
        Name=headerView.findViewById(R.id.txtName);
        txtLastPunch=headerView.findViewById(R.id.txtLastPunch);
        GooglePlusFragmentPageAdapter adapter = new GooglePlusFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bindNavigationDrawer();
        initTitle();
        displayData();
        ReadRegValue();

        byte[] recordImage = GetEmpPhoto();
        if(  recordImage!=null && recordImage.length>0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);
            imageview.setImageBitmap(bitmap);
        }
        else
        {
            imageview.setImageResource(R.mipmap.ic_launcher_round);
        }



        }

    private void initTitle() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(navigation.getMenu().getItem(0).getTitle());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        moveTaskToBack(true);
        finish();
        System.gc();
        System.exit(0);
    }


    private void bindNavigationDrawer() {


        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                displayData();
            }

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                if (id == R.id.nav_tool) {
                    startActivity(new Intent(Main2Activity.this, recordLogs.class));
                    //showToolSnackBar();
                } else if (id == R.id.nav_share) {
                    startActivity(new Intent(Main2Activity.this, setting.class));
                } else if (id == R.id.nav_gallery) {
                    startActivity(new Intent(Main2Activity.this, AboutActivity.class));
                } else if (id == R.id.nav_send) {
                    startActivity(new Intent(Main2Activity.this, Device.class));

                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }


        });
    }

    private void showSendSnackBar() {
        Snackbar.make(navigation, "Send", Snackbar.LENGTH_SHORT).show();

    }

    private void showGallerySnackBar() {
        Snackbar.make(navigation, "Gallery", Snackbar.LENGTH_SHORT).show();

    }

    private void showToolSnackBar() {
        Snackbar.make(navigation, "Tool", Snackbar.LENGTH_SHORT).show();
    }

    private void showShareSnackBar() {
        Snackbar.make(navigation, "Share", Snackbar.LENGTH_SHORT).show();
    }


    private static class GooglePlusFragmentPageAdapter extends FragmentPagerAdapter {


        public GooglePlusFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {

                case 0:
                    return DashboardFragment.newInstance();
                case 1:
                    return Projects.newInstance();
                case 2:
                    return NotificationFragment.newInstance();
                case 3:
                    return RegistrationActivity.newInstance();
                case 4:
                    return Fragment_log.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }


    public byte[] GetEmpPhoto()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;

        Double Longitude=0.0,Latitude=0.0;
        String Datetime1;
        byte[] data=new byte[8096];
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Profile_Photo", null);
            if (cursor.moveToFirst()) {
                do {

                    data=cursor.getBlob(cursor.getColumnIndex("pic"));
                    // if (imgByte == null && imgByte.length == 0);
                    //sendsavedlog(Employee_Id);
                } while (cursor.moveToNext());


            }
            cursor.close();
            res = 1;


        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return data;
    }



    public Integer ReadRegValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT FullName,BadgeNo,Employee_Id  From   Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    BadgeNo.setText(cursor.getString(cursor.getColumnIndex("BadgeNo")));
                    Name.setText(cursor.getString(cursor.getColumnIndex("FullName")));

                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(RegistrationActivity.TAG, ex.getMessage());
        }
        return res;
    }

    private void displayData() {
        String LastPunch="",Time="",NewTime="";
        database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM  tblLogs  order by Id desc LIMIT 1",null);
        if (cursor.moveToFirst()) {
            do {

                LastPunch=cursor.getString(cursor.getColumnIndex("date"));
                Time=cursor.getString(cursor.getColumnIndex("time"));
             //   NewTime =" "+ Time.substring(0,5)+" "+Time.substring(9,11);
                LastPunch+=" "+ Time+",";
                if(cursor.getString(cursor.getColumnIndex("direction")).equals("1"))
                    LastPunch+=getResources().getString(R.string.IN)+",";//"IN";
                else
                    LastPunch+=getResources().getString(R.string.OUT)+",";//" OUT";
                    LastPunch+=" "+cursor.getString(cursor.getColumnIndex("PunchType"));
                //empid.add(cursor.getString(cursor.getColumnIndex("empid")));

                //  BadgeNo.add(cursor.getString(cursor.getColumnIndex("BadgeNo")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        txtLastPunch.setText(LastPunch);
    }




}
