import { useState, useEffect } from 'react';
import { projectApi } from '@/api/projectApi';
import type { ProjectResponse } from '@/types/project.types';
import type { PageResponse } from '@/types/pagination.types';

export const useProjectsPaginated = (page: number = 0, size: number = 10) => {
  const [data, setData] = useState<PageResponse<ProjectResponse> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchProjects = async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await projectApi.getAllPaginated(page, size);
      setData(result);
    } catch (err: unknown) {
      const message = err && typeof err === 'object' && 'response' in err
        ? (err as { response?: { data?: string } }).response?.data || 'Failed to fetch projects'
        : 'Failed to fetch projects';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjects();
  }, [page, size]);

  return { data, loading, error, refetch: fetchProjects };
};
