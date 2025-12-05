import React, { createContext, useState, useEffect, useContext } from "react";
import * as cartService from "../api/cartService";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

export const CartProvider = ({ children }) => {
  const { isAuthenticated } = useAuth();
  const [cart, setCart] = useState(null);
  const [cartItemCount, setCartItemCount] = useState(0);

  const refreshCart = async () => {
    if (!isAuthenticated) {
      setCart(null);
      setCartItemCount(0);
      return;
    }

    try {
      const data = await cartService.getMyCart();
      setCart(data);

      const count = data.items
        ? data.items.reduce((sum, item) => sum + item.quantity, 0)
        : 0;
      setCartItemCount(count);
    } catch (error) {
      console.error("Failed to catch error", error);
    }
  };

  useEffect(() => {
    refreshCart();
  }, [isAuthenticated]);

  return (
    <CartContext.Provider value={{ cart, cartItemCount, refreshCart }}>
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