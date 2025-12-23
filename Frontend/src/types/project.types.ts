export interface ProjectRequest {
  title: string;
  description?: string;
}

export interface ProjectResponse {
  id: number;
  title: string;
  description?: string;
  createdAt: string;
  totalTasks: number;
  completedTasks: number;
  progressPercentage: number;
}
