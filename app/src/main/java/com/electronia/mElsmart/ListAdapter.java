package com.electronia.mElsmart;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.electronia.mElsmart.Common.DatabaseHelper;
import com.electronia.mElsmart.Common.UrlConnection;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.electronia.mElsmart.MainActivity.TAG;

/**
 * Created by Pradeepn on 3/12/2019.
 */

public class ListAdapter extends BaseAdapter {

    public ArrayList<Product> listProducts;
    private Context context;
    Controllerdb db;// = new Controllerdb(context);
    SQLiteDatabase database;
    private static String ServiceURL = "";
    UrlConnection urlconnection;
    DatabaseHelper databasehelper;

    public ListAdapter(Context context,ArrayList<Product> listProducts) {
        this.context = context;
        this.listProducts = listProducts;
        db = new Controllerdb(context);
        GetServiceURL();
        urlconnection = new UrlConnection(context);
        databasehelper=new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return listProducts.size();
    }

    @Override
    public Product getItem(int position) {
        return listProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView
            , ViewGroup parent)
    {
        View row;
        final ListViewHolder listViewHolder;

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.activity_custom_listview,parent,false);

            listViewHolder = new ListViewHolder();
            listViewHolder.tvProductName = (TextView) row.findViewById(R.id.tvProductName);
            listViewHolder.ivProduct = (ImageView) row.findViewById(R.id.ivProduct);
            listViewHolder.imglive = (ImageButton) row.findViewById(R.id.imglive);

            //listViewHolder.tvPrice = row.findViewById(R.id.tvPrice);
           // listViewHolder.btnPlus = (ImageButton) row.findViewById(R.id.ib_addnew);
            //  listViewHolder.edTextQuantity = row.findViewById(R.id.editTextQuantity);
            listViewHolder.btnMinus = (ImageButton) row.findViewById(R.id.ib_remove);
            row.setTag(listViewHolder);

        }
        else
        {
            row=convertView;
            listViewHolder= (ListViewHolder) row.getTag();
        }
        final Product products = getItem(position);

        listViewHolder.tvProductName.setText(products.ProductName);
        byte[] recordImage = products.ProductImage;
        if(  recordImage!=null && recordImage.length>0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);
            if(bitmap!=null)
            listViewHolder.ivProduct.setImageBitmap(bitmap);
            else
                listViewHolder.ivProduct.setImageResource(R.mipmap.pic);
        }

        // listViewHolder.tvPrice.setText(products.ProductPrice+" $");
        //listViewHolder.edTextQuantity.setText(products.CartQuantity+"");


        listViewHolder.ivProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendSavedData(products.ProductPrice);
                byte[] recordImage=  databasehelper.GetEmpPhoto(products.ProductPrice);

                if(  recordImage!=null && recordImage.length>0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);
                    if (bitmap != null)
                        listViewHolder.ivProduct.setImageBitmap(bitmap);
                    else
                        listViewHolder.ivProduct.setImageResource(R.mipmap.pic);
                }

            }
        });

        listViewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapTracking.class);
                Bundle b = new Bundle();
                b.putString("EmpID",String.valueOf( products.ProductPrice));
                intent.putExtras(b);
                context.startActivity(intent);

            }
        });

        listViewHolder.imglive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LocationHistory.class);
                Bundle b = new Bundle();
                b.putString("EmpID",String.valueOf( products.ProductPrice));
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });
        return row;
    }

    private void updateQuantity(int position, EditText edTextQuantity, int value) {
        Product products = getItem(position);
        if(value > 0)
        {
            products.CartQuantity = products.CartQuantity + 1;
        }
        else
        {
            if(products.CartQuantity > 0)
            {
                products.CartQuantity = products.CartQuantity - 1;
            }
        }
        edTextQuantity.setText(products.CartQuantity+"");
    }



    public Integer SendSavedData(Integer EmployeeID)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0, count = 0, Employee_Id, ID;

        Double Longitude = 0.0, Latitude = 0.0;
        String Datetime1;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Tasks where Employee_Id="+EmployeeID , null);
            if (cursor.moveToFirst()) {
                do {
                    byte[] imgByte = cursor.getBlob(cursor.getColumnIndex("pic"));
                    if (imgByte == null || imgByte.length == 0)
                        sendsavedlog(EmployeeID);
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
    public Integer GetServiceURL()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 1;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    ServiceURL = cursor.getString(cursor.getColumnIndex("url"));

                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 0;
        } catch (Exception ex) {
            res = -1;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }



    public byte[] sendsavedlog(int Empid) {


        String s = "";
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        final int IO_BUFFER_SIZE = 64;
        final URL urlObject;
        final URLConnection myConn;
        byte[] data = new byte[8096];
        try {


            if (!(urlconnection.checkConnection())) {
                return null;
            }

            s = "";
            s = ServiceURL + "/ElguardianService/Service1.svc/" + "/GetImage" + "/" + Empid;
          String  EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
            data=    urlconnection.ServerConnection_Pic(EMPLOYEE_SERVICE_URI1);
            UpdateLogData(Empid, data);
        } catch (Exception e) {
            e.printStackTrace();
          //  Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

        } finally {

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return data;

    }


    public Integer UpdateLogData(int ID, byte[] img)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            database = db.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("pic", img);

// updating row
            Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Emp_Photo where Employee_Id = " + ID, null);
            if (cursor.moveToNext()) {
                database.update("Emp_Photo", values, "Employee_Id" + " = ?", new String[]{String.valueOf(ID)});
            } else {
                values.put("Employee_Id", ID);
                database.insert("Emp_Photo", null, values);
                // database.execSQL("update Tasks set pic="+cv+ " where Employee_Id="+ID );
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