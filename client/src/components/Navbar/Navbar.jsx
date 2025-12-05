import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";
import "./Navbar.css";

const Navbar = () => {
  const { isAuthenticated, logout, user } = useAuth();
  const { cartItemCount } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <Link to="/">Lord Byron's Enterprises</Link>
      </div>
      <ul className="navbar-menu">
        <li className="navbar-link">
          <Link to="/" className="navbar-link">
            Home
          </Link>
        </li>
        <li>
          <Link to="/products" className="navbar-link">
            Products
          </Link>
        </li>

        {isAuthenticated ? (
          <>
            <li>
              <Link to="/cart" className="navbar-link">
                Cart{" "}
                {cartItemCount > 0 && (
                  <span className="cart-badge">{cartItemCount}</span>
                )}
              </Link>
            </li>
            <li>
              <Link to="/account" className="navbar-link">
                Account
              </Link>
            </li>
            {user?.role === "ADMIN" && (
              <li>
                <Link to="/admin" className="navbar-link">
                  Admin Dashboard
                </Link>
              </li>
            )}
            {(user?.role === "EMPLOYEE" || user?.role === "ADMIN") && (
              <li>
                <Link to="/employee/fulfillment" className="navbar-link">
                  Order Fulfillment
                </Link>
              </li>
            )}
            <li>
              <button onClick={handleLogout} className="navbar-button">
                Logout
              </button>
            </li>
          </>
        ) : (
          <>
            <li>
              <Link to="/login" className="navbar-link">
                Login
              </Link>
            </li>
            <li>
              <Link to="/register" className="navbar-link">
                Register
              </Link>
            </li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;
