package vn.edu.hcmuaf.fit.websubject.service.impl;

import vn.edu.hcmuaf.fit.websubject.entity.CartItems;

import java.util.List;

public interface CartItemsService {
    void addToCart(int productId);
    void removeFromCart(int cartItemId);
    List<CartItems> getCartItems();
    void decreaseCartItemQuantity(int cartItemId);
}