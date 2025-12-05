import React, { useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import * as addressService from "../../api/addressService";
import AddressCard from "../../components/AddressCard";
import AddressForm from "../../components/AddressForm";
import Navbar from "../../components/Navbar";
import { Link } from "react-router-dom";
import "./AccountPage.css";

const AccountPage = () => {
  const { user } = useAuth();
  const [addresses, setAddresses] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);

  const loadAddresses = async () => {
    try {
      const data = await addressService.getMyAddresses();
      setAddresses(data);
    } catch (error) {
      console.error("Failed to load addresses");
    }
  };

  useEffect(() => {
    loadAddresses();
  }, []);

  const handleSubmitAddress = async (formData) => {
    try {
      if (editingAddress) {
        await addressService.updateAddress(editingAddress.id, formData);
      } else {
        await addressService.createAddress(formData);
      }
      setShowForm(false);
      setEditingAddress(null);
      loadAddresses();
    } catch (error) {
      alert("Error saving address");
    }
  };

  const handleEditClick = (addr) => {
    setEditingAddress(addr);
    setShowForm(true);
  };

  const handleDeleteAddress = async (id) => {
    if (window.confirm("Delete this address?")) {
      await addressService.deleteAddress(id);
      loadAddresses();
    }
  };

  return (
    <div>
      <Navbar />
      <div className="account-container">
        <h1>My Account</h1>
        <p>
          Welcome back, <strong>{user?.username}</strong>! (Role: {user?.role})
        </p>
        <Link to="/orders" style={{ color: "#007bff" }}>
          View Order History
        </Link>

        <hr className="section-divider" />

        <div className="address-section-header">
          <h2>My Addresses</h2>
          <button
            className="btn-add-address"
            onClick={() => {
              setShowForm(!showForm);
              setEditingAddress(null);
            }}
          >
            {showForm ? "Cancel" : "+ Add New Address"}
          </button>
        </div>

        {showForm && (
          <div style={{ marginBottom: "20px" }}>
            <AddressForm
              initialData={editingAddress || {}}
              onSubmit={handleSubmitAddress}
              onCancel={() => setShowForm(false)}
            />
          </div>
        )}

        <div className="address-list">
          {addresses.map((addr) => (
            <AddressCard
              key={addr.id}
              address={addr}
              onEdit={handleEditClick}
              onDelete={handleDeleteAddress}
            />
          ))}
          {addresses.length === 0 && <p>No addresses saved.</p>}
        </div>
      </div>
    </div>
  );
};

export default AccountPage;
