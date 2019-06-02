package com.nordicsemi.nrfUARTv2;

/**
 * Created by Pradeepn on 6/21/2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    Controllerdb controldb;
    SQLiteDatabase db;
    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> time1 = new ArrayList<String>();
  //  private ArrayList<String> Name = new ArrayList<String>();
    private ArrayList<String> Ter = new ArrayList<String>();
    private ArrayList<String> direction = new ArrayList<String>();
    private ArrayList<String> name = new ArrayList<String>();
 //   private ArrayList<String> badgeno = new ArrayList<String>();


   // private ArrayList<String> empid = new ArrayList<String>();
    public CustomAdapter(Context  context,ArrayList<String> Id, ArrayList<String> date,ArrayList<String> time
            ,ArrayList<String> Ter ,ArrayList<String> direction,ArrayList<String> name)
    {
        this.mContext = context;
        this.Id = Id;
        this.name = name;

        this.date=date;
        this.time1=time;
        this.Ter=Ter;
        this.direction=direction;

     //   this.badgeno = badgeno;
      //  this.empid=empid;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final    viewHolder holder;
        controldb =new Controllerdb(mContext);
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout, null);
            holder = new viewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.tvid);
           // holder.name = (TextView) convertView.findViewById(R.id.tvname);
            holder.date = (TextView) convertView.findViewById(R.id.date1);
            holder.time = (TextView) convertView.findViewById(R.id.time1);
            holder.Ter = (TextView) convertView.findViewById(R.id.terid);
          //  holder.empid = (TextView) convertView.findViewById(R.id.empid);
            holder.direction = (TextView) convertView.findViewById(R.id.dir);
            holder.name= (TextView) convertView.findViewById(R.id.txtname);

            //  holder.badgeno = (TextView) convertView.findViewById(R.id.badgeno);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        holder.id.setText(Id.get(position));
       // holder.name.setText(Name.get(position));

        holder.date.setText(date.get(position));
        holder.time.setText(time1.get(position));
        holder.Ter.setText(Ter.get(position));
        //holder.empid.setText(empid.get(position));
        holder.direction.setText(direction.get(position));
        holder.name.setText(name.get(position));
       // holder.badgeno.setText(badgeno.get(position));
        return convertView;
    }
    public class viewHolder {
        TextView id;
       // TextView name;

        TextView date;
        TextView time;
        TextView Ter;
       // TextView empid;
        TextView direction;
        TextView name;
      //  TextView badgeno;

    }
}
