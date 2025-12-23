import { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { projectApi } from '@/api/projectApi';
import { taskApi } from '@/api/taskApi';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from '@/components/ui/alert-dialog';
import { TaskList } from '@/components/tasks/TaskList';
import { Progress } from '@/components/ui/progress';
import { Pagination } from '@/components/ui/pagination';
import { ArrowLeft, Plus, Trash2, Search, X, Edit } from 'lucide-react';
import { useToast } from '@/components/ui/use-toast';
import { useDebounce } from '@/hooks/useDebounce';
import { formatDate } from '@/utils/formatDate';
import type { ProjectResponse, ProjectRequest } from '@/types/project.types';
import type { TaskResponse, TaskRequest } from '@/types/task.types';
import type { PageResponse } from '@/types/pagination.types';

export const ProjectDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const [project, setProject] = useState<ProjectResponse | null>(null);
  const [tasksData, setTasksData] = useState<PageResponse<TaskResponse> | null>(null);
  const [loading, setLoading] = useState(true);
  const [taskDialogOpen, setTaskDialogOpen] = useState(false);
  const [taskFormData, setTaskFormData] = useState<TaskRequest>({
    title: '',
    description: '',
    dueDate: '',
  });
  const [submitting, setSubmitting] = useState(false);
  const [taskPage, setTaskPage] = useState(0);
  const [taskSize] = useState(10);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterCompleted, setFilterCompleted] = useState<boolean | undefined>(undefined);
  const [editProjectDialogOpen, setEditProjectDialogOpen] = useState(false);
  const [deleteProjectDialogOpen, setDeleteProjectDialogOpen] = useState(false);
  const [editProjectData, setEditProjectData] = useState<ProjectRequest>({ title: '', description: '' });
  const searchInputRef = useRef<HTMLInputElement>(null);
  
  const debouncedSearch = useDebounce(searchQuery, 500);

  const fetchProject = async () => {
    if (!id) return;
    try {
      const data = await projectApi.getById(Number(id));
      setProject(data);
    } catch (error: unknown) {
      const message = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: string } }).response?.data || 'Failed to load project'
        : 'Failed to load project';
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
      navigate('/dashboard');
    }
  };

  const fetchTasks = async () => {
    if (!id) return;
    try {
      const data = await taskApi.getByProjectPaginated(
        Number(id),
        taskPage,
        taskSize,
        debouncedSearch || undefined,
        filterCompleted
      );
      setTasksData(data);
    } catch (error: unknown) {
      const message = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: string } }).response?.data || 'Failed to load tasks'
        : 'Failed to load tasks';
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
    }
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      await Promise.all([fetchProject(), fetchTasks()]);
      setLoading(false);
    };
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id, taskPage, debouncedSearch, filterCompleted]);

  useEffect(() => {
    if (taskPage !== 0) {
      setTaskPage(0);
    }
  }, [debouncedSearch]);

  const handleCreateTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    
    setSubmitting(true);
    try {
      const taskData = {
        ...taskFormData,
        dueDate: taskFormData.dueDate || undefined,
      };
      await taskApi.create(Number(id), taskData);
      setTaskDialogOpen(false);
      setTaskFormData({ title: '', description: '', dueDate: '' });
      setTaskPage(0);
      await Promise.all([fetchProject(), fetchTasks()]);
      toast({
        title: 'Success',
        description: 'Task created successfully!',
      });
    } catch (error: unknown) {
      const message = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: string } }).response?.data || 'Failed to create task'
        : 'Failed to create task';
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handleEditProject = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    
    setSubmitting(true);
    try {
      const updated = await projectApi.update(Number(id), editProjectData);
      setProject(updated);
      setEditProjectDialogOpen(false);
      toast({
        title: 'Success',
        description: 'Project updated successfully!',
      });
    } catch (error: unknown) {
      const message = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: string } }).response?.data || 'Failed to update project'
        : 'Failed to update project';
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteProject = async () => {
    if (!id) return;

    try {
      await projectApi.delete(Number(id));
      toast({
        title: 'Success',
        description: 'Project deleted successfully',
      });
      navigate('/dashboard');
    } catch (error: unknown) {
      const message = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: string } }).response?.data || 'Failed to delete project'
        : 'Failed to delete project';
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
    }
  };

  const openEditProjectDialog = () => {
    if (project) {
      setEditProjectData({
        title: project.title,
        description: project.description || '',
      });
      setEditProjectDialogOpen(true);
    }
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
            <p className="mt-4 text-muted-foreground">Loading project...</p>
          </div>
        </div>
      </div>
    );
  }

  if (!project) {
    return null;
  }

  return (
    <div className="container mx-auto p-6 max-w-4xl">
      <div className="mb-6">
        <Button variant="ghost" asChild>
          <Link to="/dashboard">
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Dashboard
          </Link>
        </Button>
      </div>

      <Card className="mb-6">
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <CardTitle className="text-3xl mb-2">{project.title}</CardTitle>
              <CardDescription className="text-base">
                {project.description || 'No description'}
              </CardDescription>
              <div className="flex items-center gap-4 mt-4 text-sm text-muted-foreground">
                <span>Created {formatDate(project.createdAt)}</span>
              </div>
            </div>
            <div className="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={openEditProjectDialog}
              >
                <Edit className="h-4 w-4 mr-2" />
                Edit
              </Button>
              <AlertDialog open={deleteProjectDialogOpen} onOpenChange={setDeleteProjectDialogOpen}>
                <AlertDialogTrigger asChild>
                  <Button 
                    variant="destructive" 
                    size="sm"
                  >
                    <Trash2 className="h-4 w-4 mr-2" />
                    Delete Project
                  </Button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>Are you sure?</AlertDialogTitle>
                    <AlertDialogDescription>
                      This action cannot be undone. This will permanently delete the project
                      and all of its tasks.
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel>Cancel</AlertDialogCancel>
                    <AlertDialogAction
                      onClick={handleDeleteProject}
                      className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                    >
                      Delete
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground">Progress</span>
              <span className="font-medium">{project.progressPercentage.toFixed(0)}%</span>
            </div>
            <Progress value={project.progressPercentage} />
            <div className="flex items-center justify-between text-sm text-muted-foreground">
              <span>{project.completedTasks} of {project.totalTasks} tasks completed</span>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Edit Project Dialog */}
      <Dialog open={editProjectDialogOpen} onOpenChange={setEditProjectDialogOpen}>
        <DialogContent>
          <form onSubmit={handleEditProject}>
            <DialogHeader>
              <DialogTitle>Edit Project</DialogTitle>
              <DialogDescription>
                Update your project information
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="edit-project-title">Project Title *</Label>
                <Input
                  id="edit-project-title"
                  placeholder="My Awesome Project"
                  value={editProjectData.title}
                  onChange={(e) =>
                    setEditProjectData({ ...editProjectData, title: e.target.value })
                  }
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="edit-project-description">Description</Label>
                <Textarea
                  id="edit-project-description"
                  placeholder="What is this project about?"
                  value={editProjectData.description}
                  onChange={(e) =>
                    setEditProjectData({ ...editProjectData, description: e.target.value })
                  }
                  rows={5}
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setEditProjectDialogOpen(false)}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={submitting}>
                {submitting ? 'Saving...' : 'Save Changes'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div>
              <CardTitle>Tasks</CardTitle>
              <CardDescription>Manage your project tasks</CardDescription>
            </div>
            <Dialog open={taskDialogOpen} onOpenChange={setTaskDialogOpen}>
              <DialogTrigger asChild>
                <Button>
                  <Plus className="h-4 w-4 mr-2" />
                  Add Task
                </Button>
              </DialogTrigger>
              <DialogContent>
                <form onSubmit={handleCreateTask}>
                  <DialogHeader>
                    <DialogTitle>Create New Task</DialogTitle>
                    <DialogDescription>
                      Add a new task to this project
                    </DialogDescription>
                  </DialogHeader>
                  <div className="space-y-4 py-4">
                    <div className="space-y-2">
                      <Label htmlFor="task-title">Task Title *</Label>
                      <Input
                        id="task-title"
                        placeholder="Task name"
                        value={taskFormData.title}
                        onChange={(e) =>
                          setTaskFormData({ ...taskFormData, title: e.target.value })
                        }
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="task-description">Description</Label>
                      <Textarea
                        id="task-description"
                        placeholder="Task description"
                        value={taskFormData.description}
                        onChange={(e) =>
                          setTaskFormData({ ...taskFormData, description: e.target.value })
                        }
                        rows={3}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="task-due-date">Due Date</Label>
                      <Input
                        id="task-due-date"
                        type="datetime-local"
                        value={taskFormData.dueDate}
                        onChange={(e) =>
                          setTaskFormData({ ...taskFormData, dueDate: e.target.value })
                        }
                      />
                    </div>
                  </div>
                  <DialogFooter>
                    <Button
                      type="button"
                      variant="outline"
                      onClick={() => setTaskDialogOpen(false)}
                    >
                      Cancel
                    </Button>
                    <Button type="submit" disabled={submitting}>
                      {submitting ? 'Creating...' : 'Create Task'}
                    </Button>
                  </DialogFooter>
                </form>
              </DialogContent>
            </Dialog>
          </div>
        </CardHeader>
        <CardContent>
          {/* Search and Filter */}
          <div className="mb-6 space-y-4">
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  ref={searchInputRef}
                  placeholder="Search tasks..."
                  value={searchQuery}
                  onChange={(e) => {
                    setSearchQuery(e.target.value);
                  }}
                  onFocus={(e) => e.target.select()}
                  className="pl-10 pr-10"
                  disabled={false}
                  readOnly={false}
                />
                {searchQuery && (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="absolute right-1 top-1/2 transform -translate-y-1/2 h-6 w-6"
                    onClick={() => {
                      setSearchQuery('');
                      searchInputRef.current?.focus();
                    }}
                  >
                    <X className="h-4 w-4" />
                  </Button>
                )}
              </div>
              <div className="flex gap-2">
                <Button
                  variant={filterCompleted === undefined ? "default" : "outline"}
                  size="sm"
                  onClick={() => {
                    setFilterCompleted(undefined);
                    setTaskPage(0);
                  }}
                >
                  All
                </Button>
                <Button
                  variant={filterCompleted === false ? "default" : "outline"}
                  size="sm"
                  onClick={() => {
                    setFilterCompleted(false);
                    setTaskPage(0);
                  }}
                >
                  Pending
                </Button>
                <Button
                  variant={filterCompleted === true ? "default" : "outline"}
                  size="sm"
                  onClick={() => {
                    setFilterCompleted(true);
                    setTaskPage(0);
                  }}
                >
                  Completed
                </Button>
              </div>
            </div>
          </div>

          {tasksData && (
            <>
              <TaskList
                tasks={tasksData.content}
                onTaskUpdate={() => {
                  fetchProject();
                  fetchTasks();
                }}
                onTaskDelete={() => {
                  fetchProject();
                  fetchTasks();
                }}
              />

              {tasksData.totalPages > 1 && (
                <div className="mt-6">
                  <Pagination
                    currentPage={taskPage}
                    totalPages={tasksData.totalPages}
                    onPageChange={(newPage) => {
                      setTaskPage(newPage);
                      window.scrollTo({ top: 0, behavior: 'smooth' });
                    }}
                  />
                  <p className="text-sm text-center text-muted-foreground mt-4">
                    Showing {tasksData.page * tasksData.size + 1} to{' '}
                    {Math.min((tasksData.page + 1) * tasksData.size, tasksData.totalElements)} of{' '}
                    {tasksData.totalElements} tasks
                  </p>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
