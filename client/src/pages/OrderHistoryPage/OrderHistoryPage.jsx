import React, { useEffect, useState } from "react";
import * as orderService from "../../api/orderService";
import Navbar from "../../components/Navbar";
import { Link } from "react-router-dom";
import "./OrderHistoryPage.css";

const OrderHistoryPage = () => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const data = await orderService.getMyOrders();
        setOrders(data);
      } catch (error) {
        console.error("Failed to load orders", error);
      }
    };
    fetchOrders();
  }, []);

  return (
    <div>
      <Navbar />
      <div className="order-history-container">
        <h1>Order History</h1>
        <table className="orders-table">
          <thead>
            <tr>
              <th>Order #</th>
              <th>Date</th>
              <th>Total</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td>{order.orderNumber}</td>
                <td>{new Date(order.placedAt).toLocaleDateString()}</td>
                <td>${order.grandTotal.toFixed(2)}</td>
                <td>{order.status}</td>
                <td>
                  <Link to={`/orders/${order.id}`} className="view-order-link">
                    View
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default OrderHistoryPage;
