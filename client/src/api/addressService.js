import api from "./axiosConfig";

export const getMyAddresses = async () => {
  const response = await api.get("/user/addresses");
  return response.data;
};

export const createAddress = async (addressData) => {
  const response = await api.post("/user/addresses", addressData);
  return response.data;
};

export const updateAddress = async (addressId, addressData) => {
  const response = await api.put(`/user/addresses/${addressId}`, addressData);
  return response.data;
};

export const deleteAddress = async (addressId) => {
  await api.delete(`/user/addresses/${addressId}`);
};
