import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import * as cartService from "../../api/cartService";
import { useCart } from "../../context/CartContext";
import CartItem from "../../components/CartItem";
import Navbar from "../../components/Navbar";
import "./CartPage.css";

const CartPage = () => {
  const [cartData, setCartData] = useState(null);
  const { refreshCart } = useCart();

  const loadCart = async () => {
    try {
      const data = await cartService.getMyCart();
      setCartData(data);
    } catch (error) {
      console.error("Error loading cart", error);
    }
  };

  useEffect(() => {
    loadCart();
  }, []);

  const handleUpdateQuantity = async (itemId, newQty) => {
    await cartService.updateCartItemQuantity(itemId, newQty);
    loadCart();
    refreshCart();
  };

  const handleRemove = async (itemId) => {
    await cartService.removeItemFromCart(itemId);
    loadCart();
    refreshCart();
  };

  return (
    <div>
      <Navbar />
      <div className="cart-container">
        <h2>Shopping Cart</h2>

        {!cartData || !cartData.items || cartData.items.length === 0 ? (
          <p>
            Your cart is empty. <Link to="/products">Go Shopping</Link>
          </p>
        ) : (
          <>
            <div className="cart-items-list">
              {cartData.items.map((item) => (
                <CartItem
                  key={item.id}
                  item={item}
                  onUpdateQuantity={handleUpdateQuantity}
                  onRemove={handleRemove}
                />
              ))}
            </div>
            <div className="cart-summary">
              <p>Subtotal: ${cartData.subtotal.toFixed(2)}</p>
              <p>Tax: ${cartData.tax.toFixed(2)}</p>
              <h3 className="cart-total">
                Total: ${cartData.total.toFixed(2)}
              </h3>

              <Link to="/checkout" className="checkout-btn">
                Proceed to Checkout
              </Link>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default CartPage;
