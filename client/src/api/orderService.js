import api from "./axiosConfig";

export const createOrder = async (checkoutData) => {
  const response = await api.post("/orders");
  return response.data;
};

export const getMyOrders = async () => {
  const response = await api.get("/orders");
  return response.data;
};

export const getOrderById = async (orderId) => {
  const response = await api.get(`/orders/${orderId}`);
  return response.data;
};
