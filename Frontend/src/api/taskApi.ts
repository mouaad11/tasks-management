import { api } from './axiosConfig';
import type { TaskRequest, TaskResponse } from '@/types/task.types';
import type { PageResponse } from '@/types/pagination.types';

export const taskApi = {
  getByProject: async (projectId: number): Promise<TaskResponse[]> => {
    const response = await api.get<TaskResponse[]>(`/api/projects/${projectId}/tasks`);
    return response.data;
  },

  getByProjectPaginated: async (
    projectId: number,
    page: number = 0,
    size: number = 10,
    search?: string,
    completed?: boolean
  ): Promise<PageResponse<TaskResponse>> => {
    const params: Record<string, string | number | boolean> = {
      page,
      size,
      paginated: true,
    };
    if (search) params.search = search;
    if (completed !== undefined) params.completed = completed;

    const response = await api.get<PageResponse<TaskResponse>>(`/api/projects/${projectId}/tasks`, {
      params,
    });
    return response.data;
  },

  create: async (projectId: number, data: TaskRequest): Promise<TaskResponse> => {
    const response = await api.post<TaskResponse>(`/api/projects/${projectId}/tasks`, data);
    return response.data;
  },

  update: async (taskId: number, data: TaskRequest): Promise<TaskResponse> => {
    const response = await api.put<TaskResponse>(`/api/tasks/${taskId}`, data);
    return response.data;
  },

  updateStatus: async (taskId: number, completed: boolean): Promise<TaskResponse> => {
    const response = await api.patch<TaskResponse>(
      `/api/tasks/${taskId}/status?completed=${completed}`
    );
    return response.data;
  },

  delete: async (taskId: number): Promise<void> => {
    await api.delete(`/api/tasks/${taskId}`);
  },
};
