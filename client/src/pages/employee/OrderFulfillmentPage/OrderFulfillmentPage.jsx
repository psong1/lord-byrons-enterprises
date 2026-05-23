import React, { useState, useEffect } from "react";
import api from "../../../api/axiosConfig";
import "./OrderFulfillmentPage.css";

const OrderFulfillmentPage = () => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    const fetchAllOrders = async () => {
      try {
        const response = await api.get("/orders/all");
        setOrders(response.data);
      } catch (error) {
        console.error("Failed to fetch orders", error);
      }
    };
    fetchAllOrders();
  }, []);

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      await api.put(`/orders/${orderId}/status`, { status: newStatus });
      alert("Order status updated");
    } catch (error) {
      alert("Failed to update status");
    }
  };

  return (
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
  );
};

export default OrderFulfillmentPage;
