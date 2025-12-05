import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import * as authService from "../../api/authService";
import Navbar from "../../components/Navbar";
import "../LoginPage/LoginPage.css";
import "./RegisterPage.css";

const RegisterPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    password: "",
    role: "CUSTOMER",
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await authService.register(formData);
      alert("Registration successful!");
      navigate("/login");
    } catch (error) {
      let errorMessage = "Registration failed.";

      if (error.response) {
        // Server responded with error
        const status = error.response.status;
        const data = error.response.data;

        if (status === 409) {
          errorMessage =
            "Username or email already exists. Please choose a different one.";
        } else if (status === 400) {
          // Validation error
          if (data && data.message) {
            errorMessage = data.message;
          } else if (Array.isArray(data)) {
            errorMessage = data.map((err) => err.message || err).join(", ");
          } else if (typeof data === "string") {
            errorMessage = data;
          } else {
            errorMessage =
              "Invalid input. Please check your information:\n" +
              "- Password must be at least 8 characters with uppercase, lowercase, and a number\n" +
              "- Username must be at least 4 characters\n" +
              "- Email must be valid";
          }
        } else {
          errorMessage = `Registration failed: ${
            data?.message || "Unknown error"
          }`;
        }
      } else if (error.request) {
        errorMessage =
          "Unable to connect to server. Please check if the server is running.";
      } else {
        errorMessage = error.message || "Registration failed.";
      }

      alert(errorMessage);
      console.error("Registration error:", error);
    }
  };

  return (
    <div>
      <Navbar />
      <div className="auth-container">
        <h2 className="auth-title">Create Account</h2>
        <form onSubmit={handleSubmit} className="auth-form">
          <input
            name="firstName"
            placeholder="First Name"
            className="auth-input"
            onChange={handleChange}
            required
          />
          <input
            name="lastName"
            placeholder="Last Name"
            className="auth-input"
            onChange={handleChange}
            required
          />
          <input
            name="username"
            placeholder="Username (min 4 characters)"
            className="auth-input"
            onChange={handleChange}
            required
            minLength={4}
          />
          <input
            name="email"
            type="email"
            placeholder="Email"
            className="auth-input"
            onChange={handleChange}
            required
          />
          <input
            name="password"
            type="password"
            placeholder="Password (min 8 chars, uppercase, lowercase, number)"
            className="auth-input"
            onChange={handleChange}
            required
            minLength={8}
            pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
            title="Password must be at least 8 characters and include a digit, a lowercase and an uppercase letter"
          />
          <small
            style={{
              color: "#888",
              fontSize: "0.85em",
              display: "block",
              marginTop: "-10px",
              marginBottom: "10px",
            }}
          >
            Password must be at least 8 characters with uppercase, lowercase,
            and a number
          </small>
          <button type="submit" className="auth-button register-button">
            Register
          </button>
        </form>
        <p className="auth-footer">
          Already have an account?
          <br />
          <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;
