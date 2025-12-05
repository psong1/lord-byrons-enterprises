import React, { useState, useEffect } from "react";
import Navbar from "../../../components/Navbar";
import AdminNavbar from "../../../components/AdminNavbar";
import * as userService from "../../../api/userService";
import "./UserManagementPage.css";

const UserManagementPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState({});

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await userService.getAllUsers();
      setUsers(data);
    } catch (error) {
      console.error("Failed to load users", error);
    } finally {
      setLoading(false);
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    try {
      setUpdating({ ...updating, [userId]: true });
      const updatedUser = await userService.updateUserRole(userId, newRole);
      setUsers(users.map((user) => (user.id === userId ? updatedUser : user)));
      alert(`User role updated to ${newRole}`);
    } catch (error) {
      console.error("Failed to update user role", error);
      alert("Failed to update user role. Please try again.");
    } finally {
      setUpdating({ ...updating, [userId]: false });
    }
  };

  if (loading) {
    return (
      <div>
        <Navbar />
        <AdminNavbar />
        <div className="admin-dashboard">
          <h2>User Management</h2>
          <p>Loading users...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <AdminNavbar />
      <div className="admin-dashboard">
        <h2>User Management</h2>
        <table className="user-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>
                  <span className={`role-badge role-${user.role}`}>
                    {user.role}
                  </span>
                </td>
                <td>
                  <select
                    value={user.role}
                    onChange={(e) => handleRoleChange(user.id, e.target.value)}
                    disabled={updating[user.id]}
                    className="role-select"
                  >
                    <option value="CUSTOMER">CUSTOMER</option>
                    <option value="EMPLOYEE">EMPLOYEE</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                  {updating[user.id] && (
                    <span className="updating-text">Updating...</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UserManagementPage;
