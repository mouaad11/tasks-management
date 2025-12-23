import { useState } from 'react';
import { taskApi } from '@/api/taskApi';
import { Checkbox } from '@/components/ui/checkbox';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from '@/components/ui/alert-dialog';
import { Trash2, Calendar, Edit } from 'lucide-react';
import { formatDate, isOverdue } from '@/utils/formatDate';
import { useToast } from '@/components/ui/use-toast';
import type { TaskResponse, TaskRequest } from '@/types/task.types';

interface TaskItemProps {
  task: TaskResponse;
  onUpdate: () => void;
  onDelete: () => void;
}

export const TaskItem: React.FC<TaskItemProps> = ({ task, onUpdate, onDelete }) => {
  const [loading, setLoading] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editTaskData, setEditTaskData] = useState<TaskRequest>({
    title: task.title,
    description: task.description || '',
    dueDate: task.dueDate ? new Date(task.dueDate).toISOString().slice(0, 16) : '',
  });
  const { toast } = useToast();

  const handleToggle = async () => {
    setLoading(true);
    try {
      await taskApi.updateStatus(task.id, !task.completed);
      onUpdate();
      toast({
        title: 'Success',
        description: `Task marked as ${!task.completed ? 'completed' : 'incomplete'}`,
      });
    } catch (error: unknown) {
      const message = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: string } }).response?.data || 'Failed to update task'
        : 'Failed to update task';
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const taskData = {
        ...editTaskData,
        dueDate: editTaskData.dueDate || undefined,
      };
      await taskApi.update(task.id, taskData);
      setEditDialogOpen(false);
      onUpdate();
      toast({
        title: 'Success',
        description: 'Task updated successfully!',
      });
    } catch (error: unknown) {
      const message = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: string } }).response?.data || 'Failed to update task'
        : 'Failed to update task';
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    setLoading(true);
    try {
      await taskApi.delete(task.id);
      setDeleteDialogOpen(false);
      onDelete();
      toast({
        title: 'Success',
        description: 'Task deleted successfully',
      });
    } catch (error: unknown) {
      const message = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: string } }).response?.data || 'Failed to delete task'
        : 'Failed to delete task';
      toast({
        title: 'Error',
        description: message,
        variant: 'destructive',
      });
    } finally {
      setLoading(false);
    }
  };

  const openEditDialog = () => {
    setEditTaskData({
      title: task.title,
      description: task.description || '',
      dueDate: task.dueDate ? new Date(task.dueDate).toISOString().slice(0, 16) : '',
    });
    setEditDialogOpen(true);
  };

  const overdue = task.dueDate ? isOverdue(task.dueDate) : false;

  return (
    <Card className={task.completed ? 'opacity-60' : ''}>
      <CardContent className="p-4">
        <div className="flex items-start gap-3">
          <Checkbox
            checked={task.completed}
            onCheckedChange={handleToggle}
            disabled={loading}
            className="mt-1"
          />
          <div className="flex-1 min-w-0">
            <div className="flex items-start justify-between gap-2">
              <div className="flex-1">
                <h3
                  className={`font-medium ${
                    task.completed ? 'line-through text-muted-foreground' : ''
                  }`}
                >
                  {task.title}
                </h3>
                {task.description && (
                  <p className="text-sm text-muted-foreground mt-1">
                    {task.description}
                  </p>
                )}
                {task.dueDate && (
                  <div className="flex items-center gap-1 mt-2">
                    <Calendar className={`h-3 w-3 ${overdue && !task.completed ? 'text-destructive' : 'text-muted-foreground'}`} />
                    <span
                      className={`text-xs ${
                        overdue && !task.completed
                          ? 'text-destructive font-medium'
                          : 'text-muted-foreground'
                      }`}
                    >
                      Due: {formatDate(task.dueDate)}
                      {overdue && !task.completed && ' (Overdue)'}
                    </span>
                  </div>
                )}
              </div>
              <div className="flex gap-1">
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={openEditDialog}
                  disabled={loading}
                  className="h-8 w-8"
                >
                  <Edit className="h-4 w-4" />
                </Button>
                <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
                  <AlertDialogTrigger asChild>
                    <Button
                      variant="ghost"
                      size="icon"
                      disabled={loading}
                      className="text-destructive hover:text-destructive hover:bg-destructive/10 h-8 w-8"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>Are you sure?</AlertDialogTitle>
                      <AlertDialogDescription>
                        This action cannot be undone. This will permanently delete the task.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel>Cancel</AlertDialogCancel>
                      <AlertDialogAction
                        onClick={handleDelete}
                        className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                      >
                        Delete
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </div>
            </div>
          </div>
        </div>
      </CardContent>

      {/* Edit Task Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent>
          <form onSubmit={handleEdit}>
            <DialogHeader>
              <DialogTitle>Edit Task</DialogTitle>
              <DialogDescription>
                Update your task information
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="edit-task-title">Task Title *</Label>
                <Input
                  id="edit-task-title"
                  placeholder="Task name"
                  value={editTaskData.title}
                  onChange={(e) =>
                    setEditTaskData({ ...editTaskData, title: e.target.value })
                  }
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="edit-task-description">Description</Label>
                <Textarea
                  id="edit-task-description"
                  placeholder="Task description"
                  value={editTaskData.description}
                  onChange={(e) =>
                    setEditTaskData({ ...editTaskData, description: e.target.value })
                  }
                  rows={3}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="edit-task-due-date">Due Date</Label>
                <Input
                  id="edit-task-due-date"
                  type="datetime-local"
                  value={editTaskData.dueDate}
                  onChange={(e) =>
                    setEditTaskData({ ...editTaskData, dueDate: e.target.value })
                  }
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setEditDialogOpen(false)}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? 'Saving...' : 'Save Changes'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </Card>
  );
};
