package com.nordicsemi.nrfUARTv2;

import org.json.JSONObject;

/**
 * Created by Pradeepn on 3/12/2019.
 */

public class Product {
    String ProductName;
    int ProductPrice;
    byte[]    ProductImage;
    int    CartQuantity=0;

    public Product(String productName, int productPrice, byte[] productImage) {
        ProductName = productName;
        ProductPrice = productPrice;
        ProductImage = productImage;
    }

    public String getJsonObject() {
        JSONObject cartItems = new JSONObject();
        try
        {
            cartItems.put("ProductName", ProductName);
            cartItems.put("ProductPrice", ProductPrice);
            cartItems.put("ProductImage",ProductImage);
            cartItems.put("CartQuantity",CartQuantity);
        }
        catch (Exception e) {}
        return cartItems.toString();
    }
}
