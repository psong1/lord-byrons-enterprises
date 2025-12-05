import React from "react";
import "./AddressCard.css";

const AddressCard = ({ address, onEdit, onDelete }) => {
  return (
    <div className="address-card">
      <div className="address-header">{address.type}</div>
      <div className="address-body">
        <p>{address.line1}</p>
        {address.line2 && <p>{address.line2}</p>}
        <p>
          {address.city}, {address.country}
        </p>
      </div>
      <div className="address-footer">
        {onEdit && (
          <button onClick={() => onEdit(address)} className="btn-edit">
            Edit
          </button>
        )}
        {onDelete && (
          <button onClick={() => onDelete(address.id)} className="btn-delete">
            Delete
          </button>
        )}
      </div>
    </div>
  );
};

export default AddressCard;
