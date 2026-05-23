import React from "react";
import { Link } from "react-router-dom";
import "./HomePage.css";

const HomePage = () => {
  return (
    <div className="home-container">
      <h1 className="home-title">Welcome to Lord Byron's Enterprises</h1>
      <Link to="/products" className="shop-now-btn">
        Shop Now
      </Link>
    </div>
  );
};

export default HomePage;
