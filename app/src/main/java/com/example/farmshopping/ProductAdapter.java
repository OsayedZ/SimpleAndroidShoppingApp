package com.example.farmshopping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private MainActivity mainActivity;
    private List<Product> cartItems;

    public ProductAdapter(List<Product> products, MainActivity mainActivity) {
        this.products = products;
        this.mainActivity = mainActivity;
        this.cartItems = mainActivity.getCartItems();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, mainActivity);
        
        // Update button text if product is already in cart
        updateAddToCartButton(holder.addToCartButton, product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }
    
    public void updateCartItems(List<Product> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }
    
    private void updateAddToCartButton(Button button, Product product) {
        if (isProductInCart(product)) {
            button.setText("Added to Cart");
            button.setEnabled(false);
        } else {
            button.setText("Add to Cart");
            button.setEnabled(product.getQuantity() > 0);
        }
    }
    
    private boolean isProductInCart(Product product) {
        if (cartItems == null) return false;
        
        for (Product cartItem : cartItems) {
            if (cartItem.getName().equals(product.getName())) {
                return true;
            }
        }
        return false;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productQuantity;
        public final Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }

        public void bind(final Product product, final MainActivity mainActivity) {
            productName.setText(product.getName());
            productPrice.setText(String.format("$%.2f", product.getPrice()));
            productQuantity.setText(String.format("Quantity: %d", product.getQuantity()));

            addToCartButton.setOnClickListener(v -> {
                mainActivity.addToCart(product);
                addToCartButton.setText("Added to Cart");
                addToCartButton.setEnabled(false);
            });
        }
    }
} 