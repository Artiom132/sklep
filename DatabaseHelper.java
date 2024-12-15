package com.example.sklep20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "orders.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";

    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "id";
    private static final String COLUMN_ORDER_NAME = "name";
    private static final String COLUMN_ORDER_PRICE = "price";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PRICE + " REAL)";
        db.execSQL(createProductsTable);

        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_NAME + " TEXT, " +
                COLUMN_ORDER_PRICE + " REAL)";
        db.execSQL(createOrdersTable);

        insertSampleProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    private void insertSampleProducts(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "Komputer");
        values.put(COLUMN_PRICE, 2000.0);
        db.insert(TABLE_PRODUCTS, null, values);

        values.put(COLUMN_NAME, "Klawiatura");
        values.put(COLUMN_PRICE, 100.0);
        db.insert(TABLE_PRODUCTS, null, values);

        values.put(COLUMN_NAME, "Mysz");
        values.put(COLUMN_PRICE, 50.0);
        db.insert(TABLE_PRODUCTS, null, values);

        values.put(COLUMN_NAME, "Monitor");
        values.put(COLUMN_PRICE, 300.0);
        db.insert(TABLE_PRODUCTS, null, values);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                products.add(new Product(id, name, price));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return products;
    }

    public void insertOrder(String name, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_NAME, name);
        values.put(COLUMN_ORDER_PRICE, price);
        db.insert(TABLE_ORDERS, null, values);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ORDERS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_PRICE));
                orders.add(new Order(id, name, price));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return orders;
    }
}
