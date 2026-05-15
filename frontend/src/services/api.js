import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request automatically
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle 401 globally — redirect to login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ============================================
// Auth API
// ============================================
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login:    (data) => api.post('/auth/login', data),
};

// ============================================
// Resume API
// ============================================
export const resumeAPI = {
  upload: (formData) =>
    api.post('/resume/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  getMyResumes:  ()   => api.get('/resume/my-resumes'),
  getAtsScore:   (id) => api.get(`/resume/${id}/ats-score`),
  getAiFeedback: (id) => api.get(`/resume/${id}/ai-feedback`),
  getJobMatches: (id) => api.get(`/resume/${id}/job-matches`),
  deleteResume:  (id) => api.delete(`/resume/${id}`),
};

// ============================================
// Job API
// ============================================
export const jobAPI = {
  getAll:    ()   => api.get('/jobs/public/all'),
  matchJobs: (id) => api.get(`/jobs/match/${id}`),
};

// ============================================
// Dashboard API
// ============================================
export const dashboardAPI = {
  getDashboard: () => api.get('/dashboard'),
};

export default api;
