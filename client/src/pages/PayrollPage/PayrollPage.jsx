import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import * as payrollService from "../../api/payrollService";
import "./PayrollPage.css";

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
        <p>Loading your digital payslips...</p>
      </div>
    );
  }

  return (
    <div className="payroll-page">
      <div className="payroll-header">
        <h2>My Digital Payslips</h2>
        {user?.role === "ADMIN" && (
          <Link to="/portal/process-payroll" className="admin-process-link">
            Process Payroll
          </Link>
        )}
      </div>

      {error && <p className="error-message">{error}</p>}

      {paychecks.length === 0 ? (
        <div className="empty-state">
          <p>No payslips generated yet.</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="paychecks-table">
            <thead>
              <tr>
                <th>Week Ending</th>
                <th>Gross Pay</th>
                <th>NIG (3.4%)</th>
                <th>Net Cash</th>
                <th className="align-right">Status</th>
              </tr>
            </thead>
            <tbody>
              {paychecks.map((pc) => (
                <tr key={pc.id}>
                  <td className="date-cell">
                    {new Date(pc.payPeriodEnd).toLocaleDateString(undefined, {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                    })}
                  </td>
                  <td>${Number(pc.grossPay).toFixed(2)}</td>
                  <td className="deduction-cell">
                    - ${Number(pc.deductions).toFixed(2)}
                  </td>
                  <td className="net-cash-cell">
                    ${Number(pc.netPay).toFixed(2)}
                  </td>
                  <td
                    className={`status-badge ${pc.status === "PAID" ? "paid" : "pending"}`}
                  >
                    {pc.status === "PAID" ? "DISBURSED" : "PENDING"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default PayrollPage;
