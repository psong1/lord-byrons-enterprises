import React, { Suspense, lazy } from "react";
import { BrowserRouter, Routes, Route, Outlet, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { CartProvider } from "./context/CartContext";
import "./App.css";

// Standard Components
import Navbar from "./components/Navbar/Navbar";
import AdminNavbar from "./components/AdminNavbar/AdminNavbar";
import PrivateRoute from "./components/PrivateRoute/PrivateRoute";
import RoleBasedRoute from "./components/RoleBasedRoute/RouleBasedRoute";

// --- CUSTOMER / PUBLIC PAGES ---
const HomePage = lazy(() => import("./pages/HomePage/HomePage"));
const LoginPage = lazy(() => import("./pages/LoginPage/LoginPage"));
const RegisterPage = lazy(() => import("./pages/Register/RegisterPage"));
const ProductListPage = lazy(
  () => import("./pages/ProductListPage/ProductListPage"),
);
const ProductDetailsPage = lazy(
  () => import("./pages/ProductDetailsPage/ProductDetailsPage"),
);
const CartPage = lazy(() => import("./pages/CartPage/CartPage"));
const CheckoutPage = lazy(() => import("./pages/CheckoutPage/CheckoutPage"));
const AccountPage = lazy(() => import("./pages/AccountPage/AccountPage"));
const OrderHistoryPage = lazy(
  () => import("./pages/OrderHistoryPage/OrderHistoryPage"),
);

// --- EMPLOYEE / ADMIN PORTAL PAGES ---
const AdminDashboardPage = lazy(
  () => import("./pages/admin/AdminDashboardPage/AdminDashboardPage"),
);
const ProductManagementPage = lazy(
  () => import("./pages/admin/ProductManagement/ProductManagementPage"),
);
const UserManagementPage = lazy(
  () => import("./pages/admin/UserManagementPage/UserManagementPage"),
);
const OrderFulfillmentPage = lazy(
  () => import("./pages/employee/OrderFulfillmentPage/OrderFulfillmentPage"),
);
const PayrollPage = lazy(() => import("./pages/PayrollPage/PayrollPage"));
const ProcessPayrollPage = lazy(
  () => import("./pages/ProcessPayrollPage/ProcessPayrollPage"),
);

// --- STANDARD SHOPPER LAYOUT ---
const StorefrontLayout = () => (
  <>
    <Navbar />
    <div className="storefront-content">
      <Outlet />
    </div>
  </>
);

// --- PRIVATE LAYOUT ---
const PortalLayout = () => (
  <>
    <AdminNavbar />
    <div className="portal-content">
      <Outlet />
    </div>
  </>
);

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <Suspense
            fallback={
              <div className="Loading-spinner">Loading Application...</div>
            }
          >
            <Routes>
              {/* Legacy paths → portal */}
              <Route
                path="/admin"
                element={<Navigate to="/portal/dashboard" replace />}
              />
              <Route
                path="/admin/products"
                element={<Navigate to="/portal/inventory" replace />}
              />
              <Route
                path="/admin/users"
                element={<Navigate to="/portal/user-management" replace />}
              />
              <Route
                path="/employee/fulfillment"
                element={<Navigate to="/portal/fulfillment" replace />}
              />

              {/* Public Routes */}
              <Route element={<StorefrontLayout />}>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/products" element={<ProductListPage />} />
                <Route path="/products/:id" element={<ProductDetailsPage />} />
                <Route path="/cart" element={<CartPage />} />

                {/*Authenticated Customer Routes*/}
                <Route element={<PrivateRoute />}>
                  <Route path="/account" element={<AccountPage />} />
                  <Route path="/checkout" element={<CheckoutPage />} />
                  <Route path="/orders" element={<OrderHistoryPage />} />
                </Route>
              </Route>

              {/* Employee/Admin Routes */}
              <Route
                element={
                  <RoleBasedRoute allowedRoles={["ADMIN", "EMPLOYEE"]} />
                }
              >
                <Route element={<PortalLayout />}>
                  <Route
                    path="/portal/dashboard"
                    element={<AdminDashboardPage />}
                  />
                  <Route
                    path="/portal/fulfillment"
                    element={<OrderFulfillmentPage />}
                  />
                  <Route path="/portal/payroll" element={<PayrollPage />} />

                  <Route element={<RoleBasedRoute allowedRoles={["ADMIN"]} />}>
                    <Route
                      path="/portal/inventory"
                      element={<ProductManagementPage />}
                    />
                    <Route
                      path="/portal/process-payroll"
                      element={<ProcessPayrollPage />}
                    />
                    <Route
                      path="/portal/user-management"
                      element={<UserManagementPage />}
                    />
                  </Route>
                </Route>
              </Route>
            </Routes>
          </Suspense>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
