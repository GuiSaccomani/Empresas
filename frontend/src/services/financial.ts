import { api } from './api';

export interface FinancialTransaction {
  id: string;
  amount: number;
  type: 'INCOME' | 'EXPENSE';
  description: string;
  transactionDate: string;
  createdAt: string;
}

export interface CreateFinancialTransactionDTO {
  amount: number;
  type: 'INCOME' | 'EXPENSE';
  description: string;
  transactionDate: string;
}

export const getTransactions = async (page: number = 0, size: number = 10) => {
  const response = await api.get('/financial', {
    params: { page, size }
  });
  return response.data;
};

export const getBalance = async () => {
  const response = await api.get<{ currentBalance: number }>('/financial/balance');
  return response.data;
};

export const createTransaction = async (data: CreateFinancialTransactionDTO) => {
  const response = await api.post<FinancialTransaction>('/financial', data);
  return response.data;
};

export const exportExcel = async () => {
  const response = await api.get('/financial/export/excel', {
    responseType: 'blob'
  });
  
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', 'extrato_financeiro.xlsx');
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};