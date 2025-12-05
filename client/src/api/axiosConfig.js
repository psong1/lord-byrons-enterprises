import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("jwt_token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // Server responded with error status
      if (error.response.status === 401) {
        localStorage.removeItem("jwt_token");
        localStorage.removeItem("user_role");
        localStorage.removeItem("username");
        localStorage.removeItem("role");
        window.location.href = "/login";
      }
    } else if (error.request) {
      // Request was made but no response received
      console.error("Network error: No response from server", error.request);
      error.message = "Network error: Could not connect to server. Please check if the server is running on http://localhost:8080";
    } else {
      // Something else happened
      console.error("Error setting up request:", error.message);
    }
    return Promise.reject(error);
  }
);

export default api;
