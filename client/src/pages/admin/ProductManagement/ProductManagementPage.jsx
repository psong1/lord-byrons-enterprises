import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Navbar from "../../../components/Navbar";
import AdminNavbar from "../../../components/AdminNavbar";
import * as productService from "../../../api/productService";
import "./ProductManagementPage.css";

const ProductManagementPage = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const data = await productService.getAllProducts();
      setProducts(data);
    } catch (error) {
      console.error("Failed to load products", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Asre you sure you want to delete this product?")) {
      try {
        await productService.deleteProduct(id);
        setProducts(products.filter((p) => p.id !== id));
      } catch (error) {
        console.error("Failed to load products", error);
      } finally {
        setLoading(false);
      }
    }
  };

  if (loading)
    return (
      <div>
        <Navbar />
        <AdminNavbar />
        <div className="product-management">Loading products...</div>
      </div>
    );

  return (
    <div>
      <Navbar />
      <AdminNavbar />
      <div className="product-management">
        <div className="pm-header">
          <h2>Product Management</h2>
          <Link to="/admin/products/new" className="btn-add">
            Add New Product
          </Link>
        </div>

        <table className="product-table">
          <thead>
            <tr>
              <th className="product-table-header">ID</th>
              <th className="product-table-header">Name</th>
              <th className="product-table-header">Price</th>
              <th className="product-table-header">Quantity</th>
              <th className="product-table-header">Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.id}>
                <td>{product.id}</td>
                <td>{product.name}</td>
                <td>${product.price.toFixed(2)}</td>
                <td>{product.quantity}</td>
                <td>
                  <button
                    onClick={() => handleDelete(product.id)}
                    className="btn-delete"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ProductManagementPage;
