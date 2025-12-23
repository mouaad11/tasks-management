import { api } from './axiosConfig';
import type { LoginRequest, RegisterRequest, AuthResponse } from '@/types/auth.types';

export const authApi = {
  register: async (data: RegisterRequest): Promise<string> => {
    const response = await api.post<string>('/api/auth/register', data);
    return response.data;
  },

  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/api/auth/login', data);
    return response.data;
  },
};
