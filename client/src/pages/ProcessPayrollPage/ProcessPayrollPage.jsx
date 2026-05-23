import React, { useState, useEffect } from "react";
import * as payrollService from "../../api/payrollService";
import * as userService from "../../api/userService";

const ProcessPayrollPage = () => {
  const [payrolls, setPayrolls] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [payPeriodEnd, setPayPeriodEnd] = useState("");
  const [selectedEmployeeIds, setSelectedEmployeeIds] = useState([]);
  const [processing, setProcessing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
      <h2>Process Payroll</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleProcess}>
        <label>
          Pay period end
          <input
            type="date"
            value={payPeriodEnd}
            onChange={(e) => setPayPeriodEnd(e.target.value)}
            required
          />
        </label>
        <fieldset>
          <legend>Employees (leave blank to process all employees)</legend>
          {employees.map((emp) => (
            <label key={emp.id}>
              <input
                type="checkbox"
                checked={selectedEmployeeIds.includes(emp.id)}
                onChange={() => toggleEmployee(emp.id)}
              />
              {emp.firstName} {emp.lastName} ({emp.username})
            </label>
          ))}
        </fieldset>
        <button type="submit" disabled={processing}>
          {processing ? "Processing..." : "Run Payroll"}
        </button>
      </form>

      <h3>Previous payroll runs</h3>
      {payrolls.length === 0 ? (
        <p>No payroll runs yet.</p>
      ) : (
        <ul>
          {payrolls.map((p) => (
            <li key={p.id}>
              {new Date(p.payPeriodEnd).toLocaleDateString()} — {p.status} (
              {p.paycheckCount} paychecks)
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ProcessPayrollPage;
