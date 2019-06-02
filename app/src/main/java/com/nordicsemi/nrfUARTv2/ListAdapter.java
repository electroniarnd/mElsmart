package com.nordicsemi.nrfUARTv2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.widget.Toast.*;

/**
 * Created by Pradeepn on 3/12/2019.
 */

public class ListAdapter extends BaseAdapter {

    public ArrayList<Product> listProducts;
    private Context context;

    public ListAdapter(Context context,ArrayList<Product> listProducts) {
        this.context = context;
        this.listProducts = listProducts;
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
            listViewHolder.btnPlus = (ImageButton) row.findViewById(R.id.ib_addnew);
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

            listViewHolder.ivProduct.setImageBitmap(bitmap);
        }
        else
            listViewHolder.ivProduct.setImageResource(R.mipmap.pic);
        // listViewHolder.tvPrice.setText(products.ProductPrice+" $");
        //listViewHolder.edTextQuantity.setText(products.CartQuantity+"");

        listViewHolder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context,Projects.class);
                Bundle b = new Bundle();
                b.putString("EmpID",String.valueOf( products.ProductPrice));
                intent.putExtras(b);
                context.startActivity(intent);
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
}