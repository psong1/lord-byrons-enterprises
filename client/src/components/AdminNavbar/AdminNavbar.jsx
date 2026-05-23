import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import "./AdminNavbar.css";

const AdminNavbar = () => {
  const location = useLocation();
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";

  const isActive = (path) => location.pathname.startsWith(path);

  return (
    <nav className="admin-navbar">
      <div className="admin-navbar-container">
        <Link
          to="/portal/dashboard"
          className={`admin-nav-link ${isActive("/portal/dashboard") ? "active" : ""}`}
        >
          Dashboard
        </Link>
        <Link
          to="/portal/fulfillment"
          className={`admin-nav-link ${isActive("/portal/fulfillment") ? "active" : ""}`}
        >
          Order Fulfillment
        </Link>
        <Link
          to="/portal/payroll"
          className={`admin-nav-link ${isActive("/portal/payroll") ? "active" : ""}`}
        >
          Payroll
        </Link>
        {isAdmin && (
          <>
            <Link
              to="/portal/inventory"
              className={`admin-nav-link ${isActive("/portal/inventory") ? "active" : ""}`}
            >
              Manage Products
            </Link>
            <Link
              to="/portal/process-payroll"
              className={`admin-nav-link ${isActive("/portal/process-payroll") ? "active" : ""}`}
            >
              Process Payroll
            </Link>
            <Link
              to="/portal/user-management"
              className={`admin-nav-link ${isActive("/portal/user-management") ? "active" : ""}`}
            >
              Manage Users
            </Link>
          </>
        )}
        <Link to="/" className="admin-nav-link admin-nav-link--storefront">
          Storefront
        </Link>
      </div>
    </nav>
  );
};

export default AdminNavbar;
