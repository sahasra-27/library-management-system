import React, { useContext } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Box, CircularProgress } from '@mui/material';

// Context Providers
import { AuthProvider, AuthContext } from './context/AuthContext';
import { CustomThemeProvider } from './context/ThemeContext';

// Layouts
import AdminLayout from './layouts/AdminLayout';
import StudentLayout from './layouts/StudentLayout';

// Auth Pages
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';

// Admin Pages
import AdminDashboard from './pages/admin/Dashboard';
import Books from './pages/admin/Books';
import BookForm from './pages/admin/BookForm';
import Categories from './pages/admin/Categories';
import Authors from './pages/admin/Authors';
import Publishers from './pages/admin/Publishers';
import Users from './pages/admin/Users';
import IssueBooks from './pages/admin/IssueBooks';
import ReturnBooks from './pages/admin/ReturnBooks';
import Reservations from './pages/admin/Reservations';
import FineManagement from './pages/admin/FineManagement';
import Reports from './pages/admin/Reports';
import AdminNotifications from './pages/admin/Notifications';
import Settings from './pages/admin/Settings';

// Student Pages
import StudentDashboard from './pages/student/Dashboard';
import SearchBooks from './pages/student/SearchBooks';
import MyBooks from './pages/student/MyBooks';
import BorrowHistory from './pages/student/BorrowHistory';
import StudentReservations from './pages/student/Reservations';
import Profile from './pages/student/Profile';
import StudentReturnBooks from './pages/student/ReturnBooks';
import MyFines from './pages/student/MyFines';
import BookDetails from './pages/student/BookDetails';
import AuthorDetails from './pages/student/AuthorDetails';

function ProtectedRoute({ children, allowedRoles }) {
  const { user, role, loading } = useContext(AuthContext);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', background: '#0f172a' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(role)) {
    if (role === 'ADMIN') {
      return <Navigate to="/admin/dashboard" replace />;
    } else {
      return <Navigate to="/student/dashboard" replace />;
    }
  }

  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CustomThemeProvider>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />

            {/* Admin Routes */}
            <Route 
              path="/admin" 
              element={
                <ProtectedRoute allowedRoles={['ADMIN']}>
                  <AdminLayout />
                </ProtectedRoute>
              }
            >
              <Route index element={<Navigate to="/admin/dashboard" replace />} />
              <Route path="dashboard" element={<AdminDashboard />} />
              <Route path="books" element={<Books />} />
              <Route path="books/new" element={<BookForm />} />
              <Route path="books/edit/:id" element={<BookForm />} />
              <Route path="categories" element={<Categories />} />
              <Route path="authors" element={<Authors />} />
              <Route path="publishers" element={<Publishers />} />
              <Route path="users" element={<Users />} />
              <Route path="issues" element={<IssueBooks />} />
              <Route path="returns" element={<ReturnBooks />} />
              <Route path="reservations" element={<Reservations />} />
              <Route path="fines" element={<FineManagement />} />
              <Route path="reports" element={<Reports />} />
              <Route path="notifications" element={<AdminNotifications />} />
              <Route path="settings" element={<Settings />} />
              <Route path="profile" element={<Profile />} />
            </Route>

            {/* Student Routes */}
            <Route 
              path="/student" 
              element={
                <ProtectedRoute allowedRoles={['USER', 'STUDENT']}>
                  <StudentLayout />
                </ProtectedRoute>
              }
            >
              <Route index element={<Navigate to="/student/dashboard" replace />} />
              <Route path="dashboard" element={<StudentDashboard />} />
              <Route path="search-books" element={<SearchBooks />} />
              <Route path="my-books" element={<MyBooks />} />
              <Route path="return-books" element={<StudentReturnBooks />} />
              <Route path="borrow-history" element={<BorrowHistory />} />
              <Route path="reservations" element={<StudentReservations />} />
              <Route path="my-fines" element={<MyFines />} />
              <Route path="notifications" element={<AdminNotifications />} />
              <Route path="profile" element={<Profile />} />
              <Route path="books/:workId" element={<BookDetails />} />
              <Route path="author/:authorId" element={<AuthorDetails />} />
            </Route>

            {/* Default Route redirection */}
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
          <ToastContainer position="top-right" autoClose={3000} theme="dark" />
        </CustomThemeProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
