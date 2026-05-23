import React, { useState, useEffect } from "react";
import * as payrollService from "../../api/payrollService";
import * as userService from "../../api/userService";
import "./ProcessPayrollPage.css";

const ProcessPayrollPage = () => {
  const [payrolls, setPayrolls] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [payPeriodEnd, setPayPeriodEnd] = useState("");
  const [selectedEmployeeIds, setSelectedEmployeeIds] = useState([]);
  const [processing, setProcessing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedPayrollId, setSelectedPayrollId] = useState(null);
  const [currentPaychecks, setCurrentPaychecks] = useState([]);
  const [ledgerLoading, setLedgerLoading] = useState(false);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const [payrollData, users] = await Promise.all([
          payrollService.getAllPayrolls(),
          userService.getAllUsers(),
        ]);
        setPayrolls(payrollData);
        setEmployees(users.filter((u) => u.role === "EMPLOYEE"));
      } catch (err) {
        console.error("Failed to load payroll data", err);
        setError("Could not load payroll data.");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const toggleEmployee = (id) => {
    setSelectedEmployeeIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id],
    );
  };

  const handleProcess = async (e) => {
    e.preventDefault();
    try {
      setProcessing(true);
      setError(null);
      await payrollService.processPayroll({
        payPeriodEnd,
        employeeIds:
          selectedEmployeeIds.length > 0 ? selectedEmployeeIds : null,
      });
      alert("Payroll processed successfully");
      setPayrolls(await payrollService.getAllPayrolls());
      setPayPeriodEnd("");
      setSelectedEmployeeIds([]);
    } catch (err) {
      console.error("Failed to process payroll", err);
      alert("Failed to process payroll.");
    } finally {
      setProcessing(false);
    }
  };

  const handleViewLedger = async (payrollId) => {
    setSelectedPayrollId(payrollId);
    setLedgerLoading(true);
    try {
      const checks = await payrollService.getPaychecksForPayroll(payrollId);
      setCurrentPaychecks(checks);
    } catch (err) {
      console.error("Failed to load ledger", err);
      alert("Failed to load ledger details.");
    } finally {
      setLedgerLoading(false);
    }
  };

  const handleDisburse = async (paycheckId) => {
    try {
      await payrollService.disbursePaycheck(paycheckId);
      setCurrentPaychecks((prev) =>
        prev.map((pc) =>
          pc.id === paycheckId ? { ...pc, status: "PAID" } : pc,
        ),
      );
    } catch (err) {
      console.error("Failed to disburse paycheck", err);
      alert(err.response?.data?.message || "Failed to disburse paycheck.");
    }
  };

  const totalPendingCash = currentPaychecks
    .filter((pc) => pc.status === "PENDING")
    .reduce((sum, pc) => sum + Number(pc.netPay), 0);

  const totalDisbursedCash = currentPaychecks
    .filter((pc) => pc.status === "PAID")
    .reduce((sum, pc) => sum + Number(pc.grossPay), 0);

  if (loading) {
    return (
      <div className="process-payroll-page">
        <h2>Process Payroll</h2>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="process-payroll-page">
      <h2 className="process-payroll-title">Process Payroll</h2>
      {error && <p className="error-message">{error}</p>}

      <form className="payroll-form" onSubmit={handleProcess}>
        <label className="form-label">
          Pay period end
          <input
            type="date"
            className="form-input"
            value={payPeriodEnd}
            onChange={(e) => setPayPeriodEnd(e.target.value)}
            required
          />
        </label>
        <fieldset className="employee-fieldset">
          <legend>Employees (leave blank to process all employees)</legend>
          {employees.map((emp) => (
            <label key={emp.id} className="employee-checkbox-label">
              <input
                type="checkbox"
                checked={selectedEmployeeIds.includes(emp.id)}
                onChange={() => toggleEmployee(emp.id)}
              />
              {emp.firstName} {emp.lastName} ({emp.username})
            </label>
          ))}
        </fieldset>
        <button type="submit" className="submit-btn" disabled={processing}>
          {processing ? "Processing..." : "Run Payroll"}
        </button>
      </form>

      <hr className="divider" />

      <div className="ledger-container">
        <div className="ledger-sidebar">
          <h3>Payroll History</h3>
          {payrolls.length === 0 ? (
            <p>No payroll runs yet.</p>
          ) : (
            <ul className="payroll-list">
              {payrolls.map((p) => (
                <li key={p.id} className="payroll-list-item">
                  <button
                    onClick={() => handleViewLedger(p.id)}
                    className={`payroll-run-btn ${
                      selectedPayrollId === p.id ? "active" : ""
                    }`}
                  >
                    <strong>
                      Week Ending:{" "}
                      {new Date(p.payPeriodEnd).toLocaleDateString()}
                    </strong>
                    <br />
                    <small>
                      {p.paycheckCount} checks — Status: {p.status}
                    </small>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="ledger-main">
          {!selectedPayrollId ? (
            <p className="empty-state">
              Select a payroll run to view the Cash Ledger.
            </p>
          ) : ledgerLoading ? (
            <p>Loading ledger...</p>
          ) : (
            <>
              <h3>Cash Ledger Details</h3>
              <div className="summary-cards">
                <div className="summary-card">
                  <p className="summary-label">Total Cash Needed (Pending)</p>
                  <h2 className="summary-value pending">
                    ${totalPendingCash.toFixed(2)}
                  </h2>
                </div>
                <div className="summary-card align-right">
                  <p className="summary-label">Already Disbursed</p>
                  <h2 className="summary-value success">
                    ${totalDisbursedCash.toFixed(2)}
                  </h2>
                </div>
              </div>

              <table className="ledger-table">
                <thead>
                  <tr>
                    <th>Employee</th>
                    <th>Gross</th>
                    <th>NIB (3.4%)</th>
                    <th>Net Cash</th>
                    <th className="align-right">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {currentPaychecks.map((pc) => (
                    <tr key={pc.id}>
                      <td>{pc.employeeName}</td>
                      {/* Enforcing Number wrapper ensures safe formatting */}
                      <td>${Number(pc.grossPay).toFixed(2)}</td>
                      <td className="deduction-text">
                        -${Number(pc.deductions).toFixed(2)}
                      </td>
                      <td className="net-pay-text">
                        ${Number(pc.netPay).toFixed(2)}
                      </td>
                      <td className="align-right">
                        {pc.status === "PENDING" ? (
                          <button
                            onClick={() => handleDisburse(pc.id)}
                            className="disburse-btn"
                          >
                            Mark as Disbursed
                          </button>
                        ) : (
                          <span className="status-badge paid">✓ PAID</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProcessPayrollPage;
