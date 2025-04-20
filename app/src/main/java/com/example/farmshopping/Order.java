package com.example.farmshopping;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private int orderNo;
    private List<Product> products;

    public Order(int orderNo, List<Product> products) {
        this.orderNo = orderNo;
        this.products = products;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
