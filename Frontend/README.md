# Project Manager Frontend

A modern, professional task and project management application built with React, TypeScript, and shadcn/ui.

## Features

- 🔐 **Authentication** - Secure login and registration
- 📁 **Project Management** - Create and manage projects
- ✅ **Task Management** - Add tasks with due dates and track completion
- 📊 **Dashboard** - Overview of all projects with progress tracking
- 🎨 **Modern UI** - Built with shadcn/ui components and Tailwind CSS
- 📱 **Responsive Design** - Works seamlessly on all devices

## Tech Stack

- **React 19** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **shadcn/ui** - High-quality component library
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **React Hook Form** - Form management

## Getting Started

### Prerequisites

- Node.js (v18 or higher)
- npm or yarn
- Backend API running (default: http://localhost:8080)

### Installation

1. Install dependencies:
```bash
npm install
```

2. Create a `.env` file in the Frontend directory:
```env
VITE_API_URL=http://localhost:8080
```

3. Start the development server:
```bash
npm run dev
```

The app will be available at `http://localhost:5173`

### Build for Production

```bash
npm run build
```

The production build will be in the `dist` directory.

## Project Structure

```
src/
├── api/              # API client functions
├── components/       # React components
│   ├── ui/          # shadcn/ui components
│   ├── layout/      # Layout components (Navbar, etc.)
│   └── tasks/       # Task-related components
├── context/         # React Context providers
├── hooks/           # Custom React hooks
├── pages/           # Page components
├── routes/          # Routing configuration
├── types/           # TypeScript type definitions
└── utils/           # Utility functions
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Usage

1. **Register/Login**: Create an account or login with existing credentials
2. **Create Projects**: Click "New Project" to create a project
3. **Add Tasks**: Open a project and click "Add Task" to create tasks
4. **Track Progress**: Mark tasks as complete to update project progress
5. **Monitor Dashboard**: View all projects and overall statistics on the dashboard

## API Integration

The frontend communicates with a Spring Boot backend API. Make sure the backend is running and accessible at the URL specified in `.env`.

### API Endpoints Used

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/projects` - Get all projects
- `POST /api/projects` - Create project
- `GET /api/projects/{id}` - Get project details
- `DELETE /api/projects/{id}` - Delete project
- `GET /api/projects/{id}/tasks` - Get project tasks
- `POST /api/projects/{id}/tasks` - Create task
- `PATCH /api/tasks/{id}/status` - Update task status
- `DELETE /api/tasks/{id}` - Delete task

## Contributing

This is a professional project for hiring purposes. The codebase follows best practices:

- TypeScript for type safety
- Component-based architecture
- Reusable UI components
- Proper error handling
- Loading states
- Responsive design

## License

This project is part of a technical assessment.