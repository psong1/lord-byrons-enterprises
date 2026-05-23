import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import * as payrollService from "../../api/payrollService";
// import "./PayrollPage.css";

const PayrollPage = () => {
  const { user } = useAuth();
  const [paychecks, setPaychecks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const data = await payrollService.getMyPaychecks();
        setPaychecks(data);
      } catch (error) {
        console.error("Failed to load paychecks", error);
        setError("Could not load paychecks.");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) {
    return (
      <div className="payroll-page">
        <p>Loading paychecks...</p>
      </div>
    );
  }

  return (
    <div className="payroll-page">
      <h2>My Paychecks</h2>
      {user?.role === "ADMIN" && (
        <p>
          <Link to="/portal/process-payroll">Process Payroll</Link>
        </p>
      )}
      {error && <p className="error">{error}</p>}
      {paychecks.length === 0 ? (
        <p>No paychecks yet.</p>
      ) : (
        <table className="paychecks-table">
          <thead>
            <tr>
              <th>Pay Period End</th>
              <th>Gross Pay</th>
              <th>Deductions</th>
              <th>Net Pay</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {paychecks.map((pc) => (
              <tr key={pc.id}>
                <td>{new Date(pc.payPeriodEnd).toLocaleDateString()}</td>
                <td>${Number(pc.grossPay).toFixed(2)}</td>
                <td>${Number(pc.deductions).toFixed(2)}</td>
                <td>${Number(pc.netPay).toFixed(2)}</td>
                <td>{pc.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default PayrollPage;
