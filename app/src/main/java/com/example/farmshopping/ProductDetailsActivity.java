package com.example.farmshopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    
    private ImageView productImage;
    private TextView productName;
    private TextView productPrice;
    private TextView productCategory;
    private TextView productQuantity;
    private TextView productOrganic;
    private TextView productDescription;
    private Button addToCartButton;
    
    private Product product;
    private List<Product> cartItems;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        
        // Initialize views
        initializeViews();
        
        // Get product from intent
        if (getIntent().hasExtra("product")) {
            product = (Product) getIntent().getSerializableExtra("product");
            displayProductDetails();
        } else {
            Toast.makeText(this, "Error loading product details", Toast.LENGTH_SHORT).show();
            finish();
        }
        
        // Setup shared preferences
        sharedPreferences = getSharedPreferences("FarmShopping", MODE_PRIVATE);
        
        // Load cart items
        loadCartItems();
        
        // Setup add to cart button
        setupAddToCartButton();
    }
    
    private void initializeViews() {
        productImage = findViewById(R.id.detailProductImage);
        productName = findViewById(R.id.detailProductName);
        productPrice = findViewById(R.id.detailProductPrice);
        productCategory = findViewById(R.id.detailProductCategory);
        productQuantity = findViewById(R.id.detailProductQuantity);
        productOrganic = findViewById(R.id.detailProductOrganic);
        productDescription = findViewById(R.id.detailProductDescription);
        addToCartButton = findViewById(R.id.detailAddToCartButton);
    }
    
    private void displayProductDetails() {
        productName.setText(product.getName());
        productPrice.setText(String.format("$%.2f", product.getPrice()));
        productCategory.setText("Category: " + product.getCategory());
        productQuantity.setText("Quantity: " + product.getQuantity());
        productOrganic.setText("Organic: " + (product.isOrganic() ? "Yes" : "No"));

        productDescription.setText("Fresh " + product.getName() + " from local farms. " +
                "Our products are carefully selected to ensure the best quality for our customers.");
    }
    
    private void loadCartItems() {
        String cartJson = sharedPreferences.getString("cart", "[]");
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        cartItems = new Gson().fromJson(cartJson, type);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }
    
    private void setupAddToCartButton() {
        // Check if product is already in cart
        boolean isInCart = isProductInCart(product);
        
        if (isInCart) {
            addToCartButton.setText("Added to Cart");
            addToCartButton.setEnabled(false);
        } else {
            addToCartButton.setText("Add to Cart");
            addToCartButton.setEnabled(product.getQuantity() > 0);
        }
        
        addToCartButton.setOnClickListener(v -> {
            if (product.getQuantity() > 0 && !isInCart) {
                addToCart(product);
                addToCartButton.setText("Added to Cart");
                addToCartButton.setEnabled(false);
            } else if (product.getQuantity() <= 0) {
                Toast.makeText(this, "Product out of stock", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private boolean isProductInCart(Product product) {
        for (Product cartItem : cartItems) {
            if (cartItem.getName().equals(product.getName())) {
                return true;
            }
        }
        return false;
    }
    
    private void addToCart(Product product) {
        cartItems.add(product);
        saveCartItems();
        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
    }
    
    private void saveCartItems() {
        String cartJson = new Gson().toJson(cartItems);
        sharedPreferences.edit().putString("cart", cartJson).apply();
    }
} 