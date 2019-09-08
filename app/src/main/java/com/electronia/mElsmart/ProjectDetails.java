package com.electronia.mElsmart;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ProjectDetails  extends  TabActivity
{
    String TaskId="";
    String[] TaskValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_tab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
       // mTitle.setText(getResources().getString(R.string.title_task));
        toolbar.setTitle(getResources().getString(R.string.Task));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp); // your drawable
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();// Implemented by activity
            }
        });


        Intent intent = getIntent();
        TaskId = intent.getStringExtra("TaskID");
        TaskValue = TaskId.split(",");
        TabHost tabHost = getTabHost();

        // Tab for Photos
        TabHost.TabSpec photospec = tabHost.newTabSpec(getResources().getString(R.string.Task_Details));
        // setting Title and Icon for the Tab
        photospec.setIndicator(getResources().getString(R.string.Task_Details), getResources().getDrawable(R.drawable.photos_gray));
        Intent photosIntent = new Intent(this, Task.class);
        photosIntent.putExtra("TaskID1",TaskId);
        photosIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        photospec.setContent(photosIntent);

        // Tab for Songs
        TabHost.TabSpec songspec = tabHost.newTabSpec(getResources().getString(R.string.Task_History));
        songspec.setIndicator(getResources().getString(R.string.Task_History), getResources().getDrawable(R.drawable.photos_gray));
        Intent songsIntent = new Intent(this, TaskHistory.class);
        songsIntent.putExtra("TaskID1",TaskId);
        songsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        songspec.setContent(songsIntent);

        // Tab for Videos
        TabHost.TabSpec videospec = tabHost.newTabSpec(getResources().getString(R.string.Task_on_Map));
        videospec.setIndicator(getResources().getString(R.string.Task_on_Map), getResources().getDrawable(R.drawable.photos_gray));
        Intent videosIntent = new Intent(this, TaskOnMap.class);
        videosIntent.putExtra("TaskID1",TaskId);
        videosIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        videospec.setContent(videosIntent);

        TabHost.TabSpec Task_Employee = tabHost.newTabSpec(getResources().getString(R.string.Assigned_Employee));
        Task_Employee.setIndicator(getResources().getString(R.string.Assigned_Employee), getResources().getDrawable(R.drawable.photos_gray));
        Intent TaskEmp = new Intent(this, Tracking.class);
        TaskEmp.putExtra("TaskID1",TaskId);
        TaskEmp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Task_Employee.setContent(TaskEmp);

        // Adding all TabSpec to TabHost
        tabHost.addTab(photospec); // Adding photos tab
        tabHost.addTab(songspec); // Adding songs tab
        tabHost.addTab(videospec); // Adding videos tab
        tabHost.addTab(Task_Employee);
    }

}
