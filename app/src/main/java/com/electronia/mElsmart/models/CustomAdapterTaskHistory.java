package com.electronia.mElsmart.models;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.electronia.mElsmart.R;

import java.util.ArrayList;

/**
 * Created by Pradeepn on 4/2/2019.
 */

public class CustomAdapterTaskHistory extends ArrayAdapter<DataModelTaskHistory> implements View.OnClickListener{

    private ArrayList<DataModelTaskHistory> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtPlace;
        TextView txtStaus;
        TextView txtDatetime;
        TextView BadgeNo;
    }



    public CustomAdapterTaskHistory(ArrayList<DataModelTaskHistory> data, Context context) {
        super(context, R.layout.row_task_history, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModelTaskHistory dataModel=(DataModelTaskHistory)object;




        switch (v.getId())
        {

            case R.id.txtPlace:

                Snackbar.make(v, "Release date " +dataModel.getPlace(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();

                break;


        }


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModelTaskHistory dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        CustomAdapterTaskHistory.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new CustomAdapterTaskHistory.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_task_history, parent, false);



            viewHolder.txtPlace =    (TextView) convertView.findViewById(R.id.txtPlace);
            viewHolder.txtStaus =    (TextView) convertView.findViewById(R.id.txtStatusTask);
            viewHolder.txtDatetime = (TextView) convertView.findViewById(R.id.txtDatetimeTask);
            viewHolder.BadgeNo = (TextView) convertView.findViewById(R.id.txtBadgeNoTask);


            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomAdapterTaskHistory.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.txtPlace.setText(dataModel.getPlace());
        viewHolder.txtStaus.setText(dataModel.getstatus());
        viewHolder.txtDatetime.setText(dataModel.getdatetime());
        viewHolder.BadgeNo.setText(dataModel.getBadgeNo());
        return convertView;
    }


}
