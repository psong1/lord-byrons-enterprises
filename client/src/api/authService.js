import api from "./axiosConfig.js";

export const login = async (username, password) => {
  try {
    const response = await api.post("/auth/login", { username, password });

    if (response.data.token) {
      localStorage.setItem("jwt_token", response.data.token);
      localStorage.setItem("username", response.data.username);
      if (response.data.role) {
        localStorage.setItem("role", response.data.role);
      }
    }
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
};

export const register = async (userData) => {
  try {
    const response = await api.post("/user", userData);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
};

export const logout = async () => {
  localStorage.removeItem("jwt_token");
  localStorage.removeItem("username");
  localStorage.removeItem("role");
  window.location.href = "/login";
};
