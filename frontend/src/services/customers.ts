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
};