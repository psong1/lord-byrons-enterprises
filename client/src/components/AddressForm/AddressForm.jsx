import React, { useState, useEffect } from "react";
import "./AddressForm.css";

const AddressForm = ({ initialData = {}, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    line1: "",
    line2: "",
    city: "",
    country: "",
    type: "SHIPPING",
  });

  useEffect(() => {
    if (initialData.id) {
      setFormData(initialData);
    }
  }, [initialData]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="address-form">
      <div className="form-group">
        <label>Address Type:</label>
        <select name="type" value={formData.type} onChange={handleChange}>
          <option value="SHIPPING">Shipping</option>
          <option value="BILLING">Billing</option>
        </select>
      </div>
      <div className="form-group">
        <label>Street Address:</label>
        <input
          name="line1"
          value={formData.line1}
          onChange={handleChange}
          required
        />
      </div>
      <div className="form-group">
        <label>Apartment/Unit</label>
        <input name="line2" value={formData.line2} onChange={handleChange} />
      </div>
      <div className="form-group">
        <label>City:</label>
        <input
          name="city"
          value={formData.city}
          onChange={handleChange}
          required
        />
      </div>
      <div className="form-group">
        <label>Country:</label>
        <input
          name="country"
          value={formData.country}
          onChange={handleChange}
          required
        />
      </div>

      <div className="form-actions">
        <button type="submit" className="btn-submit">
          Save Address
        </button>
        {onCancel && (
          <button type="button" onClick={onCancel} className="btn-cancel">
            Cancel
          </button>
        )}
      </div>
    </form>
  );
};

export default AddressForm;
