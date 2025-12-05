import React from "react";
import "./CartItem.css";

const CartItem = ({ item, onUpdateQuantity, onRemove }) => {
  return (
    <div className="cart-item-row">
      <div className="cart-item-info">
        <h4 className="cart-item-title">{item.productName}</h4>
        <small>Unit Price: ${item.unitPrice.toFixed(2)}</small>
      </div>

      <div className="cart-item-controls">
        <button
          className="qty-btn"
          onClick={() => onUpdateQuantity(item.id, item.quantity - 1)}
          disabled={item.quantity <= 1}
        >
          -
        </button>
        <span>{item.quantity}</span>
        <button
          className="qty-btn"
          onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}
        >
          +
        </button>
      </div>

      <div className="cart-item-total">
        <p className="item-total-price">${item.lineTotal.toFixed(2)}</p>
        <button onClick={() => onRemove(item.id)} className="remove-btn">
          Remove
        </button>
      </div>
    </div>
  );
};

export default CartItem;
