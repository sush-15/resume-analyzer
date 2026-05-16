import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { dashboardAPI, resumeAPI } from '../../services/api';

export default function Profile() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const [stats, setStats]   = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dashboardAPI.getDashboard()
      .then(res => setStats(res.data))
      .catch(() => toast.error('Failed to load profile stats'))
      .finally(() => setLoading(false));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <div style={styles.page}>
      <div style={styles.container}>

        {/* Profile Card */}
        <div style={styles.profileCard}>
          <div style={styles.avatar}>
            {user.fullName ? user.fullName.charAt(0).toUpperCase() : '?'}
          </div>
          <div style={styles.profileInfo}>
            <h1 style={styles.name}>{user.fullName || 'User'}</h1>
            <p style={styles.email}>✉️ {user.email || '—'}</p>
            <span style={styles.roleBadge}>{user.role || 'USER'}</span>
          </div>
          <button style={styles.logoutBtn} onClick={handleLogout}>
            Logout
          </button>
        </div>

        {/* Stats */}
        {!loading && stats && (
          <div style={styles.statsGrid}>
            <StatCard icon="📄" label="Resumes Uploaded"  value={stats.totalResumes}    color="#4f46e5" />
            <StatCard icon="📊" label="Avg ATS Score"     value={`${stats.averageAtsScore}/100`} color="#f59e0b" />
            <StatCard icon="🏆" label="Best ATS Score"    value={`${stats.bestAtsScore}/100`}    color="#10b981" />
            <StatCard icon="💼" label="Total Job Matches" value={stats.totalJobMatches}  color="#8b5cf6" />
          </div>
        )}

        {/* Quick Actions */}
        <div style={styles.actionsCard}>
          <h2 style={styles.sectionTitle}>Quick Actions</h2>
          <div style={styles.actionsGrid}>
            <ActionButton icon="📤" label="Upload Resume"    onClick={() => navigate('/upload')} color="#4f46e5" />
            <ActionButton icon="📊" label="View Dashboard"   onClick={() => navigate('/dashboard')} color="#10b981" />
            <ActionButton icon="💼" label="Browse Jobs"      onClick={() => navigate('/jobs')} color="#f59e0b" />
          </div>
        </div>

        {/* Account Info */}
        <div style={styles.infoCard}>
          <h2 style={styles.sectionTitle}>Account Details</h2>
          <div style={styles.infoRow}>
            <span style={styles.infoLabel}>Full Name</span>
            <span style={styles.infoValue}>{user.fullName || '—'}</span>
          </div>
          <div style={styles.infoRow}>
            <span style={styles.infoLabel}>Email</span>
            <span style={styles.infoValue}>{user.email || '—'}</span>
          </div>
          <div style={styles.infoRow}>
            <span style={styles.infoLabel}>Role</span>
            <span style={styles.infoValue}>{user.role || 'USER'}</span>
          </div>
          <div style={styles.infoRow}>
            <span style={styles.infoLabel}>Session</span>
            <span style={styles.infoValue}>Active (JWT)</span>
          </div>
        </div>

      </div>
    </div>
  );
}

function StatCard({ icon, label, value, color }) {
  return (
    <div style={{ ...styles.statCard, borderTop: `3px solid ${color}` }}>
      <div style={{ fontSize: 28, marginBottom: 8 }}>{icon}</div>
      <p style={{ fontSize: 24, fontWeight: 700, color }}>{value}</p>
      <p style={{ fontSize: 13, color: '#64748b', marginTop: 2 }}>{label}</p>
    </div>
  );
}

function ActionButton({ icon, label, onClick, color }) {
  return (
    <button
      onClick={onClick}
      style={{ ...styles.actionBtn, borderColor: color + '40', color }}
    >
      <span style={{ fontSize: 24, marginBottom: 6, display: 'block' }}>{icon}</span>
      <span style={{ fontSize: 13, fontWeight: 600 }}>{label}</span>
    </button>
  );
}

const styles = {
  page:      { padding: '0 24px' },
  container: { maxWidth: 800, margin: '0 auto' },

  profileCard: {
    background: 'linear-gradient(135deg, #4f46e5, #7c3aed)',
    borderRadius: 16, padding: '28px 32px',
    display: 'flex', alignItems: 'center', gap: 20,
    marginBottom: 24, flexWrap: 'wrap',
  },
  avatar: {
    width: 72, height: 72, borderRadius: '50%',
    background: 'rgba(255,255,255,0.25)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    fontSize: 32, fontWeight: 700, color: '#fff', flexShrink: 0,
  },
  profileInfo: { flex: 1 },
  name:  { fontSize: 22, fontWeight: 700, color: '#fff', marginBottom: 4 },
  email: { fontSize: 14, color: 'rgba(255,255,255,0.85)', marginBottom: 8 },
  roleBadge: {
    background: 'rgba(255,255,255,0.2)', color: '#fff',
    padding: '3px 12px', borderRadius: 20, fontSize: 12, fontWeight: 600,
  },
  logoutBtn: {
    background: 'rgba(255,255,255,0.15)', color: '#fff',
    border: '1px solid rgba(255,255,255,0.3)',
    padding: '8px 20px', borderRadius: 8, fontSize: 14,
    fontWeight: 500, cursor: 'pointer',
  },

  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
    gap: 16, marginBottom: 24,
  },
  statCard: {
    background: '#fff', borderRadius: 12, padding: '20px 24px',
    border: '1px solid #e2e8f0',
    boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
  },

  actionsCard: {
    background: '#fff', borderRadius: 12, padding: 24,
    border: '1px solid #e2e8f0', marginBottom: 24,
  },
  sectionTitle: { fontSize: 16, fontWeight: 600, color: '#1e293b', marginBottom: 16 },
  actionsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))',
    gap: 12,
  },
  actionBtn: {
    background: '#fff', border: '2px solid',
    borderRadius: 12, padding: '16px 12px',
    cursor: 'pointer', textAlign: 'center',
    transition: 'transform 0.1s',
  },

  infoCard: {
    background: '#fff', borderRadius: 12, padding: 24,
    border: '1px solid #e2e8f0', marginBottom: 24,
  },
  infoRow: {
    display: 'flex', justifyContent: 'space-between', alignItems: 'center',
    padding: '12px 0', borderBottom: '1px solid #f1f5f9',
  },
  infoLabel: { fontSize: 14, color: '#64748b', fontWeight: 500 },
  infoValue: { fontSize: 14, color: '#1e293b', fontWeight: 600 },
};
