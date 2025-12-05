import React from "react";
import { Link, useLocation } from "react-router-dom";
import "./AdminNavbar.css";

const AdminNavbar = () => {
  const location = useLocation();

  const isActive = (path) => {
    if (path === "/admin") {
      return location.pathname === "/admin";
    }
    return location.pathname.startsWith(path);
  };

  return (
    <nav className="admin-navbar">
      <div className="admin-navbar-container">
        <Link
          to="/admin"
          className={`admin-nav-link ${isActive("/admin") ? "active" : ""}`}
        >
          Dashboard
        </Link>
        <Link
          to="/admin/products"
          className={`admin-nav-link ${isActive("/admin/products") ? "active" : ""}`}
        >
          Manage Products
        </Link>
        <Link
          to="/admin/users"
          className={`admin-nav-link ${isActive("/admin/users") ? "active" : ""}`}
        >
          Manage Users
        </Link>
      </div>
    </nav>
  );
};

export default AdminNavbar;

