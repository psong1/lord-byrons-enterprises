import api from "./axiosConfig";

export const getMyCart = async () => {
  const response = await api.get("/cart");
  return response.data;
};

export const addItemToCart = async (variantId, quantity, productId = null) => {
  const response = await api.post("/cart/items", {
    productId,
    variantId,
    quantity,
  });
  return response.data;
};

export const updateCartItemQuantity = async (cartItemId, quantity) => {
  const response = await api.put(`/cart/items/${cartItemId}`, { quantity });
  return response.data;
};

export const removeItemFromCart = async (cartItemId) => {
  const response = await api.delete(`/cart/items/${cartItemId}`);
  return response.data;
};

export const clearCart = async () => {
  await api.delete("/cart");
};
