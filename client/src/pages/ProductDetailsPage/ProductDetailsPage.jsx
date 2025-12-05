import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import * as productService from "../../api/productService";
import { useCart } from "../../context/CartContext";
import { addItemToCart } from "../../api/cartService";
import Navbar from "../../components/Navbar/Navbar";
import "./ProductDetailsPage.css";

const ProductDetailsPage = () => {
  const { id } = useParams();
  const { refreshCart } = useCart();
  const [product, setProduct] = useState(null);
  const [variants, setVariants] = useState([]);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadData = async () => {
      try {
        const prodData = await productService.getProductById(id);
        const varData = await productService.getProductVariants(id);
        setProduct(prodData);
        setVariants(varData);
        if (varData.length > 0) setSelectedVariant(varData[0].id);
      } catch (error) {
        console.error("Error loading product", error);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [id]);

  const handleAddToCart = async () => {
    if (!selectedVariant) return alert("Please select a variant (Size/Color)");
    try {
      await addItemToCart(selectedVariant, quantity);
      refreshCart();
      alert("Added to cart!");
    } catch (error) {
      alert("Failed to add to cart. " + (error.response?.data || ""));
    }
  };

  if (loading) return <div>Loading...</div>;
  if (!product) return <div>Product not found.</div>;

  return (
    <div>
      <Navbar />
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
            <p className="out-of-stock">Out of Stock</p>
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
            disabled={variants.length === 0}
            className="add-to-cart-btn"
          >
            {variants.length === 0 ? "Unavailable" : "Add to Cart"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductDetailsPage;
