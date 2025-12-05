import api from "./axiosConfig";

export const getAllProducts = async () => {
  const response = await api.get("/product");
  return response.data;
};

export const getProductById = async (id) => {
  const response = await api.get(`/product/${id}`);
  return response.data;
};

export const getProductVariants = async (productId) => {
  const response = await api.get(`/products/${productId}/variants`);
  return response.data;
};

export const getAllCategories = async () => {
  const response = await api.get("/category");
  return response.data;
};

export const getCategoryById = async (id) => {
  const response = await api.get(`category/${id}`);
  return response.data;
};

export const createProduct = async (productData) => {
  const response = await api.post("/product", productData);
  return response.data;
};

export const updateProduct = async (id, productData) => {
  const response = await api.put(`/product/${id}`, productData);
  return response.data;
};

export const deleteProduct = async (id) => {
  const response = await api.delete(`/product/${id}`);
  return response.data;
};
