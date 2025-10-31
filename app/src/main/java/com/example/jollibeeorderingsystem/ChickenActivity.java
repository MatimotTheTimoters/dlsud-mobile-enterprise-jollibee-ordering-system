package com.example.jollibeeorderingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ChickenActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private CartManager cartManager;
    private TextView tvTotalItems, tvTotalCost;
    private com.google.android.material.button.MaterialButton btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chicken);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get CartManager instance
        cartManager = CartManager.getInstance();

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get IDs
        initializeViews();

        // Set onClick event listeners
        setupButtonClickListeners();
        setupCheckoutButton();
        updateCartDisplay();
    }

    private void initializeViews() {
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    private void setupButtonClickListeners() {
        // Chickenjoy Bucket
        findViewById(R.id.btnAddToCartBucket).setOnClickListener(v -> btnAddToCartOnClick("Chickenjoy Bucket", R.id.etQuantityBucket));

        // 1-pc Chickenjoy
        findViewById(R.id.btnAddToCart1pc).setOnClickListener(v -> btnAddToCartOnClick("1-pc Chickenjoy", R.id.etQuantity1pc));

        // 2-pc Chickenjoy
        findViewById(R.id.btnAddToCart2pc).setOnClickListener(v -> btnAddToCartOnClick("2-pc Chickenjoy", R.id.etQuantity2pc));

        // Spaghetti with Chicken
        findViewById(R.id.btnAddToCartSpaghetti).setOnClickListener(v -> btnAddToCartOnClick("Spaghetti with Chicken", R.id.etQuantitySpaghetti));

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // btnAddToCartOnClick
    private void btnAddToCartOnClick(String productName, int quantityEditTextId) {
        // Get value from product quantity's EditText value
        EditText etQuantity = findViewById(quantityEditTextId);
        int quantity;
        try {
            quantity = Integer.parseInt(etQuantity.getText().toString());
            if (quantity <= 0) {
                Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get product values from database's products table
        ArrayList<Object> productDetails = databaseHelper.getProductDetailsByName(productName);

        if (productDetails.isEmpty()) {
            Toast.makeText(this, "Product not found in database", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract product details from ArrayList
        int productId = (int) productDetails.get(0);
        String name = (String) productDetails.get(1);
        double price = (double) productDetails.get(2);

        // Check if item already exists in cart
        if (cartManager.getCartItem(productId) != null) {
            // Item exists: Update quantity
            cartManager.updateQuantity(productId, cartManager.getCartItem(productId).getProductQuantity() + quantity);
            Toast.makeText(this, "Quantity updated for " + name + ", no new item added", Toast.LENGTH_SHORT).show();
        } else {
            // Item does not exist: Create CartItem obj and add item to cart
            cartManager.addToCart(productId, name, price, quantity);
            Toast.makeText(this, name + " added to cart", Toast.LENGTH_SHORT).show();
        }

        // Update cart display
        updateCartDisplay();

        // Reset quantity to 1
        etQuantity.setText("1");
    }

    private void setupCheckoutButton() {
        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getTotalItems() > 0) {
                startActivity(new Intent(this, CheckoutActivity.class));
            } else {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartDisplay() {
        CartManager cart = CartManager.getInstance();
        int totalItems = cart.getTotalItems();
        double totalCost = cart.getGrandTotal();

        tvTotalItems.setText(String.valueOf(totalItems));
        tvTotalCost.setText(String.format("₱%.2f", totalCost));

        // Enable/disable checkout button based on cart contents
        btnCheckout.setEnabled(totalItems > 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartDisplay();
    }
}