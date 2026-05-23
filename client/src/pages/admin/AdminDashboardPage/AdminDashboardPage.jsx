import React, { useState, useEffect } from "react";
import "./AdminDashboardPage.css";

const AdminDashboardPage = () => {
  const [message, setMessage] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [tempMessage, setTempMessage] = useState("");

  useEffect(() => {
    const savedMessage = localStorage.getItem("adminMessageOfTheDay");
    if (savedMessage) {
      setMessage(savedMessage);
    } else {
      setMessage(
        "Welcome to the portal! Use the navigation above to manage orders, payroll, products, and users.",
      );
    }
  }, []);

  const handleEdit = () => {
    setTempMessage(message);
    setIsEditing(true);
  };

  const handleSave = () => {
    setMessage(tempMessage);
    localStorage.setItem("adminMessageOfTheDay", tempMessage);
    setIsEditing(false);
  };

  const handleCancel = () => {
    setTempMessage("");
    setIsEditing(false);
  };

  return (
    <div className="admin-dashboard">
      <h1>Portal Dashboard</h1>
      <div className="message-of-the-day">
        <div className="message-header">
          <h2>Message of the Day</h2>
          {!isEditing && (
            <button onClick={handleEdit} className="edit-message-btn">
              Edit
            </button>
          )}
        </div>
        {isEditing ? (
          <div className="message-editor">
            <textarea
              value={tempMessage}
              onChange={(e) => setTempMessage(e.target.value)}
              className="message-textarea"
              rows="4"
              placeholder="Enter your message of the day..."
            />
            <div className="message-actions">
              <button onClick={handleSave} className="save-btn">
                Save
              </button>
              <button onClick={handleCancel} className="cancel-btn">
                Cancel
              </button>
            </div>
          </div>
        ) : (
          <div className="message-display">
            <p>{message}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminDashboardPage;
