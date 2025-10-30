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

    public String getProductName() {
        return this.productName;
    }

    public double getProductCost() {
        return this.productCost;
    }

    public int getProductQuantity() {
        return this.productQuantity;
    }
}
