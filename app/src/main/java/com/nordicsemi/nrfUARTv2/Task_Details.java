package com.nordicsemi.nrfUARTv2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.nordicsemi.nrfUARTv2.MainActivity.TAG;

public class Task_Details extends Activity {
 Button TaskDetails,TaskHistory,TaskLive;
 String TaskId="";
 TextView txtProject,txtTask;
 String Project_Name ="",Task_Name="";

    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    String[] TaskValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task__details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtProject=(TextView) findViewById(R.id.txtProject);
        txtTask=(TextView) findViewById(R.id.txtTask);
        Intent intent = getIntent();
        TaskId = intent.getStringExtra("TaskID");
        TaskValue = TaskId.split(",");
        ReadTask();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String empid=TaskValue[1];
                Bundle sendBundle = new Bundle();
                sendBundle.putString("EmpID",empid);
                Intent intent = new Intent(Task_Details.this, Projects.class);
                intent.putExtras(sendBundle);;
                startActivity(intent);
                finish();
            }
        });
        TaskDetails = (Button) findViewById(R.id.btntaskDetails);
        TaskHistory = (Button) findViewById(R.id.btntaskHistory);
        TaskLive = (Button) findViewById(R.id.btntaskOnMap);


        TaskDetails.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Task.class);
                intent.putExtra("TaskID", TaskId);
                startActivity(intent);
            }
        });



        TaskHistory.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Task_Details.this, TaskHistory.class);
                intent.putExtra("TaskID",TaskId); // getText() SHOULD NOT be static!!!
                startActivity(intent);
            }
        });


        TaskLive.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Task_Details.this, TaskOnMap.class);
                intent.putExtra("TaskID",TaskId); // getText() SHOULD NOT be static!!!
                startActivity(intent);

            }
        });
    }
    @Override
    public void onBackPressed() {

        String empid=TaskValue[1];
        Bundle sendBundle = new Bundle();
        sendBundle.putString("EmpID",empid);
        Intent intent = new Intent(Task_Details.this, Projects.class);
        intent.putExtras(sendBundle);;
        startActivity(intent);
        finish();

    }

    public Integer ReadTask()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Project_Name,Task_Name FROM  Tasks where Task_Id="+TaskValue[0], null);
            if (cursor.moveToFirst()) {
                do {
                    Project_Name =   cursor.getString(cursor.getColumnIndex("Project_Name"));
                    Task_Name=       cursor.getString(cursor.getColumnIndex("Task_Name"));

                } while (cursor.moveToNext());
            }
            cursor.close();
            txtProject.setText(Project_Name);
            txtTask.setText(Task_Name);
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }
}
