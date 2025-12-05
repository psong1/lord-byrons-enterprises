import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../Navbar";
import AdminNavbar from "../AdminNavbar";
import * as productService from "../../api/productService";
import "./ProductForm.css";

const ProductForm = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    price: "",
    quantity: "",
    categoryId: "",
  });

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await productService.getAllCategories();
        setCategories(data);
        if (data.length > 0)
          setFormData((prev) => ({ ...prev, categoryId: data[0].id }));
      } catch (error) {
        console.error("Failed to load categories", error);
      }
    };
    fetchCategories();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...formData,
        price: parseFloat(formData.price),
        quantity: parseInt(formData.quantity),
        categoryId: parseInt(formData.categoryId),
      };

      await productService.createProduct(payload);
      alert("Product created successfully!");
      navigate("/admin/products");
    } catch (error) {
      console.error("Error creating product", error);
      alert("Failed to create product");
    }
  };

  return (
    <div>
      <Navbar />
      <AdminNavbar />
      <div className="product-form-container">
        <h2>Add New Product</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-group-label">Product Name</label>
            <input
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-group-label">Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-group-label">Price</label>
            <input
              type="number"
              step="0.01"
              name="price"
              value={formData.price}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-group-label">Quantity (Initial Stock)</label>
            <input
              type="number"
              name="quantity"
              value={formData.quantity}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-group-label">Category</label>
            <select
              name="categoryId"
              value={formData.categoryId}
              onChange={handleChange}
            >
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>
          <button type="submit" className="btn-save">
            Create Product
          </button>
        </form>
      </div>
    </div>
  );
};

export default ProductForm;
