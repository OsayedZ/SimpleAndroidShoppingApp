package com.example.farmshopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private CartAdapter cartAdapter;
    private List<Product> cartItems;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        checkoutButton = findViewById(R.id.checkoutButton);

        // Setup shared preferences
        sharedPreferences = getSharedPreferences("FarmShopping", MODE_PRIVATE);

        // Load cart items
        loadCartItems();

        // Setup cart list
        setupCartList();

        // Setup click listeners
        setupClickListeners();
    }

    private void loadCartItems() {
        String cartJson = sharedPreferences.getString("cart", "[]");
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        cartItems = new Gson().fromJson(cartJson, type);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        updateTotalPrice();
    }

    private void setupCartList() {
        cartAdapter = new CartAdapter(cartItems, this);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        checkoutButton.setOnClickListener(v -> checkout());
    }

    public void removeFromCart(Product product) {
        cartItems.remove(product);
        saveCartItems();
        updateTotalPrice();
        cartAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load current products from SharedPreferences
        String productsJson = sharedPreferences.getString("products", "[]");
        Type productType = new TypeToken<ArrayList<Product>>(){}.getType();
        List<Product> products = new Gson().fromJson(productsJson, productType);
        if (products == null) {
            products = new ArrayList<>();
        }

        // Reduce quantities and update product list
        for (Product cartItem : cartItems) {
            for (Product product : products) {
                if (product.getName().equals(cartItem.getName())) {
                    product.setQuantity(product.getQuantity() - 1);
                    break;
                }
            }
        }

        // Save updated products back to SharedPreferences
        String updatedProductsJson = new Gson().toJson(products);
        sharedPreferences.edit().putString("products", updatedProductsJson).apply();

        // Load current orders from SharedPreferences
        String ordersJson = sharedPreferences.getString("orders", "[]");
        Type orderType = new TypeToken<ArrayList<Order>>(){}.getType();
        List<Order> orders = new Gson().fromJson(ordersJson, orderType);
        if (orders == null) {
            orders = new ArrayList<>();
        }

        int maxOrderNo = 0;
        for (Order o : orders) {
            if(o.getOrderNo() > maxOrderNo)
                maxOrderNo = o.getOrderNo();
        }
        Order order = new Order(maxOrderNo + 1, cartItems);

        orders.add(order);

        String updatedOrdersJson = new Gson().toJson(orders);
        sharedPreferences.edit().putString("orders", updatedOrdersJson).apply();


        // Clear cart
        cartItems.clear();
        saveCartItems();
        updateTotalPrice();
        cartAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Order placed successfully, order no: " + (maxOrderNo + 1), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateTotalPrice() {
        double total = 0;
        for (Product product : cartItems) {
            total += product.getPrice();
        }
        totalPriceTextView.setText(String.format("Total: $%.2f", total));
    }

    private void saveCartItems() {
        String cartJson = new Gson().toJson(cartItems);
        sharedPreferences.edit().putString("cart", cartJson).apply();
    }
} 