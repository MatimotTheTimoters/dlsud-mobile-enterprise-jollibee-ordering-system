package com.example.jollibeeorderingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private CartManager cartManager;
    private TextView tvTotalFoodPrice, tvVAT, tvTotalAmount;
    private LinearLayout linearLayoutOrderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get CartManager instance
        cartManager = CartManager.getInstance();

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();

        // Display values to TextView views
        displayCartValues();

        // Display order items
        displayOrderItems();

        // Set up button click listeners
        setupButtonClickListeners();
    }

    private void initializeViews() {
        tvTotalFoodPrice = findViewById(R.id.tvTotalFoodPrice);
        tvVAT = findViewById(R.id.tvVAT);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        linearLayoutOrderItems = findViewById(R.id.linearLayoutOrderItems);
    }

    private void displayCartValues() {
        // Get values from CartManager instance
        double totalFoodPrice = cartManager.getTotalFoodPrice();
        double vat = cartManager.getVAT();
        double totalAmount = cartManager.getTotalAmount();

        // Display values to TextView views
        tvTotalFoodPrice.setText(String.format("₱%.2f", totalFoodPrice));
        tvVAT.setText(String.format("₱%.2f", vat));
        tvTotalAmount.setText(String.format("₱%.2f", totalAmount));
    }

    private void displayOrderItems() {
        linearLayoutOrderItems.removeAllViews();

        Map<Integer, OrderItem> cartItems = cartManager.getCartItems();
        for (OrderItem item : cartItems.values()) {
            // Create a view for each order item
            View itemView = getLayoutInflater().inflate(R.layout.item_order_product, linearLayoutOrderItems, false);

            TextView tvProductName = itemView.findViewById(R.id.tvProductName);
            TextView tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            TextView tvProductPrice = itemView.findViewById(R.id.tvProductPrice);

            tvProductName.setText(item.getProductName());
            tvProductQuantity.setText("Qty: " + item.getProductQuantity());
            tvProductPrice.setText(String.format("₱%.2f", item.getTotalPrice()));

            linearLayoutOrderItems.addView(itemView);
        }
    }

    private void setupButtonClickListeners() {
        // btnConfirmOnClick
        findViewById(R.id.btnConfirmPurchase).setOnClickListener(v -> btnConfirmOnClick());

        // btnBackOnClick
        findViewById(R.id.btnBack).setOnClickListener(v -> btnBackOnClick());
    }

    private void btnConfirmOnClick() {
        if (cartManager.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get values from CartManager instance
        double totalFoodPrice = cartManager.getTotalFoodPrice();
        double vat = cartManager.getVAT();
        double totalAmount = cartManager.getTotalAmount();

        // Create new Order and insert to Order table
        long orderId = databaseHelper.createOrder(totalFoodPrice, vat, totalAmount);

        if (orderId == -1) {
            Toast.makeText(this, "Failed to create order!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Iterate over cart object and insert each item to Order_Items table
        Map<Integer, OrderItem> cartItems = cartManager.getCartItems();
        boolean allItemsAdded = true;

        for (Map.Entry<Integer, OrderItem> entry : cartItems.entrySet()) {
            int productId = entry.getKey();
            OrderItem item = entry.getValue();

            boolean itemAdded = databaseHelper.addOrderItem(
                    orderId,
                    productId,
                    item.getProductQuantity(),
                    item.getProductCost()
            );

            if (!itemAdded) {
                allItemsAdded = false;
            }
        }

        if (allItemsAdded) {
            // Show success toast
            Toast.makeText(this, "Order confirmed successfully! Order ID: " + orderId, Toast.LENGTH_LONG).show();

            // Clear cart after successful order
            cartManager.clearCart();

            // Return to TransactionActivity
            Intent intent = new Intent(this, TransactionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Some items failed to process!", Toast.LENGTH_SHORT).show();
        }
    }

    private void btnBackOnClick() {
        // Return to ActivityTransaction
        Intent intent = new Intent(CheckoutActivity.this, TransactionActivity.class);
        startActivity(intent);
        finish();
    }
}