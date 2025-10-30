package com.example.jollibeeorderingsystem;

import com.example.jollibeeorderingsystem.OrderItem;
import java.util.ArrayList;

public class CartManager {
    public static ArrayList<OrderItem> cart;
    public static double totalAmount, totalFoodPrice, vat;

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getTotalFoodPrice() {
        return totalFoodPrice;
    }

    public double getVAT() {
        return vat;
    }
}
