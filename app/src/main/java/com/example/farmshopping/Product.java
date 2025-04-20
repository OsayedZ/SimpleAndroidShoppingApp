package com.example.farmshopping;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String category;
    private double price;
    private int quantity;
    private boolean isOrganic;

    public Product(String name, String category, double price, int quantity, 
                  boolean isOrganic) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.isOrganic = isOrganic;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isOrganic() {
        return isOrganic;
    }

    public void setOrganic(boolean organic) {
        isOrganic = organic;
    }


} 