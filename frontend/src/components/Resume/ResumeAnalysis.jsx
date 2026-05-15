import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { RadialBarChart, RadialBar, ResponsiveContainer, Tooltip } from 'recharts';
import { resumeAPI } from '../../services/api';

export default function ResumeAnalysis() {
  const { resumeId } = useParams();
  const [atsScore,   setAtsScore]   = useState(null);
  const [aiFeedback, setAiFeedback] = useState(null);
  const [loading,    setLoading]    = useState(true);
  const [activeTab,  setActiveTab]  = useState('ats');

  useEffect(() => {
    fetchData();
  }, [resumeId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [atsRes, aiRes] = await Promise.all([
        resumeAPI.getAtsScore(resumeId),
        resumeAPI.getAiFeedback(resumeId),
      ]);
      setAtsScore(atsRes.data);
      setAiFeedback(aiRes.data);
    } catch (err) {
      toast.error('Failed to load analysis data');
    } finally {
      setLoading(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 70) return '#10b981';
    if (score >= 50) return '#f59e0b';
    return '#ef4444';
  };

  if (loading) return (
    <div style={styles.loading}>
      <div style={styles.spinner}>⏳</div>
      <p>Loading analysis...</p>
    </div>
  );

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.title}>📊 Resume Analysis</h1>
        <p style={styles.subtitle}>
          {atsScore?.fileName} — Analyzed on{' '}
          {atsScore?.analyzedAt ? new Date(atsScore.analyzedAt).toLocaleDateString() : ''}
        </p>

        {/* Score Overview */}
        {atsScore && (
          <div style={styles.scoreOverview}>
            <div style={styles.mainScore}>
              <div style={{
                ...styles.scoreBadge,
                background: `conic-gradient(${getScoreColor(atsScore.overallScore)} ${atsScore.overallScore * 3.6}deg, #e2e8f0 0deg)`,
              }}>
                <div style={styles.scoreInner}>
                  <span style={styles.scoreNum}>{atsScore.overallScore}</span>
                  <span style={styles.scoreMax}>/100</span>
                </div>
              </div>
              <div>
                <p style={styles.gradeLabel}>
                  Grade:{' '}
                  <strong style={{ color: getScoreColor(atsScore.overallScore), fontSize: 22 }}>
                    {atsScore.scoreGrade}
                  </strong>
                </p>
                <p style={{ color: '#64748b', fontSize: 14, marginTop: 4 }}>
                  {atsScore.scoreMessage}
                </p>
              </div>
            </div>

            {/* Score Breakdown */}
            <div style={styles.breakdown}>
              {[
                { label: 'Keywords',    score: atsScore.keywordScore,    max: 40 },
                { label: 'Sections',    score: atsScore.sectionCompleteness, max: 25 },
                { label: 'Skills',      score: atsScore.skillsScore,     max: 20 },
                { label: 'Formatting',  score: atsScore.formattingScore, max: 15 },
              ].map(({ label, score, max }) => (
                <div key={label} style={styles.breakdownItem}>
                  <div style={styles.breakdownHeader}>
                    <span style={styles.breakdownLabel}>{label}</span>
                    <span style={styles.breakdownScore}>{score}/{max}</span>
                  </div>
                  <div style={styles.progressBar}>
                    <div style={{
                      ...styles.progressFill,
                      width: `${(score / max) * 100}%`,
                      background: getScoreColor((score / max) * 100),
                    }} />
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Tabs */}
        <div style={styles.tabs}>
          {['ats', 'ai', 'keywords'].map(tab => (
            <button
              key={tab}
              style={{ ...styles.tab, ...(activeTab === tab ? styles.activeTab : {}) }}
              onClick={() => setActiveTab(tab)}
            >
              {tab === 'ats'      && '📈 ATS Details'}
              {tab === 'ai'       && '🤖 AI Feedback'}
              {tab === 'keywords' && '🔑 Keywords'}
            </button>
          ))}
        </div>

        {/* Tab Content */}
        <div style={styles.tabContent}>

          {/* ATS Details Tab */}
          {activeTab === 'ats' && atsScore && (
            <div>
              <h3 style={styles.sectionTitle}>Score Breakdown</h3>
              <div style={styles.grid2}>
                <ScoreCard label="Keyword Match" score={atsScore.keywordScore} max={40} />
                <ScoreCard label="Section Completeness" score={atsScore.sectionCompleteness} max={25} />
                <ScoreCard label="Technical Skills" score={atsScore.skillsScore} max={20} />
                <ScoreCard label="Formatting" score={atsScore.formattingScore} max={15} />
              </div>
            </div>
          )}

          {/* AI Feedback Tab */}
          {activeTab === 'ai' && aiFeedback && (
            <div>
              <h3 style={styles.sectionTitle}>AI-Generated Feedback</h3>
              {aiFeedback.suggestions?.length > 0 && (
                <FeedbackSection
                  title="💡 Suggestions"
                  items={aiFeedback.suggestions}
                  color="#4f46e5"
                  bg="#eef2ff"
                />
              )}
              {aiFeedback.missingSkills?.length > 0 && (
                <FeedbackSection
                  title="⚠️ Missing Skills"
                  items={aiFeedback.missingSkills}
                  color="#dc2626"
                  bg="#fef2f2"
                />
              )}
              {aiFeedback.improvements?.length > 0 && (
                <FeedbackSection
                  title="🔧 Improvements"
                  items={aiFeedback.improvements}
                  color="#059669"
                  bg="#f0fdf4"
                />
              )}
              {aiFeedback.feedbackText && (
                <div style={styles.feedbackText}>
                  <h4 style={{ marginBottom: 12, color: '#374151' }}>Full AI Analysis</h4>
                  <pre style={styles.feedbackPre}>{aiFeedback.feedbackText}</pre>
                </div>
              )}
            </div>
          )}

          {/* Keywords Tab */}
          {activeTab === 'keywords' && atsScore && (
            <div>
              <div style={styles.grid2}>
                <div>
                  <h3 style={{ ...styles.sectionTitle, color: '#059669' }}>
                    ✅ Matched Keywords ({atsScore.matchedKeywords?.length || 0})
                  </h3>
                  <div style={styles.keywordGrid}>
                    {atsScore.matchedKeywords?.map(kw => (
                      <span key={kw} style={styles.matchedKeyword}>{kw}</span>
                    ))}
                  </div>
                </div>
                <div>
                  <h3 style={{ ...styles.sectionTitle, color: '#dc2626' }}>
                    ❌ Missing Keywords ({atsScore.missingKeywords?.length || 0})
                  </h3>
                  <div style={styles.keywordGrid}>
                    {atsScore.missingKeywords?.map(kw => (
                      <span key={kw} style={styles.missingKeyword}>{kw}</span>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// Sub-components
function ScoreCard({ label, score, max }) {
  const pct = Math.round((score / max) * 100);
  const color = pct >= 70 ? '#10b981' : pct >= 50 ? '#f59e0b' : '#ef4444';
  return (
    <div style={{ background: '#fff', borderRadius: 12, padding: 20, border: '1px solid #e2e8f0' }}>
      <p style={{ fontSize: 13, color: '#64748b', marginBottom: 8 }}>{label}</p>
      <p style={{ fontSize: 24, fontWeight: 700, color }}>{score}<span style={{ fontSize: 14, color: '#94a3b8' }}>/{max}</span></p>
      <div style={{ background: '#f1f5f9', borderRadius: 4, height: 6, marginTop: 8 }}>
        <div style={{ width: `${pct}%`, height: '100%', background: color, borderRadius: 4 }} />
      </div>
    </div>
  );
}

function FeedbackSection({ title, items, color, bg }) {
  return (
    <div style={{ background: bg, borderRadius: 12, padding: 20, marginBottom: 16, border: `1px solid ${color}22` }}>
      <h4 style={{ color, marginBottom: 12, fontSize: 15 }}>{title}</h4>
      <ul style={{ paddingLeft: 20 }}>
        {items.map((item, i) => (
          <li key={i} style={{ color: '#374151', fontSize: 14, marginBottom: 6 }}>{item}</li>
        ))}
      </ul>
    </div>
  );
}

const styles = {
  page:      { padding: '0 24px' },
  container: { maxWidth: 900, margin: '0 auto' },
  title:     { fontSize: 26, fontWeight: 700, color: '#1e293b', marginBottom: 4 },
  subtitle:  { color: '#64748b', fontSize: 14, marginBottom: 28 },
  loading:   { textAlign: 'center', padding: 80, color: '#64748b' },
  spinner:   { fontSize: 40, marginBottom: 12 },

  scoreOverview: {
    background: '#fff', borderRadius: 16, padding: 28,
    border: '1px solid #e2e8f0', marginBottom: 24,
    display: 'flex', gap: 32, flexWrap: 'wrap', alignItems: 'center',
  },
  mainScore: { display: 'flex', alignItems: 'center', gap: 20 },
  scoreBadge: {
    width: 100, height: 100, borderRadius: '50%',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    flexShrink: 0,
  },
  scoreInner: {
    width: 76, height: 76, borderRadius: '50%',
    background: '#fff', display: 'flex', flexDirection: 'column',
    alignItems: 'center', justifyContent: 'center',
  },
  scoreNum:   { fontSize: 26, fontWeight: 800, color: '#1e293b', lineHeight: 1 },
  scoreMax:   { fontSize: 12, color: '#94a3b8' },
  gradeLabel: { fontSize: 16, color: '#374151' },

  breakdown: { flex: 1, minWidth: 240 },
  breakdownItem: { marginBottom: 12 },
  breakdownHeader: { display: 'flex', justifyContent: 'space-between', marginBottom: 4 },
  breakdownLabel: { fontSize: 13, color: '#374151', fontWeight: 500 },
  breakdownScore: { fontSize: 13, color: '#64748b' },
  progressBar: { background: '#f1f5f9', borderRadius: 4, height: 8 },
  progressFill: { height: '100%', borderRadius: 4, transition: 'width 0.5s' },

  tabs: { display: 'flex', gap: 8, marginBottom: 20 },
  tab: {
    padding: '10px 20px', borderRadius: 8, border: '1px solid #e2e8f0',
    background: '#fff', color: '#64748b', fontSize: 14, fontWeight: 500, cursor: 'pointer',
  },
  activeTab: { background: '#4f46e5', color: '#fff', borderColor: '#4f46e5' },

  tabContent: {
    background: '#fff', borderRadius: 16, padding: 28,
    border: '1px solid #e2e8f0',
  },
  sectionTitle: { fontSize: 16, fontWeight: 600, color: '#1e293b', marginBottom: 16 },
  grid2: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 16 },

  keywordGrid: { display: 'flex', flexWrap: 'wrap', gap: 8 },
  matchedKeyword: {
    background: '#dcfce7', color: '#166534', padding: '4px 12px',
    borderRadius: 20, fontSize: 12, fontWeight: 500,
  },
  missingKeyword: {
    background: '#fee2e2', color: '#991b1b', padding: '4px 12px',
    borderRadius: 20, fontSize: 12, fontWeight: 500,
  },

  feedbackText: { marginTop: 20 },
  feedbackPre: {
    background: '#f8fafc', borderRadius: 8, padding: 16,
    fontSize: 13, color: '#374151', whiteSpace: 'pre-wrap',
    lineHeight: 1.7, border: '1px solid #e2e8f0',
  },
};
