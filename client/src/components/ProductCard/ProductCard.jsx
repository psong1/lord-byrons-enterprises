import React, { useState } from "react";
import { Link } from "react-router-dom";
import { addItemToCart } from "../../api/cartService";
import { useCart } from "../../context/CartContext";
import * as productService from "../../api/productService";
import "./ProductCard.css";

const ProductCard = ({ product }) => {
  const [adding, setAdding] = useState(false);
  const { refreshCart } = useCart();

  const handleQuickAdd = async () => {
    try {
      setAdding(true);
      // Fetch variants for this product
      const variants = await productService.getProductVariants(product.id);

      if (!variants || variants.length === 0) {
        // If no variants exist, send productId and let backend create a default variant
        await addItemToCart(null, 1, product.id);
        refreshCart();
        alert("Added to cart!");
        return;
      }

      // Use the first available variant
      const firstVariant = variants[0];
      // Always send productId - use variant's productId if available, otherwise use product.id
      const productIdToSend = firstVariant.productId || product.id;
      await addItemToCart(firstVariant.id, 1, productIdToSend);
      refreshCart();
      alert("Added to cart!");
    } catch (error) {
      console.error("Error adding to cart", error);
      let errorMessage = "Failed to add to cart.";

      if (error.response) {
        // Server responded with error
        if (error.response.data) {
          if (typeof error.response.data === "string") {
            errorMessage = error.response.data;
          } else if (error.response.data.message) {
            errorMessage = error.response.data.message;
          } else if (error.response.data.error) {
            errorMessage = error.response.data.error;
          }
        } else {
          errorMessage = `Server error: ${error.response.status} ${error.response.statusText}`;
        }
      } else if (error.request) {
        errorMessage = "No response from server. Please check your connection.";
      } else {
        errorMessage = error.message || "Unknown error occurred.";
      }

      alert(errorMessage);
    } finally {
      setAdding(false);
    }
  };

  return (
    <div className="product-card">
      <div className="product-image-placeholder">Image</div>

      <h3 className="product-title">{product.name}</h3>
      <p className="product-description">
        {product.description.substring(0, 50)}...
      </p>
      <p className="product-price">${product.price.toFixed(2)}</p>

      <div className="product-card-actions">
        <button
          onClick={handleQuickAdd}
          disabled={adding}
          className="add-to-cart-btn"
        >
          {adding ? "Adding..." : "Add to Cart"}
        </button>
        <Link to={`/product/${product.id}`} className="view-details-btn">
          View Details
        </Link>
      </div>
    </div>
  );
};

export default ProductCard;
