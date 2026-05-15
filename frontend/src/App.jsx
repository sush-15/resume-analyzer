import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import Dashboard from './components/Dashboard/Dashboard';
import ResumeUpload from './components/Resume/ResumeUpload';
import ResumeAnalysis from './components/Resume/ResumeAnalysis';
import JobMatch from './components/Jobs/JobMatch';
import Navbar from './components/Layout/Navbar';

// Protected route wrapper
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" replace />;
};

// Layout wrapper for authenticated pages
const AuthLayout = ({ children }) => (
  <>
    <Navbar />
    <main style={{ minHeight: 'calc(100vh - 64px)', padding: '24px 0' }}>
      {children}
    </main>
  </>
);

export default function App() {
  return (
    <BrowserRouter>
      <ToastContainer position="top-right" autoClose={3000} />
      <Routes>
        {/* Public routes */}
        <Route path="/login"    element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Protected routes */}
        <Route path="/dashboard" element={
          <ProtectedRoute>
            <AuthLayout><Dashboard /></AuthLayout>
          </ProtectedRoute>
        } />
        <Route path="/upload" element={
          <ProtectedRoute>
            <AuthLayout><ResumeUpload /></AuthLayout>
          </ProtectedRoute>
        } />
        <Route path="/analysis/:resumeId" element={
          <ProtectedRoute>
            <AuthLayout><ResumeAnalysis /></AuthLayout>
          </ProtectedRoute>
        } />
        <Route path="/jobs" element={
          <ProtectedRoute>
            <AuthLayout><JobMatch /></AuthLayout>
          </ProtectedRoute>
        } />

        {/* Default redirect */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
