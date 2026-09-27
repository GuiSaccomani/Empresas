import axios from 'axios';

// Instância separada para rota pública (sem interceptor de token)
const publicApi = axios.create({
  baseURL: 'http://localhost:8080/api/public',
});

export interface PublicCompanyInfo {
  name: string;
  slug: string;
}

export interface CreateBookingDTO {
  name: string;
  email: string;
  phone: string;
  scheduledTime: string; // ISO 8601
}

export const getCompanyInfo = async (slug: string): Promise<PublicCompanyInfo> => {
  const response = await publicApi.get(`/${slug}/info`);
  return response.data;
};

export const getAvailableSlots = async (slug: string, date: string): Promise<string[]> => {
  // date format: YYYY-MM-DD
  const response = await publicApi.get(`/${slug}/available-slots`, {
    params: { date }
  });
  return response.data;
};

export const createPublicAppointment = async (slug: string, data: CreateBookingDTO) => {
  const response = await publicApi.post(`/${slug}/appointments`, data);
  return response.data;
};