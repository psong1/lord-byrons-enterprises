import React, { useState, useEffect } from "react";
import axios from "axios"; // Or use a dedicated service
import Navbar from "../../../components/Navbar";
import "./OrderFulfillmentPage.css";

const OrderFulfillmentPage = () => {
  const [orders, setOrders] = useState([]);
  const token = localStorage.getItem("jwt_token");

  useEffect(() => {
    // NOTE: You need to implement this endpoint in your backend!
    // GET /orders/all (Admin/Employee only)
    const fetchAllOrders = async () => {
      try {
        const response = await axios.get("http://localhost:8080/orders/all", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setOrders(response.data);
      } catch (error) {
        console.error("Failed to fetch orders", error);
      }
    };
    fetchAllOrders();
  }, [token]);

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      // NOTE: You need to implement this endpoint in your backend!
      // PUT /orders/{id}/status
      await axios.put(
        `http://localhost:8080/orders/${orderId}/status`,
        { status: newStatus },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert("Order status updated");
      // Refresh list or update local state
    } catch (error) {
      alert("Failed to update status");
    }
  };

  return (
    <div>
      <Navbar />
      <div className="fulfillment-page">
        <h2>Order Fulfillment</h2>
      <div className="order-list">
        {orders.map((order) => (
          <div key={order.id} className="order-card">
            <div>
              <h4>Order #{order.orderNumber}</h4>
              <p>User: {order.customerEmail}</p>
              <p>Total: ${order.grandTotal.toFixed(2)}</p>
            </div>
            <div>
              <select
                className="status-select"
                defaultValue={order.status}
                onChange={(e) => handleStatusChange(order.id, e.target.value)}
              >
                <option value="NEW">New</option>
                <option value="PAID">Paid</option>
                <option value="SHIPPED">Shipped</option>
                <option value="DELIVERED">Delivered</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
          </div>
        ))}
      </div>
      </div>
    </div>
  );
};

export default OrderFulfillmentPage;
