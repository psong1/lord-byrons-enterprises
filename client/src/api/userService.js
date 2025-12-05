import api from "./axiosConfig";

export const updateProfile = async (userData) => {
  const username = localStorage.getItem("username");
  if (!username) throw new Error("No user logged in");

  const response = await api.put(`/user/${username}`);
  return response.data;
};

// Admin only
export const getAllUsers = async () => {
  const response = await api.get("/user");
  return response.data;
};

export const updateUserRole = async (userId, role) => {
  const response = await api.put(`/user/${userId}/role`, { role });
  return response.data;
};