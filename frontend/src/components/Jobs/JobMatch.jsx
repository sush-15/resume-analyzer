import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { jobAPI, resumeAPI } from '../../services/api';

export default function JobMatch() {
  const [jobs,         setJobs]         = useState([]);
  const [resumes,      setResumes]      = useState([]);
  const [matches,      setMatches]      = useState([]);
  const [selectedResume, setSelectedResume] = useState('');
  const [loading,      setLoading]      = useState(false);
  const [filter,       setFilter]       = useState('all');

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      const [jobsRes, resumesRes] = await Promise.all([
        jobAPI.getAll(),
        resumeAPI.getMyResumes(),
      ]);
      setJobs(jobsRes.data);
      setResumes(resumesRes.data);
    } catch (err) {
      toast.error('Failed to load data');
    }
  };

  const handleMatchJobs = async () => {
    if (!selectedResume) {
      toast.error('Please select a resume first');
      return;
    }
    setLoading(true);
    try {
      const res = await resumeAPI.getJobMatches(selectedResume);
      setMatches(res.data);
      toast.success(`Found ${res.data.length} job matches!`);
    } catch (err) {
      toast.error('Failed to match jobs');
    } finally {
      setLoading(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 70) return '#10b981';
    if (score >= 50) return '#f59e0b';
    return '#ef4444';
  };

  const getMatchBg = (score) => {
    if (score >= 70) return '#f0fdf4';
    if (score >= 50) return '#fffbeb';
    return '#fef2f2';
  };

  const filteredMatches = matches.filter(m => {
    if (filter === 'excellent') return m.matchScore >= 70;
    if (filter === 'good')      return m.matchScore >= 50 && m.matchScore < 70;
    if (filter === 'partial')   return m.matchScore < 50;
    return true;
  });

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.title}>💼 Job Matcher</h1>
        <p style={styles.subtitle}>
          Match your resume against available jobs and see your compatibility score
        </p>

        {/* Match Controls */}
        <div style={styles.controlCard}>
          <div style={styles.controlRow}>
            <div style={styles.selectWrapper}>
              <label style={styles.label}>Select Resume to Match</label>
              <select
                value={selectedResume}
                onChange={(e) => setSelectedResume(e.target.value)}
                style={styles.select}
              >
                <option value="">-- Choose a resume --</option>
                {resumes.map(r => (
                  <option key={r.id} value={r.id}>{r.fileName}</option>
                ))}
              </select>
            </div>
            <button
              onClick={handleMatchJobs}
              disabled={loading || !selectedResume}
              style={styles.matchBtn}
            >
              {loading ? '⏳ Matching...' : '🔍 Find Matches'}
            </button>
          </div>
        </div>

        {/* All Jobs (before matching) */}
        {matches.length === 0 && (
          <div>
            <h2 style={styles.sectionTitle}>
              📋 Available Jobs ({jobs.length})
            </h2>
            <div style={styles.jobsGrid}>
              {jobs.map(job => (
                <div key={job.id} style={styles.jobCard}>
                  <div style={styles.jobHeader}>
                    <h3 style={styles.jobTitle}>{job.title}</h3>
                    <span style={styles.jobTypeBadge}>
                      {job.jobType?.replace('_', ' ')}
                    </span>
                  </div>
                  <p style={styles.jobCompany}>🏢 {job.company}</p>
                  <p style={styles.jobLocation}>📍 {job.location}</p>
                  <p style={styles.jobLevel}>
                    Level: <strong>{job.experienceLevel}</strong>
                  </p>
                  {job.requiredSkills && (
                    <div style={styles.skillsRow}>
                      {JSON.parse(job.requiredSkills || '[]').slice(0, 4).map(skill => (
                        <span key={skill} style={styles.skillTag}>{skill}</span>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Match Results */}
        {matches.length > 0 && (
          <div>
            <div style={styles.matchHeader}>
              <h2 style={styles.sectionTitle}>
                🎯 Match Results ({matches.length} jobs)
              </h2>
              <div style={styles.filterRow}>
                {['all', 'excellent', 'good', 'partial'].map(f => (
                  <button
                    key={f}
                    style={{ ...styles.filterBtn, ...(filter === f ? styles.activeFilter : {}) }}
                    onClick={() => setFilter(f)}
                  >
                    {f.charAt(0).toUpperCase() + f.slice(1)}
                  </button>
                ))}
              </div>
            </div>

            <div style={styles.matchesGrid}>
              {filteredMatches.map((match, i) => (
                <div key={i} style={{
                  ...styles.matchCard,
                  background: getMatchBg(match.matchScore),
                  borderColor: getScoreColor(match.matchScore) + '40',
                }}>
                  <div style={styles.matchTop}>
                    <div>
                      <h3 style={styles.matchTitle}>{match.jobTitle}</h3>
                      <p style={styles.matchCompany}>🏢 {match.company} • 📍 {match.location}</p>
                    </div>
                    <div style={{
                      ...styles.matchScore,
                      background: getScoreColor(match.matchScore),
                    }}>
                      <span style={styles.matchScoreNum}>{match.matchScore}%</span>
                      <span style={styles.matchScoreLabel}>Match</span>
                    </div>
                  </div>

                  <div style={{
                    ...styles.matchLevelBadge,
                    color: getScoreColor(match.matchScore),
                    background: getScoreColor(match.matchScore) + '15',
                  }}>
                    {match.matchLevel}
                  </div>

                  <div style={styles.matchDetails}>
                    {match.matchedSkills?.length > 0 && (
                      <div style={styles.matchSection}>
                        <p style={styles.matchSectionTitle}>✅ You have:</p>
                        <div style={styles.skillsRow}>
                          {match.matchedSkills.map(s => (
                            <span key={s} style={styles.matchedSkill}>{s}</span>
                          ))}
                        </div>
                      </div>
                    )}
                    {match.missingSkills?.length > 0 && (
                      <div style={styles.matchSection}>
                        <p style={styles.matchSectionTitle}>❌ You need:</p>
                        <div style={styles.skillsRow}>
                          {match.missingSkills.map(s => (
                            <span key={s} style={styles.missingSkill}>{s}</span>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  page:      { padding: '0 24px' },
  container: { maxWidth: 1100, margin: '0 auto' },
  title:     { fontSize: 26, fontWeight: 700, color: '#1e293b', marginBottom: 4 },
  subtitle:  { color: '#64748b', fontSize: 14, marginBottom: 24 },

  controlCard: {
    background: '#fff', borderRadius: 12, padding: 24,
    border: '1px solid #e2e8f0', marginBottom: 28,
  },
  controlRow: { display: 'flex', gap: 16, alignItems: 'flex-end', flexWrap: 'wrap' },
  selectWrapper: { flex: 1, minWidth: 240 },
  label:  { display: 'block', fontSize: 13, fontWeight: 600, color: '#374151', marginBottom: 6 },
  select: {
    width: '100%', padding: '10px 14px', border: '1.5px solid #e2e8f0',
    borderRadius: 8, fontSize: 14, background: '#fff', outline: 'none',
  },
  matchBtn: {
    background: '#4f46e5', color: '#fff', border: 'none',
    padding: '11px 24px', borderRadius: 8, fontSize: 14,
    fontWeight: 600, cursor: 'pointer', whiteSpace: 'nowrap',
  },

  sectionTitle: { fontSize: 17, fontWeight: 600, color: '#1e293b', marginBottom: 16 },

  jobsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
    gap: 16,
  },
  jobCard: {
    background: '#fff', borderRadius: 12, padding: 20,
    border: '1px solid #e2e8f0',
  },
  jobHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 8 },
  jobTitle:  { fontSize: 15, fontWeight: 600, color: '#1e293b', flex: 1 },
  jobTypeBadge: {
    background: '#eef2ff', color: '#4f46e5',
    padding: '3px 10px', borderRadius: 20, fontSize: 11, fontWeight: 500,
    whiteSpace: 'nowrap', marginLeft: 8,
  },
  jobCompany:  { fontSize: 13, color: '#64748b', marginBottom: 4 },
  jobLocation: { fontSize: 13, color: '#64748b', marginBottom: 4 },
  jobLevel:    { fontSize: 13, color: '#374151', marginBottom: 10 },
  skillsRow:   { display: 'flex', flexWrap: 'wrap', gap: 6 },
  skillTag: {
    background: '#f1f5f9', color: '#475569',
    padding: '3px 10px', borderRadius: 20, fontSize: 11,
  },

  matchHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap', gap: 12 },
  filterRow:   { display: 'flex', gap: 8 },
  filterBtn: {
    padding: '6px 14px', borderRadius: 6, border: '1px solid #e2e8f0',
    background: '#fff', color: '#64748b', fontSize: 13, cursor: 'pointer',
  },
  activeFilter: { background: '#4f46e5', color: '#fff', borderColor: '#4f46e5' },

  matchesGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))',
    gap: 16,
  },
  matchCard: {
    borderRadius: 12, padding: 20,
    border: '1px solid',
  },
  matchTop: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 10 },
  matchTitle:   { fontSize: 15, fontWeight: 600, color: '#1e293b', marginBottom: 4 },
  matchCompany: { fontSize: 12, color: '#64748b' },
  matchScore: {
    width: 60, height: 60, borderRadius: '50%',
    display: 'flex', flexDirection: 'column',
    alignItems: 'center', justifyContent: 'center',
    color: '#fff', flexShrink: 0,
  },
  matchScoreNum:   { fontSize: 16, fontWeight: 800, lineHeight: 1 },
  matchScoreLabel: { fontSize: 10, opacity: 0.9 },
  matchLevelBadge: {
    display: 'inline-block', padding: '4px 12px',
    borderRadius: 20, fontSize: 12, fontWeight: 600, marginBottom: 12,
  },
  matchDetails: {},
  matchSection: { marginBottom: 10 },
  matchSectionTitle: { fontSize: 12, fontWeight: 600, color: '#374151', marginBottom: 6 },
  matchedSkill: {
    background: '#dcfce7', color: '#166534',
    padding: '3px 10px', borderRadius: 20, fontSize: 11, fontWeight: 500,
    display: 'inline-block', margin: '2px',
  },
  missingSkill: {
    background: '#fee2e2', color: '#991b1b',
    padding: '3px 10px', borderRadius: 20, fontSize: 11, fontWeight: 500,
    display: 'inline-block', margin: '2px',
  },
};
