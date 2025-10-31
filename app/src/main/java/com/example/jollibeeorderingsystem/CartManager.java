package com.example.jollibeeorderingsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<Integer, OrderItem> cartItems;
    private double totalAmount;
    private static final double VAT_RATE = 0.12; // 12% VAT

    // Private constructor for singleton
    private CartManager() {
        cartItems = new HashMap<>();
        totalAmount = 0.0;
    }

    // Singleton instance getter
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Add item to cart or update quantity if exists
    public void addToCart(int productId, String productName, double price, int quantity) {
        if (cartItems.containsKey(productId)) {
            OrderItem existingItem = cartItems.get(productId);
            existingItem.setProductQuantity(existingItem.getProductQuantity() + quantity);
        } else {
            cartItems.put(productId, new OrderItem(productName, price, quantity));
        }
        calculateTotals();
    }

    // Update specific item quantity
    public void updateQuantity(int productId, int quantity) {
        if (cartItems.containsKey(productId)) {
            if (quantity <= 0) {
                removeFromCart(productId);
            } else {
                cartItems.get(productId).setProductQuantity(quantity);
                calculateTotals();
            }
        }
    }

    // Remove item from cart
    public void removeFromCart(int productId) {
        cartItems.remove(productId);
        calculateTotals();
    }

    // Get specific item from cart
    public OrderItem getCartItem(int productId) {
        return cartItems.get(productId);
    }

    // Calculate all totals
    private void calculateTotals() {
        totalAmount = 0.0;
        for (OrderItem item : cartItems.values()) {
            totalAmount += item.getProductCost() * item.getProductQuantity();
        }
    }

    // Clear entire cart
    public void clearCart() {
        cartItems.clear();
        totalAmount = 0.0;
    }

    // Getters
    public Map<Integer, OrderItem> getCartItems() {
        return cartItems;
    }

    public ArrayList<OrderItem> getCart() {
        return new ArrayList<>(cartItems.values());
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getTotalFoodPrice() {
        return totalAmount;
    }

    public double getVAT() {
        return totalAmount * VAT_RATE;
    }

    public double getGrandTotal() {
        return totalAmount + getVAT();
    }

    public int getTotalItems() {
        int total = 0;
        for (OrderItem item : cartItems.values()) {
            total += item.getProductQuantity();
        }
        return total;
    }

    // Check if cart is empty
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    // Get cart size (number of unique items)
    public int getCartSize() {
        return cartItems.size();
    }
}