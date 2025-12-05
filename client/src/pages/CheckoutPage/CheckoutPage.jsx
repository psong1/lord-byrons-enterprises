import React, { useState, useEffect } from "react";
import * as addressService from "../../api/addressService";
import CheckoutForm from "../../components/CheckoutForm";
import Navbar from "../../components/Navbar";
import { Link } from "react-router-dom";
import "./CheckoutPage.css";

const CheckoutPage = () => {
  const [addresses, setAddresses] = useState([]);
  const [shippingId, setShippingId] = useState("");
  const [billingId, setBillingId] = useState("");
  const token = localStorage.getItem("jwt_token");

  useEffect(() => {
    const load = async () => {
      try {
        const data = await addressService.getMyAddresses();
        setAddresses(data);
        if (data.length > 0) {
          setShippingId(data[0].id);
          setBillingId(data[0].id);
        }
      } catch (error) {
        console.error("Error loading addresses");
      }
    };
    load();
  }, []);

  if (addresses.length === 0) {
    return (
      <div>
        <Navbar />
        <div style={{ padding: "20px" }}>
          You need to add an address in your <Link to="/account">Account</Link>{" "}
          page before checking out.
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="checkout-container">
        <h1>Checkout</h1>

        <div className="checkout-section">
          <h3>1. Select Shipping Address</h3>
          <select
            className="checkout-select"
            value={shippingId}
            onChange={(e) => setShippingId(e.target.value)}
          >
            {addresses.map((a) => (
              <option key={a.id} value={a.id}>
                {a.line1}, {a.city}
              </option>
            ))}
          </select>
        </div>

        <div className="checkout-section">
          <h3>2. Select Billing Address</h3>
          <select
            className="checkout-select"
            value={billingId}
            onChange={(e) => setBillingId(e.target.value)}
          >
            {addresses.map((a) => (
              <option key={a.id} value={a.id}>
                {a.line1}, {a.city}
              </option>
            ))}
          </select>
        </div>

        <div className="checkout-section">
          <h3>3. Payment</h3>
          <CheckoutForm
            shippingAddressId={shippingId}
            billingAddressId={billingId}
            userToken={token}
          />
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
