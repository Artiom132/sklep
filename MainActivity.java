package com.example.sklep20;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Spinner spinnerComputer;
    private CheckBox checkboxMonitor, checkboxKeyboard, checkboxMouse;
    private TextView textViewTotalPrice;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        spinnerComputer = findViewById(R.id.spinnerComputer);
        checkboxMonitor = findViewById(R.id.checkboxMonitor);
        checkboxKeyboard = findViewById(R.id.checkboxKeyboard);
        checkboxMouse = findViewById(R.id.checkboxMouse);
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        Button btnSaveOrder = findViewById(R.id.btnSaveOrder);
        Button btnViewOrders = findViewById(R.id.btnViewOrders);

        loadComputers();

        AdapterView.OnItemSelectedListener priceCalculator = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                calculateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerComputer.setOnItemSelectedListener(priceCalculator);
        checkboxMonitor.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotalPrice());
        checkboxKeyboard.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotalPrice());
        checkboxMouse.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotalPrice());


        btnSaveOrder.setOnClickListener(v -> {
            String orderName = generateOrderName();
            if (orderName.isEmpty()) {
                Toast.makeText(this, "Wybierz produkty, aby zapisać zamówienie.", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.insertOrder(orderName, totalPrice);
            Toast.makeText(this, "Zamówienie zapisane!", Toast.LENGTH_SHORT).show();
        });


        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
            startActivity(intent);
        });
    }

    private void loadComputers() {
        List<Product> computers = new ArrayList<>();
        computers.add(new Product(1, "Komputer Intel I72 128GB RAM", 10000.0));
        computers.add(new Product(2, "Komputer AMD RYŻEN 64 7GB RAM", 1500.0));
        computers.add(new Product(3, "Komputer turbo składak 5000", 15000.0));

        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, computers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerComputer.setAdapter(adapter);
    }

    private void calculateTotalPrice() {
        totalPrice = 0.0;

        Product selectedComputer = (Product) spinnerComputer.getSelectedItem();
        if (selectedComputer != null) {
            totalPrice += selectedComputer.getPrice();
        }

        if (checkboxMonitor.isChecked()) totalPrice += 800.0;
        if (checkboxKeyboard.isChecked()) totalPrice += 300.0;
        if (checkboxMouse.isChecked()) totalPrice += 150.0;

        textViewTotalPrice.setText(String.format("Łączna cena: %.2f zł", totalPrice));
    }

    private String generateOrderName() {
        Product selectedComputer = (Product) spinnerComputer.getSelectedItem();
        StringBuilder orderName = new StringBuilder();

        if (selectedComputer != null) {
            orderName.append(selectedComputer.getName()).append(", ");
        }
        if (checkboxMonitor.isChecked()) {
            orderName.append("Monitor Dell, ");
        }
        if (checkboxKeyboard.isChecked()) {
            orderName.append("Klawiatura Razer, ");
        }
        if (checkboxMouse.isChecked()) {
            orderName.append("Mysz Genesis, ");
        }

        if (orderName.length() > 0) {
            orderName.setLength(orderName.length() - 2);
        }

        return orderName.toString();
    }

    private void sendSmsWithOrder() {
        String smsContent = "Zamówienie: " + generateOrderName() + "\nŁączna cena: " + totalPrice + " zł";

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(android.net.Uri.parse("smsto:"));
        smsIntent.putExtra("sms_body", smsContent);

        try {
            startActivity(smsIntent);
            Toast.makeText(this, "Otwieranie aplikacji SMS", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Nie udało się otworzyć aplikacji SMS.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        Log.d("MainActivity", "Menu created successfully!");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_orders:
                Intent intentOrders = new Intent(this, OrdersActivity.class);
                startActivity(intentOrders);
                return true;
            case R.id.menu_about:
                showAuthorInfoDialog();
                return true;
            case R.id.menu_share:
                String shareContent = generateOrderName() + "\nŁączna cena: " + totalPrice + " zł";
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                startActivity(Intent.createChooser(shareIntent, "Udostępnij koszyk"));
                return true;
            case R.id.menu_sms:
                sendSmsWithOrder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAuthorInfoDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Informacje o autorze")
                .setMessage("Autor: Mikołaj, lat 18\nUczeń ZSMEiE")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()) // Przycisk zamykający okno
                .setIcon(android.R.drawable.ic_dialog_info) // Ikona informacyjna
                .show();
    }
}
