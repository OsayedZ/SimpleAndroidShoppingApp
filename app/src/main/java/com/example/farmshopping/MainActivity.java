package com.example.farmshopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Spinner categorySpinner;
    private CheckBox organicCheckBox;
    private Switch outOfStockSwitch;
    private Button searchButton;
    private Button cartButton;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> products;
    private List<Product> cartItems;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();
        
        // Setup shared preferences
        sharedPreferences = getSharedPreferences("FarmShopping", MODE_PRIVATE);
        

        // Setup category spinner
        setupCategorySpinner();

        
        // Setup click listeners
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Setup product list
        setupProductList();
        // Load saved cart items
        loadCartItems();
        
        // Update the adapter with cart items
        if (productAdapter != null) {
            productAdapter.updateCartItems(cartItems);
        }
    }

    private void initializeViews() {
        searchEditText = findViewById(R.id.searchEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        organicCheckBox = findViewById(R.id.checkOrganic);
        outOfStockSwitch = findViewById(R.id.switchOutOfStock);
        searchButton = findViewById(R.id.searchButton);
        cartButton = findViewById(R.id.cartButton);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
    }

    private void setupCategorySpinner() {
        String[] categories = {"All", "Vegetables", "Fruits", "Herbs"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupProductList() {
        // Load products from SharedPreferences if they exist
        String productsJson = sharedPreferences.getString("products", null);
        if (productsJson != null) {
            Type productType = new TypeToken<ArrayList<Product>>(){}.getType();
            products = new Gson().fromJson(productsJson, productType);
        } else {
            // Initialize with default products if none exist
            products = new ArrayList<>();
            products.add(new Product("Tomato", "Vegetables", 2.99, 50, true));
            products.add(new Product("Apple", "Fruits", 1.99, 100, false));
            products.add(new Product("Basil", "Herbs", 3.49, 30, true));
            products.add(new Product("Mint", "Herbs", 1.0, 0, false));
            products.add(new Product("Carrot", "Vegetables", 1.49, 75, false));
            products.add(new Product("Banana", "Fruits", 0.99, 120, false));
            
            // Save initial products to SharedPreferences
            String initialProductsJson = new Gson().toJson(products);
            sharedPreferences.edit().putString("products", initialProductsJson).apply();
        }
        
        productAdapter = new ProductAdapter(products, this);
        
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        searchButton.setOnClickListener(v -> searchProducts());
        cartButton.setOnClickListener(v -> openCart());
    }

    private void searchProducts() {
        String searchQuery = searchEditText.getText().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        boolean organicOnly = organicCheckBox.isChecked();
        boolean showOutOfStock = outOfStockSwitch.isChecked();
        
        List<Product> filteredProducts = new ArrayList<>();
        
        for (Product product : products) {
            boolean matchesSearch = product.getName().toLowerCase().contains(searchQuery);
            boolean matchesCategory = selectedCategory.equals("All") || 
                product.getCategory().equals(selectedCategory);
            boolean matchesOrganic = !organicOnly || product.isOrganic();
            boolean matchesStock = showOutOfStock || product.getQuantity() > 0;
            
            if (matchesSearch && matchesCategory && matchesOrganic && matchesStock) {
                filteredProducts.add(product);
            }
        }
        
        productAdapter.updateProducts(filteredProducts);
        
        // Save updated products to SharedPreferences
        String productsJson = new Gson().toJson(products);
        sharedPreferences.edit().putString("products", productsJson).apply();
    }

    public void addToCart(Product product) {
        if (product.getQuantity() > 0) {
            cartItems.add(product);
            saveCartItems();
            // Update the adapter with the new cart items
            if (productAdapter != null) {
                productAdapter.updateCartItems(cartItems);
            }
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product out of stock", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    private void loadCartItems() {
        String cartJson = sharedPreferences.getString("cart", "[]");
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        cartItems = new Gson().fromJson(cartJson, type);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCartItems() {
        String cartJson = new Gson().toJson(cartItems);
        sharedPreferences.edit().putString("cart", cartJson).apply();
    }
    
    public List<Product> getCartItems() {
        return cartItems;
    }
}