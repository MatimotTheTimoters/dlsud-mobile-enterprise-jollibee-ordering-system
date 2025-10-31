package com.example.jollibeeorderingsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "JollibeeDB";
    private static final int DATABASE_VERSION = 1;

    // Users table constants
    private static final String TABLE_USERS = "users";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // Products table constants
    private static final String TABLE_PRODUCTS = "products";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_PRODUCT_PRICE = "product_price";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT,"
                + KEY_USERNAME + " TEXT UNIQUE,"
                + KEY_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create products table
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PRODUCT_NAME + " TEXT UNIQUE,"
                + KEY_PRODUCT_PRICE + " REAL" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    /* ____________________
    Users table methods
    */

    // Register new user
    public boolean registerUser(String firstName, String lastName, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, firstName);
        values.put(KEY_LAST_NAME, lastName);
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Check if username already exists
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_USER_ID};
        String selection = KEY_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Validate login credentials
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_USER_ID};
        String selection = KEY_USERNAME + " = ? AND " + KEY_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Get user's full name (for welcome message)
    public String getUserFullName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_FIRST_NAME, KEY_LAST_NAME};
        String selection = KEY_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        String fullName = "";
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LAST_NAME));
            fullName = firstName + "" + lastName;
        }

        cursor.close();
        db.close();
        return fullName;
    }

    /* ____________________
    Products table methods
    */

    // Adds a new product if it doesn't already exist
    public boolean addProduct(String product_name, double cost) {
        // Use the product name to check existence, as product_id is auto-incremented.
        if (checkProductExistsByName(product_name)) {
            // Product already exists, no need to add again
            return true;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // KEY_PRODUCT_ID is AUTOINCREMENT, so we skip it here.
        values.put(KEY_PRODUCT_NAME, product_name);
        values.put(KEY_PRODUCT_PRICE, cost);

        // Insert the row
        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        // If result is -1, the insertion failed.
        return result != -1;
    }

    // Helper method to check if product already exists
    public boolean checkProductExistsByName(String product_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_PRODUCT_ID};
        String selection = KEY_PRODUCT_NAME + " = ?";
        String[] selectionArgs = {product_name};

        Cursor cursor = db.query(TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Initialize products if not yet in table
    public void initializeProductsIfEmpty() {
        if (isProductsTableEmpty()) {
            // Chicken Products
            addProduct("Chickenjoy Bucket", 299.00);
            addProduct("1-pc Chickenjoy", 89.00);
            addProduct("2-pc Chickenjoy", 159.00);
            addProduct("Spaghetti with Chicken", 129.00);

            // Burger Products
            addProduct("Yumburger", 39.00);
            addProduct("Cheesy Yumburger", 49.00);
            addProduct("Bacon Cheesy Yumburger", 59.00);
            addProduct("Champ Burger", 99.00);

            // Noodle Products
            addProduct("Jolly Spaghetti", 59.00);
            addProduct("Palabok Fiesta", 79.00);
            addProduct("Shanghai Rolls + Noodles", 129.00);
            addProduct("Beefy Macaroni Soup", 89.00);

            // Dessert & Drink Products
            addProduct("Peach Mango Pie", 39.00);
            addProduct("Chocolate Sundae", 29.00);
            addProduct("Pineapple Juice", 25.00);
            addProduct("Iced Tea", 20.00);
        }
    }

    // Add this helper method to check if products table is empty
    public boolean isProductsTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == 0;
    }

    // Gets product ID by passing name (used by CartManager to add products to cart)
    public int getProductIdByName(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_PRODUCT_ID};
        String selection = KEY_PRODUCT_NAME + " = ?";
        String[] selectionArgs = {productName};

        Cursor cursor = db.query(TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        int productId = -1;
        if (cursor.moveToFirst()) {
            productId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRODUCT_ID));
        }
        cursor.close();
        db.close();
        return productId;
    }

    // Get product details by name
    public ArrayList<Object> getProductDetailsByName(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_PRODUCT_ID, KEY_PRODUCT_NAME, KEY_PRODUCT_PRICE};
        String selection = KEY_PRODUCT_NAME + " = ?";
        String[] selectionArgs = {productName};

        Cursor cursor = db.query(TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        ArrayList<Object> productDetails = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRODUCT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRODUCT_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRODUCT_PRICE));

            productDetails.add(id);
            productDetails.add(name);
            productDetails.add(price);
        }
        cursor.close();
        db.close();
        return productDetails;
    }
}