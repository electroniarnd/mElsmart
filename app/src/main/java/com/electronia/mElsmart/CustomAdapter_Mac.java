package com.electronia.mElsmart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Pradeepn on 8/9/2018.
 */

public class CustomAdapter_Mac extends BaseAdapter {
    private Context mContext;
    Controllerdb controldb;
    SQLiteDatabase db;

    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> MacAddress = new ArrayList<String>();
    private ArrayList<String> DeviceName = new ArrayList<String>();
    private ArrayList<String> Delete = new ArrayList<String>();
    public static final String TAG = "ElSmart";
    public CustomAdapter_Mac(Context  context,ArrayList<String> Id, ArrayList<String> MacAddress,ArrayList<String> DeviceName,ArrayList<String> Delete)
    {
        this.mContext = context;
        this.Id = Id;
        this.MacAddress = MacAddress;
        this.DeviceName=DeviceName;
        this.Delete=Delete;
    }
    @Override
    public int getCount() {
        return Id.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CustomAdapter_Mac.viewHolder holder;
        controldb =new Controllerdb(mContext);
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_mac, null);
            holder = new CustomAdapter_Mac.viewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.tvid);
            holder.MacAddress = (TextView) convertView.findViewById(R.id.MacAdress);
            holder.DeviceName = (TextView) convertView.findViewById(R.id.DeviceName);
            holder.Delete = (Button) convertView.findViewById(R.id.delete_btn);
            convertView.setTag(holder);
        } else {
            holder = (CustomAdapter_Mac.viewHolder) convertView.getTag();
        }
        holder.id.setText(Id.get(position));
        holder.MacAddress.setText(MacAddress.get(position));
        holder.DeviceName.setText(DeviceName.get(position));
        holder.Delete.setText("");
       // holder.Delete.set(Delete.get(position));



        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Integer res=0;
               // lstvw.remove(Delete.get(position)); //or some other task
               // notifyDataSetChanged();
                try {
                    db = controldb.getWritableDatabase();
                    db.execSQL("Delete from Device where ID= "+ Delete.get(position));// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
                    res=1;
                }
                catch (Exception ex) {
                    res=0;
                    Log.d(TAG, ex.getMessage());
                }
                if(res>0)
                {
                    Toast.makeText(mContext, "Data deleted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(mContext,
                            "Fail to Delete", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(mContext, "Data deleted1"+holder.Delete.getText()+Delete.get(position), Toast.LENGTH_SHORT).show();

            }
        });

        return convertView;
    }
    public class viewHolder {
        TextView id;
        TextView MacAddress;
        TextView DeviceName;
        Button Delete;
    }






}