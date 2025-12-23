import { api } from './axiosConfig';
import type { ProjectRequest, ProjectResponse } from '@/types/project.types';
import type { PageResponse } from '@/types/pagination.types';

export const projectApi = {
  getAll: async (): Promise<ProjectResponse[]> => {
    const response = await api.get<ProjectResponse[]>('/api/projects');
    return response.data;
  },

  getAllPaginated: async (page: number = 0, size: number = 10): Promise<PageResponse<ProjectResponse>> => {
    const response = await api.get<PageResponse<ProjectResponse>>('/api/projects', {
      params: { page, size, paginated: true },
    });
    return response.data;
  },

  getById: async (id: number): Promise<ProjectResponse> => {
    const response = await api.get<ProjectResponse>(`/api/projects/${id}`);
    return response.data;
  },

  create: async (data: ProjectRequest): Promise<ProjectResponse> => {
    const response = await api.post<ProjectResponse>('/api/projects', data);
    return response.data;
  },

  update: async (id: number, data: ProjectRequest): Promise<ProjectResponse> => {
    const response = await api.put<ProjectResponse>(`/api/projects/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/projects/${id}`);
  },
};
