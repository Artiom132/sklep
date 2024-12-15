package com.example.sklep20;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView listViewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        dbHelper = new DatabaseHelper(this);
        listViewOrders = findViewById(R.id.listViewOrders);
        Button btnBackToMain = findViewById(R.id.btnBackToMain);

        loadOrders();

        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(OrdersActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadOrders() {
        List<Order> orders = dbHelper.getAllOrders();

        if (orders.isEmpty()) {
            orders.add(new Order(0, "Brak zamówień", 0.0));
        }

        ArrayAdapter<Order> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, orders);
        listViewOrders.setAdapter(adapter);
    }
}
