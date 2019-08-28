package com.electronia.mElsmart;

import android.app.ActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.electronia.mElsmart.Services.AutoRegistration;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.electronia.mElsmart.MainActivity.TAG;

public class DashboardFragment extends Fragment {


    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }
    TabLayout tabLayout;
    private static int BLE = 0;
    private static int QRCode = 0;
    private static int Geofence = 0,Employee_Id=0,GeoQR=0;
    private static String msg = "",url="";
    Controllerdb db;
    SQLiteDatabase database;
    private int[] imageResId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        //GooglePlusFragmentPageAdapter adapter = new GooglePlusFragmentPageAdapter(getChildFragmentManager());
        //  viewPager.setAdapter(adapter);
        // viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        db = new Controllerdb(getContext());
        //  tabLayout = view.findViewById(R.id.tabs);
        //  tabLayout.setupWithViewPager(viewPager);
        //  setupTabIcons();
        ReadSystemValue();


        // Setting ViewPager for each Tabs

        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
// configure icons
        boolean flag1=true,flag2=true,flag3=true,flag4=true;
        for (int i = 0 ; i <tabs.getTabCount(); i++) {
            if (BLE == 1 && flag1) {
                tabs.getTabAt(i).setIcon(R.drawable.ble);
                flag1=false;
                continue;
            }
            if (Geofence == 1 && flag2) {
                tabs.getTabAt(i).setIcon(R.drawable.geo);
                flag2=false;
                continue;
            }
            if (QRCode == 1 && flag3) {
                tabs.getTabAt(i).setIcon(R.drawable.qr);
                flag3=false;
                continue;
            }
            if (GeoQR == 1 && flag4) {
                tabs.getTabAt(i).setIcon(R.drawable.geoqr);
                flag4=false;
                continue;
            }
             if(i==(tabs.getTabCount()-1))
                tabs.getTabAt(i).setIcon(R.drawable.mp);






        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ble);
        tabLayout.getTabAt(1).setIcon(R.drawable.geo);
        tabLayout.getTabAt(2).setIcon(R.drawable.qr);
        tabLayout.getTabAt(3).setIcon(R.drawable.geoqr);
        tabLayout.getTabAt(4).setIcon(R.drawable.mp);
    }



    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    private class GooglePlusFragmentPageAdapter extends FragmentPagerAdapter {

        public GooglePlusFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return MainActivity.newInstance();
                case 1:
               return markattendanceActivity.newInstance();
                case 2:
                    return barcodemain.newInstance();
                case 3:
                    return Geo_QR.newInstance();
                case 4:
                    return Fragment_log.newInstance();
                //case 5:
                   // return Fragment_log.newInstance();
            }
            return null;


            //return DashboardChildFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return  getResources().getString(R.string.BLE);
                case 1:
                    return  getResources().getString(R.string.Geo);
                case 2:
                    return  getResources().getString(R.string.QR);
                case 3:
                    return  getResources().getString(R.string.Geo_QR);
                case 4:
                    return  getResources().getString(R.string.MP);


            }
            return getResources().getString(R.string.NA);
        }
    }

    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getChildFragmentManager());
        if(BLE==1)
        adapter.addFragment(new MainActivity(), getResources().getString(R.string.BLE));
        if(Geofence==1)
        adapter.addFragment(new markattendanceActivity(),  getResources().getString(R.string.Geo));
        if(QRCode==1)
        adapter.addFragment(new barcodemain(), getResources().getString(R.string.QR));
        if(GeoQR==1)
        adapter.addFragment(new Geo_QR(), getResources().getString(R.string.QR_Geo));
        adapter.addFragment(new Fragment_log(), getResources().getString(R.string.MR));
        viewPager.setAdapter(adapter);



    }


    public Integer ReadSystemValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT BLE,Geofence,QRCode,Employee_Id,BadgeNo,url,GeoQR  From   Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    BLE = Integer.valueOf(cursor.getString(cursor.getColumnIndex("BLE")));
                    Geofence = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
                    QRCode = Integer.valueOf(cursor.getString(cursor.getColumnIndex("QRCode")));
                    Employee_Id= Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    GeoQR =  Integer.valueOf(cursor.getString(cursor.getColumnIndex("GeoQR")));
                    url  =   cursor.getString(cursor.getColumnIndex("url"));

                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }


}
