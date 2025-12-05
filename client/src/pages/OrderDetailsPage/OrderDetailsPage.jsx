import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import * as orderService from "../../api/orderService";

const OrderDetailsPage = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        const data = await orderService.getOrderById(id);
        setOrder(data);
      } catch (error) {
        console.error("Error loading order", error);
      }
    };
    fetchOrder();
  }, [id]);

  if (!order) return <div>Loading...</div>;

  return (
    <div style={{ padding: "20px", maxWidth: "800px", margin: "0 auto" }}>
      <h1>Order #{order.orderNumber}</h1>
      <p>
        Status: <strong>{order.status}</strong>
      </p>
      <p>Date: {new Date(order.placedAt).toLocaleString()}</p>

      <h3>Shipping Address</h3>
      <p>{order.shippingAddress.line1}</p>
      <p>
        {order.shippingAddress.city}, {order.shippingAddress.country}
      </p>

      <h3>Items</h3>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {order.items.map((item) => (
          <li
            key={item.id}
            style={{
              display: "flex",
              justifyContent: "space-between",
              borderBottom: "1px solid #eee",
              padding: "10px 0",
            }}
          >
            <span>
              {item.name} (x{item.quantity})
            </span>
            <span>${item.lineTotal.toFixed(2)}</span>
          </li>
        ))}
      </ul>

      <h3 style={{ textAlign: "right" }}>
        Total: ${order.grandTotal.toFixed(2)}
      </h3>
    </div>
  );
};

export default OrderDetailsPage;
