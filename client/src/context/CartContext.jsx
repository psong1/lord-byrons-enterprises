import React, { createContext, useState, useEffect, useContext } from "react";
import * as cartService from "../api/cartService";

const CartContext = createContext(null);

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState(null);
  const [cartItemCount, setCartItemCount] = useState(0);
  const [isUpdating, setIsUpdating] = useState(false);

  const refreshCart = async () => {
    try {
      const data = await cartService.getMyCart();
      setCart(data);

      const count = data.items
        ? data.items.reduce((sum, item) => sum + item.quantity, 0)
        : 0;
      setCartItemCount(count);
    } catch (error) {
      console.error("Failed to load cart", error);
    }
  };

  const addToCart = async (variantId, quantity, productId = null) => {
    setIsUpdating(true);
    try {
      await cartService.addItemToCart(variantId, quantity, productId);
      await refreshCart();
    } catch (error) {
      console.error("Error adding to cart:", error);
      throw error;
    } finally {
      setIsUpdating(false);
    }
  };

  const updateQuantity = async (cartItemId, quantity) => {
    setIsUpdating(true);
    try {
      await cartService.updateCartItemQuantity(cartItemId, quantity);
      await refreshCart();
    } catch (error) {
      console.error("Error updating quantity:", error);
    } finally {
      setIsUpdating(false);
    }
  };

  const removeFromCart = async (cartItemId) => {
    setIsUpdating(true);
    try {
      await cartService.removeItemFromCart(cartItemId);
      await refreshCart();
    } catch (error) {
      console.error("Error removing item from cart:", error);
    } finally {
      setIsUpdating(false);
    }
  };

  useEffect(() => {
    refreshCart();
  }, []);

  return (
    <CartContext.Provider
      value={{
        cart,
        cartItemCount,
        refreshCart,
        addToCart,
        updateQuantity,
        removeFromCart,
        isUpdating,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
};
