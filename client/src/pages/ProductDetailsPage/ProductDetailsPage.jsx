import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import * as productService from "../../api/productService";
import { useCart } from "../../context/CartContext";
import "./ProductDetailsPage.css";

const ProductDetailsPage = () => {
  const { id } = useParams();
  const { addToCart, isUpdating } = useCart();
  const [product, setProduct] = useState(null);
  const [variants, setVariants] = useState([]);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        setError(null);
        console.log("Loading product with ID:", id, "Type:", typeof id);

        // Convert id to number if it's a string (React Router params are strings)
        const productId = id
          ? typeof id === "string"
            ? parseInt(id, 10)
            : id
          : null;

        if (!productId || isNaN(productId)) {
          setError("Invalid product ID.");
          setLoading(false);
          return;
        }

        const prodData = await productService.getProductById(productId);
        console.log("Product data received:", prodData);

        // Try to get variants, but don't fail if it errors
        let varData = [];
        try {
          varData = (await productService.getProductVariants(productId)) || [];
        } catch (variantError) {
          console.warn("Could not load variants:", variantError);
          // Continue without variants
        }

        setProduct(prodData);
        setVariants(varData);
        if (varData && varData.length > 0) setSelectedVariant(varData[0].id);
      } catch (error) {
        console.error("Error loading product", error);
        console.error("Error response:", error.response);
        if (error.response?.status === 404) {
          setError(
            `Product not found (ID: ${id}). The product may have been removed or the ID is invalid.`,
          );
        } else if (error.response?.status === 401) {
          setError("You need to be logged in to view this product.");
        } else if (error.response?.status === 500) {
          setError("Server error occurred. Please try again later.");
        } else if (error.message?.includes("Network")) {
          setError(
            "Could not connect to server. Please check if the server is running.",
          );
        } else {
          setError(
            `Failed to load product: ${
              error.response?.data?.message || error.message || "Unknown error"
            }`,
          );
        }
      } finally {
        setLoading(false);
      }
    };
    if (id) {
      loadData();
    } else {
      setError("No product ID provided.");
      setLoading(false);
    }
  }, [id]);

  const handleAddToCart = async () => {
    try {
      // If product has variants, use the selected variant
      // Otherwise, add the product directly using productId
      if (variants.length > 0) {
        if (!selectedVariant) {
          alert("Please select a variant (Size/Color)");
          return;
        }
        await addToCart(selectedVariant, quantity, product.id);
      } else {
        // No variants - add product directly
        await addToCart(null, quantity, product.id);
      }
      alert("Added to cart!");
    } catch (error) {
      console.error("Error adding to cart:", error);
      const errorMessage =
        error.response?.data?.message ||
        error.response?.data?.error ||
        error.response?.data ||
        "Failed to add to cart. Please try again.";
      alert(errorMessage);
    }
  };

  if (loading) {
    return (
      <div style={{ padding: "40px", textAlign: "center" }}>
        Loading product details...
      </div>
    );
  }

  if (error || !product) {
    return (
      <div style={{ padding: "40px", textAlign: "center" }}>
          <h2>Product Not Found</h2>
          <p>
            {error ||
              "The product you're looking for doesn't exist or has been removed."}
          </p>
          <Link
            to="/products"
            style={{ color: "#646cff", textDecoration: "underline" }}
          >
            Return to Products
          </Link>
      </div>
    );
  }

  return (
    <div className="product-details-container">
        <div className="product-detail-image">Image Placeholder</div>
        <div className="product-info">
          <h1>{product.name}</h1>
          <p>{product.description}</p>

          <h3>
            $
            {variants.find((v) => v.id == selectedVariant)?.price.toFixed(2) ||
              product.price.toFixed(2)}
          </h3>

          {variants.length > 0 ? (
            <div className="variant-selector">
              <label>
                <strong>Variant:</strong>{" "}
              </label>
              <select
                className="variant-select"
                onChange={(e) => setSelectedVariant(e.target.value)}
                value={selectedVariant || ""}
              >
                {variants.map((v) => (
                  <option key={v.id} value={v.id}>
                    {v.title} (SKU: {v.sku})
                  </option>
                ))}
              </select>
            </div>
          ) : (
            <div className="product-info-section">
              <p>{product.quantity > 0 ? `In Stock` : "Out of Stock"}</p>
            </div>
          )}

          <div className="quantity-selector">
            <label>
              <strong>Quantity:</strong>{" "}
            </label>
            <input
              type="number"
              min="1"
              className="quantity-input"
              value={quantity}
              onChange={(e) => setQuantity(parseInt(e.target.value))}
            />
          </div>

          <button
            onClick={handleAddToCart}
            disabled={product.quantity === 0}
            className="add-to-cart-btn"
          >
            {isUpdating
              ? "Adding..."
              : product.quantity === 0
                ? "Out of Stock"
                : "Add to Cart"}
          </button>
        </div>
    </div>
  );
};

export default ProductDetailsPage;
