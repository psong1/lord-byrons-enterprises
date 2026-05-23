import api from "./axiosConfig";

/** Employee: own paycheck history */
export const getMyPaychecks = async () => {
  const response = await api.get("/paychecks/me");
  return response.data;
};

/** Admin: all payroll runs */
export const getAllPayrolls = async () => {
  const response = await api.get("/payroll");
  return response.data;
};

/** Admin: paychecks for one payroll run */
export const getPaychecksForPayroll = async (payrollId) => {
  const response = await api.get(`/payroll/${payrollId}/paychecks`);
  return response.data;
};

/** Admin: create and process a pay period */
export const processPayroll = async (payload) => {
  const response = await api.post("/payroll/process", payload);
  return response.data;
};
