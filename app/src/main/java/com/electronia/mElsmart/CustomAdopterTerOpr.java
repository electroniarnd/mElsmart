package com.electronia.mElsmart;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdopterTerOpr extends BaseAdapter {
    private Context mContext;
    Controllerdb controldb;
    SQLiteDatabase db;
    private ArrayList<String> TerminalID = new ArrayList<String>();
    private ArrayList<String> Site_No = new ArrayList<String>();
    private ArrayList<String> TerNo = new ArrayList<String>();
    private ArrayList<String> Terminal_Name = new ArrayList<String>();
    private ArrayList<String> IPAddress = new ArrayList<String>();


    public CustomAdopterTerOpr(Context  context, ArrayList<String> TerminalID, ArrayList<String> Site_No, ArrayList<String> TerNo
            , ArrayList<String> Terminal_Name , ArrayList<String> IPAddress)
    {
        this.mContext =     context;
        this.TerminalID = TerminalID;
        this.Site_No = Site_No;
        this.TerNo=TerNo;
        this.Terminal_Name=Terminal_Name;
        this.IPAddress=IPAddress;
    }
    @Override
    public int getCount() {
        return TerminalID.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final CustomAdopterTerOpr.viewHolder holder;
        controldb =new Controllerdb(mContext);
        LayoutInflater layoutInflater;
        TextView Terminalname;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layouterpr, null);
            holder = new CustomAdopterTerOpr.viewHolder();
            holder.TerminalID = (TextView) convertView.findViewById(R.id.TerminalID);
            holder.Site_No = (TextView) convertView.findViewById(R.id.SiteNo);
            holder.TerNo = (TextView) convertView.findViewById(R.id.TerNo);
            Terminalname=(TextView) convertView.findViewById(R.id.TerminalName);
            Terminalname.setPaintFlags(Terminalname.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            Terminalname.setTextColor(Color.BLUE);
            holder.Terminal_Name = Terminalname;
            holder.IPAddress = (TextView) convertView.findViewById(R.id.IPAddress);
            convertView.setTag(holder);
        } else {
            holder = (CustomAdopterTerOpr.viewHolder) convertView.getTag();
        }
        holder.TerminalID.setText(TerminalID.get(position));
        holder.Site_No.setText(Site_No.get(position));
        holder.TerNo.setText(TerNo.get(position));
        holder.Terminal_Name.setText(Terminal_Name.get(position));
        holder.IPAddress.setText(IPAddress.get(position));

        holder.Terminal_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Terminal_Control.class);

                Bundle b = new Bundle();
                b.putString("Terminal",String.valueOf( holder.TerminalID.getText())+","+holder.Site_No.getText()+","+holder.TerNo.getText()+","+ holder.IPAddress.getText());
                intent.putExtras(b);
                mContext.startActivity(intent);

            }
        });

        return convertView;
    }
    public class viewHolder {
        TextView TerminalID;
        TextView Site_No;
        TextView TerNo;
        TextView Terminal_Name;
        TextView IPAddress;
    }
}

