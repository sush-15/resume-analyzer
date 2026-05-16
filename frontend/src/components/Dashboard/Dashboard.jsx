import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { dashboardAPI, resumeAPI } from '../../services/api';

export default function Dashboard() {
  const navigate = useNavigate();
  const [data,    setData]    = useState(null);
  const [resumes, setResumes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    setLoading(true);
    try {
      const [dashRes, resumeRes] = await Promise.all([
        dashboardAPI.getDashboard(),
        resumeAPI.getMyResumes(),
      ]);
      setData(dashRes.data);
      setResumes(resumeRes.data);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to load dashboard');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (resumeId, fileName) => {
    if (!window.confirm(`Delete "${fileName}"? This cannot be undone.`)) return;
    setDeletingId(resumeId);
    try {
      await resumeAPI.deleteResume(resumeId);
      toast.success('Resume deleted');
      // Refresh both lists
      fetchDashboard();
    } catch (err) {
      toast.error('Failed to delete resume');
    } finally {
      setDeletingId(null);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 70) return '#10b981';
    if (score >= 50) return '#f59e0b';
    return '#ef4444';
  };

  if (loading) return (
    <div style={styles.loading}>
      <div style={{ fontSize: 40, marginBottom: 12 }}>⏳</div>
      <p>Loading your dashboard...</p>
    </div>
  );

  const chartData = data?.recentScores?.map(s => ({
    name: s.fileName?.length > 12 ? s.fileName.substring(0, 12) + '…' : (s.fileName || 'Resume'),
    score: s.overallScore,
  })) || [];

  return (
    <div style={styles.page}>
      <div style={styles.container}>

        {/* Welcome Header */}
        <div style={styles.welcomeCard}>
          <div>
            <h1 style={styles.welcomeTitle}>
              👋 Welcome back, {data?.userName || 'User'}!
            </h1>
            <p style={styles.welcomeSubtitle}>
              Here's your resume performance overview
            </p>
          </div>
          <button
            style={styles.uploadBtn}
            onClick={() => navigate('/upload')}
          >
            + Upload Resume
          </button>
        </div>

        {/* Stats Cards */}
        <div style={styles.statsGrid}>
          <StatCard
            icon="📄"
            label="Total Resumes"
            value={data?.totalResumes || 0}
            color="#4f46e5"
          />
          <StatCard
            icon="📊"
            label="Average ATS Score"
            value={`${data?.averageAtsScore || 0}/100`}
            color={getScoreColor(data?.averageAtsScore || 0)}
          />
          <StatCard
            icon="🏆"
            label="Best ATS Score"
            value={`${data?.bestAtsScore || 0}/100`}
            color="#f59e0b"
          />
          <StatCard
            icon="💼"
            label="Job Matches"
            value={data?.totalJobMatches || 0}
            color="#10b981"
          />
        </div>

        <div style={styles.grid2}>
          {/* ATS Score Chart */}
          {chartData.length > 0 && (
            <div style={styles.card}>
              <h3 style={styles.cardTitle}>📈 ATS Score History</h3>
              <ResponsiveContainer width="100%" height={200}>
                <BarChart data={chartData}>
                  <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                  <YAxis domain={[0, 100]} tick={{ fontSize: 11 }} />
                  <Tooltip />
                  <Bar dataKey="score" radius={[4, 4, 0, 0]}>
                    {chartData.map((entry, i) => (
                      <Cell key={i} fill={getScoreColor(entry.score)} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}

          {/* Top Job Matches */}
          {data?.topJobMatches?.length > 0 && (
            <div style={styles.card}>
              <h3 style={styles.cardTitle}>💼 Top Job Matches</h3>
              {data.topJobMatches.map((job, i) => (
                <div key={i} style={styles.jobItem}>
                  <div>
                    <p style={styles.jobTitle}>{job.jobTitle}</p>
                    <p style={styles.jobCompany}>{job.company}</p>
                  </div>
                  <div style={{
                    ...styles.matchBadge,
                    background: getScoreColor(job.matchScore) + '20',
                    color: getScoreColor(job.matchScore),
                  }}>
                    {job.matchScore}%
                  </div>
                </div>
              ))}
              <button
                style={{ ...styles.viewAllBtn, marginTop: 12 }}
                onClick={() => navigate('/jobs')}
              >
                View All Matches →
              </button>
            </div>
          )}
        </div>

        {/* Skills Analysis */}
        {(data?.topMissingSkills?.length > 0 || data?.topMatchedSkills?.length > 0) && (
          <div style={styles.grid2}>
            {data.topMatchedSkills?.length > 0 && (
              <div style={styles.card}>
                <h3 style={{ ...styles.cardTitle, color: '#059669' }}>✅ Your Strong Skills</h3>
                <div style={styles.skillGrid}>
                  {data.topMatchedSkills.map(skill => (
                    <span key={skill} style={styles.matchedSkill}>{skill}</span>
                  ))}
                </div>
              </div>
            )}
            {data.topMissingSkills?.length > 0 && (
              <div style={styles.card}>
                <h3 style={{ ...styles.cardTitle, color: '#dc2626' }}>⚠️ Skills to Add</h3>
                <div style={styles.skillGrid}>
                  {data.topMissingSkills.map(skill => (
                    <span key={skill} style={styles.missingSkill}>{skill}</span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* Recent Resumes */}
        <div style={styles.card}>
          <div style={styles.cardHeader}>
            <h3 style={styles.cardTitle}>📄 Your Resumes</h3>
            <button style={styles.viewAllBtn} onClick={() => navigate('/upload')}>
              + New Upload
            </button>
          </div>

          {resumes.length === 0 ? (
            <div style={styles.emptyState}>
              <p style={{ fontSize: 40, marginBottom: 12 }}>📤</p>
              <p style={{ color: '#64748b', marginBottom: 16 }}>
                No resumes uploaded yet
              </p>
              <button style={styles.uploadBtn} onClick={() => navigate('/upload')}>
                Upload Your First Resume
              </button>
            </div>
          ) : (
            <div>
              {resumes.map(resume => (
                <div key={resume.id} style={styles.resumeItem}>
                  <div style={styles.resumeIcon}>📄</div>
                  <div style={styles.resumeInfo}>
                    <p style={styles.resumeName}>{resume.fileName}</p>
                    <p style={styles.resumeDate}>
                      Uploaded: {new Date(resume.uploadedAt).toLocaleDateString()}
                    </p>
                  </div>
                  <div style={styles.resumeActions}>
                    <button
                      style={styles.analyzeBtn}
                      onClick={() => navigate(`/analysis/${resume.id}`)}
                    >
                      View Analysis
                    </button>
                    <button
                      style={styles.deleteBtn}
                      onClick={() => handleDelete(resume.id, resume.fileName)}
                      disabled={deletingId === resume.id}
                      title="Delete resume"
                    >
                      {deletingId === resume.id ? '…' : '🗑'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  );
}

function StatCard({ icon, label, value, color }) {
  return (
    <div style={{ ...styles.statCard, borderTop: `3px solid ${color}` }}>
      <div style={{ fontSize: 28, marginBottom: 8 }}>{icon}</div>
      <p style={{ fontSize: 26, fontWeight: 700, color }}>{value}</p>
      <p style={{ fontSize: 13, color: '#64748b', marginTop: 2 }}>{label}</p>
    </div>
  );
}

const styles = {
  page:      { padding: '0 24px' },
  container: { maxWidth: 1100, margin: '0 auto' },
  loading:   { textAlign: 'center', padding: 80, color: '#64748b' },

  welcomeCard: {
    background: 'linear-gradient(135deg, #4f46e5, #7c3aed)',
    borderRadius: 16, padding: '28px 32px',
    display: 'flex', justifyContent: 'space-between', alignItems: 'center',
    marginBottom: 24, flexWrap: 'wrap', gap: 16,
  },
  welcomeTitle:    { fontSize: 22, fontWeight: 700, color: '#fff', marginBottom: 4 },
  welcomeSubtitle: { color: 'rgba(255,255,255,0.8)', fontSize: 14 },
  uploadBtn: {
    background: '#fff', color: '#4f46e5', border: 'none',
    padding: '10px 20px', borderRadius: 8, fontSize: 14,
    fontWeight: 600, cursor: 'pointer',
  },

  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: 16, marginBottom: 24,
  },
  statCard: {
    background: '#fff', borderRadius: 12, padding: '20px 24px',
    border: '1px solid #e2e8f0',
    boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
  },

  grid2: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))',
    gap: 16, marginBottom: 24,
  },
  card: {
    background: '#fff', borderRadius: 12, padding: 24,
    border: '1px solid #e2e8f0',
    boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
    marginBottom: 24,
  },
  cardHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 },
  cardTitle:  { fontSize: 15, fontWeight: 600, color: '#1e293b', marginBottom: 16 },
  viewAllBtn: {
    background: '#f1f5f9', color: '#4f46e5', border: 'none',
    padding: '6px 14px', borderRadius: 6, fontSize: 13,
    fontWeight: 500, cursor: 'pointer',
  },

  jobItem: {
    display: 'flex', justifyContent: 'space-between', alignItems: 'center',
    padding: '10px 0', borderBottom: '1px solid #f1f5f9',
  },
  jobTitle:   { fontSize: 14, fontWeight: 600, color: '#1e293b' },
  jobCompany: { fontSize: 12, color: '#64748b' },
  matchBadge: {
    padding: '4px 12px', borderRadius: 20,
    fontSize: 13, fontWeight: 700,
  },

  skillGrid:    { display: 'flex', flexWrap: 'wrap', gap: 8 },
  matchedSkill: {
    background: '#dcfce7', color: '#166534',
    padding: '4px 12px', borderRadius: 20, fontSize: 12, fontWeight: 500,
  },
  missingSkill: {
    background: '#fee2e2', color: '#991b1b',
    padding: '4px 12px', borderRadius: 20, fontSize: 12, fontWeight: 500,
  },

  emptyState: { textAlign: 'center', padding: '32px 0' },

  resumeItem: {
    display: 'flex', alignItems: 'center', gap: 14,
    padding: '12px 0', borderBottom: '1px solid #f1f5f9',
  },
  resumeIcon:    { fontSize: 28, flexShrink: 0 },
  resumeInfo:    { flex: 1 },
  resumeName:    { fontSize: 14, fontWeight: 600, color: '#1e293b' },
  resumeDate:    { fontSize: 12, color: '#64748b' },
  resumeActions: { display: 'flex', gap: 8, alignItems: 'center' },
  analyzeBtn: {
    background: '#eef2ff', color: '#4f46e5', border: 'none',
    padding: '7px 14px', borderRadius: 6, fontSize: 13,
    fontWeight: 500, cursor: 'pointer',
  },
  deleteBtn: {
    background: '#fee2e2', color: '#ef4444', border: 'none',
    padding: '7px 10px', borderRadius: 6, fontSize: 14,
    cursor: 'pointer',
  },
};
