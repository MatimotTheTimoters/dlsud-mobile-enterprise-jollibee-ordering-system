package com.example.jollibeeorderingsystem;

public class OrderItem {
    private String productName;
    private double productCost;
    private int productQuantity;

    public OrderItem(String productName, double productCost, int productQuantity) {
        this.productName = productName;
        this.productCost = productCost;
        this.productQuantity = productQuantity;
    }

    // Getters
    public String getProductName() {
        return productName;
    }

    public double getProductCost() {
        return productCost;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public double getTotalPrice() {
        return productCost * productQuantity;
    }

    // Setters
    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public void setProductCost(double productCost) {
        this.productCost = productCost;
    }

    @Override
    public String toString() {
        return productName + " x" + productQuantity + " - ₱" + String.format("%.2f", getTotalPrice());
    }
}