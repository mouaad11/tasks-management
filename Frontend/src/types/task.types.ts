export interface TaskRequest {
  title: string;
  description?: string;
  dueDate?: string;
}

export interface TaskResponse {
  id: number;
  title: string;
  description?: string;
  completed: boolean;
  dueDate?: string;
  createdAt: string;
  projectId: number;
}
