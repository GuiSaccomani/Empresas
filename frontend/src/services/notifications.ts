import { api } from './api';

export interface Notification {
  id: number;
  companyId: number;
  message: string;
  read: boolean;
  createdAt: string;
}

export const getNotifications = async (page = 0, size = 20) => {
  const response = await api.get(`/notifications?page=${page}&size=${size}`);
  return response.data;
};

export const markNotificationAsRead = async (id: number) => {
  await api.patch(`/notifications/${id}/read`);
};