import { api } from './api';

export interface Customer {
  id: string;
  name: string;
  email: string;
  phone: string;
  createdAt: string;
}

export const getCustomers = async (page = 0, size = 10) => {
  const response = await api.get(`/customers?page=${page}&size=${size}`);
  return response.data;
};

export const createCustomer = async (data: { name: string; email: string; phone: string }) => {
  const response = await api.post('/customers', data);
  return response.data;
};export const updateCustomer = async (id: string, data: Omit<any, 'companyId'>) => {
  const response = await api.put<Customer>(`/customers/${id}`, data);
  return response.data;
};

export const deleteCustomer = async (id: string) => {
  await api.delete(`/customers/${id}`);
};
