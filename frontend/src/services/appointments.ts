import { api } from './api';

export interface Appointment {
  id: string;
  customerId: string;
  customerName: string;
  employeeId?: string;
  employeeName?: string;
  scheduledTime: string;
  status: 'SCHEDULED' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  notes?: string;
}

export interface CreateAppointmentDTO {
  customerId: string;
  employeeId?: string;
  scheduledTime: string;
  notes?: string;
}

export const getAppointmentsByRange = async (start: string, end: string) => {
  const response = await api.get<Appointment[]>('/appointments/range', {
    params: { start, end }
  });
  return response.data;
};

export const createAppointment = async (data: CreateAppointmentDTO) => {
  const response = await api.post<Appointment>('/appointments', data);
  return response.data;
};

export const updateAppointmentStatus = async (id: string, status: string, amount?: number) => {
  const response = await api.patch<Appointment>(`/appointments/${id}/status`, { status, amount });
  return response.data;
};// cache buster 1


export const sendReminder = async (id: string) => {
  await api.post(`/appointments/${id}/send-reminder`);
};
export const deleteAppointment = async (id: string) => {
  await api.delete(`/appointments/${id}`);
};
