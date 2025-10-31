package com.example.jollibeeorderingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TransactionActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView tvTotalItems, tvTotalCost;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Populate Products table if empty
        databaseHelper.initializeProductsIfEmpty();

        // Initialize cart (singleton automatically handles this)
        // CartManager.getInstance() is ready to use

        initializeViews();
        setupCategoryClicks();
        setupCheckoutButton();
        updateCartDisplay();
    }

    private void initializeViews() {
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    private void setupCategoryClicks() {
        CardView cardChicken = findViewById(R.id.cardChicken);
        CardView cardBurgers = findViewById(R.id.cardBurgers);
        CardView cardNoodles = findViewById(R.id.cardNoodles);
        CardView cardDesserts = findViewById(R.id.cardDesserts);

        cardChicken.setOnClickListener(v ->
                startActivity(new Intent(this, ChickenActivity.class)));

        cardBurgers.setOnClickListener(v ->
                startActivity(new Intent(this, BurgersActivity.class)));

        cardNoodles.setOnClickListener(v ->
                startActivity(new Intent(this, NoodlesActivity.class)));

        cardDesserts.setOnClickListener(v ->
                startActivity(new Intent(this, DessertDrinksActivity.class)));
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

    @Override
    protected void onResume() {
        super.onResume();
        updateCartDisplay();
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
}