import React, { useState, useEffect } from "react";
import * as userService from "../../../api/userService";
import "./UserManagementPage.css";

const ROLE_FILTERS = [
  { value: "ALL", label: "All" },
  { value: "CUSTOMER", label: "Customers" },
  { value: "EMPLOYEE", label: "Employees" },
  { value: "ADMIN", label: "Admins" },
];

const UserManagementPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState({});
  const [roleFilter, setRoleFilter] = useState("ALL");

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

  const filteredUsers =
    roleFilter === "ALL"
      ? users
      : users.filter((user) => user.role === roleFilter);

  if (loading) {
    return (
      <div className="user-management">
        <h2>User Management</h2>
        <p className="user-management-loading">Loading users...</p>
      </div>
    );
  }

  return (
    <div className="user-management">
      <h2>User Management</h2>

      <div className="um-toolbar">
        <fieldset className="role-filter-fieldset">
          <legend className="role-filter-legend">Filter by role</legend>
          <div className="role-filter-group" role="group" aria-label="Filter by role">
            {ROLE_FILTERS.map(({ value, label }) => (
              <button
                key={value}
                type="button"
                className={`role-filter-btn ${roleFilter === value ? "active" : ""}`}
                aria-pressed={roleFilter === value}
                onClick={() => setRoleFilter(value)}
              >
                {label}
              </button>
            ))}
          </div>
        </fieldset>
        <p className="um-result-count" aria-live="polite">
          Showing {filteredUsers.length} of {users.length} users
        </p>
      </div>

      {filteredUsers.length === 0 ? (
        <p className="empty-state">No users match this filter.</p>
      ) : (
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
          {filteredUsers.map((user) => (
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
      )}
    </div>
  );
};

export default UserManagementPage;
