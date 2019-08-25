package com.electronia.mElsmart;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Pradeepn on 3/12/2019.
 */

public class Summary extends AppCompatActivity {

    ListView lvSummary;
    TextView tvTotal;
    Double Total = 0d;
    ArrayList<Product> productOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        lvSummary = (ListView) findViewById(R.id.lvSummary);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        getOrderItemData();
    }

    private void getOrderItemData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String orderItems = extras.getString("orderItems", null);
            if (orderItems != null && orderItems.length() > 0) {
                try {
                    JSONArray jsonOrderItems = new JSONArray(orderItems);
                    for (int i = 0; i < jsonOrderItems.length(); i++) {


                    }

                    if (productOrders.size() > 0) {
                        ListAdapter listAdapter = new ListAdapter(this, productOrders);
                        lvSummary.setAdapter(listAdapter);
                        tvTotal.setText("Order Total: " + Total);
                    } else {
                        showMessage("Empty");
                    }
                } catch (Exception e) {
                    showMessage(e.toString());
                }
            }

        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
