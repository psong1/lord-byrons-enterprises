import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { CartProvider } from "./context/CartContext";
import "./App.css";

// Pages
import HomePage from "./pages/HomePage/HomePage";
import LoginPage from "./pages/LoginPage/LoginPage";
import RegisterPage from "./pages/Register/RegisterPage";
import ProductListPage from "./pages/ProductListPage/ProductListPage";
import ProductDetailsPage from "./pages/ProductDetailsPage/ProductDetailsPage";
import CartPage from "./pages/CartPage/CartPage";
import CheckoutPage from "./pages/CheckoutPage/CheckoutPage";
import AccountPage from "./pages/AccountPage/AccountPage";
import OrderHistoryPage from "./pages/OrderHistoryPage/OrderHistoryPage";
import OrderDetailsPage from "./pages/OrderDetailsPage/OrderDetailsPage";
import AdminDashboardPage from "./pages/admin/AdminDashboardPage/AdminDashboardPage";
import ProductManagementPage from "./pages/admin/ProductManagement/ProductManagementPage";
import UserManagementPage from "./pages/admin/UserManagementPage/UserManagementPage";
import OrderFulfillmentPage from "./pages/employee/OrderFulfillmentPage/OrderFulfillmentPage";

// Components
import PrivateRoute from "./components/PrivateRoute";
import RoleBasedRoute from "./components/RoleBasedRoute";
import ProductForm from "./components/ProductForm";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/products" element={<ProductListPage />} />
            <Route path="/product/:id" element={<ProductDetailsPage />} />

            {/* Protected User Routes */}
            <Route element={<PrivateRoute />}>
              <Route path="/cart" element={<CartPage />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/account" element={<AccountPage />} />
              <Route path="/orders" element={<OrderHistoryPage />} />
              <Route path="/orders/:id" element={<OrderDetailsPage />} />
            </Route>

            {/* Admin Routes */}
            <Route element={<RoleBasedRoute allowedRoles={["ADMIN"]} />}>
              <Route path="/admin" element={<AdminDashboardPage />} />
              <Route
                path="/admin/products"
                element={<ProductManagementPage />}
              />
              <Route path="/admin/products/new" element={<ProductForm />} />
              <Route path="/admin/users" element={<UserManagementPage />} />
            </Route>

            {/* Employee Routes */}
            <Route
              element={<RoleBasedRoute allowedRoles={["EMPLOYEE", "ADMIN"]} />}
            >
              <Route
                path="/employee/fulfillment"
                element={<OrderFulfillmentPage />}
              />
            </Route>
          </Routes>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
