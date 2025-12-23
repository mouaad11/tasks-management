import React, { useState } from 'react';
import type { ReactNode } from 'react';
import { authApi } from '@/api/authApi';
import { storage } from '@/utils/storage';
import type { User, LoginRequest, RegisterRequest } from '@/types/auth.types';
import { useToast } from '@/components/ui/use-toast';
import { AuthContext } from './AuthContext.context';

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const token = storage.getToken();
    const userData = storage.getUser();
    if (token && userData) {
      return { username: userData.username, email: '' };
    }
    return null;
  });
  const [loading] = useState(false);
  const { toast } = useToast();

  const login = async (data: LoginRequest) => {
    try {
      const response = await authApi.login(data);
      storage.setToken(response.token);
      storage.setUser({ username: response.username });
      setUser({ username: response.username, email: data.email });
      toast({
        title: 'Success',
        description: 'Logged in successfully!',
      });
    } catch (error: unknown) {
      let message = 'Failed to login';
      if (error && typeof error === 'object' && 'response' in error) {
        const response = (error as { response?: { data?: unknown } }).response;
        if (response?.data) {
          if (typeof response.data === 'string') {
            message = response.data;
          } else if (typeof response.data === 'object' && response.data !== null) {
            const errorData = response.data as Record<string, unknown>;
            if (typeof errorData.error === 'string') {
              message = errorData.error;
            } else if (typeof errorData.message === 'string') {
              message = errorData.message;
            }
          }
        }
      }
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
      throw error;
    }
  };

  const register = async (data: RegisterRequest) => {
    try {
      await authApi.register(data);
      toast({
        title: 'Success',
        description: 'Account created successfully! Please login.',
      });
    } catch (error: unknown) {
      let message = 'Failed to register';
      if (error && typeof error === 'object' && 'response' in error) {
        const response = (error as { response?: { data?: unknown } }).response;
        if (response?.data) {
          if (typeof response.data === 'string') {
            message = response.data;
          } else if (typeof response.data === 'object' && response.data !== null) {
            const errorData = response.data as Record<string, unknown>;
            if (typeof errorData.error === 'string') {
              message = errorData.error;
            } else if (typeof errorData.message === 'string') {
              message = errorData.message;
            }
          }
        }
      }
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
      throw error;
    }
  };

  const logout = () => {
    storage.clear();
    setUser(null);
    toast({
      title: 'Logged out',
      description: 'You have been logged out successfully.',
    });
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        register,
        logout,
        isAuthenticated: !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
