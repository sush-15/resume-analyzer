import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { resumeAPI } from '../../services/api';

export default function ResumeUpload() {
  const navigate = useNavigate();
  const [file, setFile]         = useState(null);
  const [dragging, setDragging] = useState(false);
  const [loading, setLoading]   = useState(false);
  const [result, setResult]     = useState(null);

  const handleFileSelect = (selectedFile) => {
    const allowed = ['application/pdf', 'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
    if (!allowed.includes(selectedFile.type)) {
      toast.error('Only PDF, DOC, and DOCX files are allowed');
      return;
    }
    if (selectedFile.size > 10 * 1024 * 1024) {
      toast.error('File size must not exceed 10MB');
      return;
    }
    setFile(selectedFile);
    setResult(null);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragging(false);
    const dropped = e.dataTransfer.files[0];
    if (dropped) handleFileSelect(dropped);
  };

  const handleUpload = async () => {
    if (!file) { toast.error('Please select a file first'); return; }

    const formData = new FormData();
    formData.append('file', file);

    setLoading(true);
    try {
      const res = await resumeAPI.upload(formData);
      setResult(res.data);
      toast.success('Resume analyzed successfully!');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Upload failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 70) return '#10b981';
    if (score >= 50) return '#f59e0b';
    return '#ef4444';
  };

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <div style={styles.header}>
          <h1 style={styles.title}>Upload Your Resume</h1>
          <p style={styles.subtitle}>
            Get instant ATS score, AI feedback, and job matches
          </p>
        </div>

        {/* Upload Zone */}
        <div
          style={{
            ...styles.dropZone,
            ...(dragging ? styles.dropZoneActive : {}),
            ...(file ? styles.dropZoneHasFile : {}),
          }}
          onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
          onDragLeave={() => setDragging(false)}
          onDrop={handleDrop}
          onClick={() => document.getElementById('fileInput').click()}
        >
          <input
            id="fileInput"
            type="file"
            accept=".pdf,.doc,.docx"
            style={{ display: 'none' }}
            onChange={(e) => e.target.files[0] && handleFileSelect(e.target.files[0])}
          />

          {file ? (
            <div style={styles.fileInfo}>
              <div style={styles.fileIcon}>📄</div>
              <div>
                <p style={styles.fileName}>{file.name}</p>
                <p style={styles.fileSize}>
                  {(file.size / 1024).toFixed(1)} KB
                </p>
              </div>
              <button
                style={styles.removeBtn}
                onClick={(e) => { e.stopPropagation(); setFile(null); setResult(null); }}
              >
                ✕
              </button>
            </div>
          ) : (
            <div style={styles.dropContent}>
              <div style={styles.uploadIcon}>📤</div>
              <p style={styles.dropText}>
                <strong>Drag & drop</strong> your resume here
              </p>
              <p style={styles.dropSubtext}>or click to browse</p>
              <p style={styles.dropFormats}>PDF, DOC, DOCX • Max 10MB</p>
            </div>
          )}
        </div>

        {/* Upload Button */}
        {file && !result && (
          <button
            onClick={handleUpload}
            disabled={loading}
            style={styles.uploadBtn}
          >
            {loading ? (
              <span>⏳ Analyzing Resume...</span>
            ) : (
              <span>🚀 Analyze Resume</span>
            )}
          </button>
        )}

        {/* Loading State */}
        {loading && (
          <div style={styles.loadingCard}>
            <div style={styles.loadingSteps}>
              <p>✅ Uploading file...</p>
              <p>✅ Extracting text...</p>
              <p>⏳ Calculating ATS score...</p>
              <p>⏳ Generating AI feedback...</p>
              <p>⏳ Matching jobs...</p>
            </div>
          </div>
        )}

        {/* Result Card */}
        {result && (
          <div style={styles.resultCard}>
            <div style={styles.resultHeader}>
              <h2 style={styles.resultTitle}>✅ Analysis Complete!</h2>
            </div>

            <div style={styles.scoreSection}>
              <div style={{
                ...styles.scoreBadge,
                background: getScoreColor(result.atsScore),
              }}>
                <span style={styles.scoreNumber}>{result.atsScore}</span>
                <span style={styles.scoreLabel}>ATS Score</span>
              </div>
              <div style={styles.scoreInfo}>
                <p style={styles.gradeText}>
                  Grade: <strong style={{ color: getScoreColor(result.atsScore) }}>
                    {result.scoreGrade}
                  </strong>
                </p>
                <p style={styles.fileNameText}>📄 {result.fileName}</p>
              </div>
            </div>

            <div style={styles.resultActions}>
              <button
                style={styles.viewBtn}
                onClick={() => navigate(`/analysis/${result.resumeId}`)}
              >
                📊 View Full Analysis
              </button>
              <button
                style={styles.jobsBtn}
                onClick={() => navigate('/jobs')}
              >
                💼 View Job Matches
              </button>
              <button
                style={styles.uploadAnotherBtn}
                onClick={() => { setFile(null); setResult(null); }}
              >
                Upload Another
              </button>
            </div>
          </div>
        )}

        {/* Tips */}
        <div style={styles.tipsCard}>
          <h3 style={styles.tipsTitle}>💡 Tips for a Higher ATS Score</h3>
          <ul style={styles.tipsList}>
            <li>Include keywords like Java, Spring Boot, REST API, MySQL</li>
            <li>Add all sections: Skills, Experience, Education, Projects</li>
            <li>Include your GitHub and LinkedIn profile links</li>
            <li>Use bullet points for project descriptions</li>
            <li>Keep resume between 300–700 words</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

const styles = {
  page:      { padding: '0 24px' },
  container: { maxWidth: 700, margin: '0 auto' },
  header:    { textAlign: 'center', marginBottom: 32 },
  title:     { fontSize: 28, fontWeight: 700, color: '#1e293b', marginBottom: 8 },
  subtitle:  { color: '#64748b', fontSize: 15 },

  dropZone: {
    border: '2px dashed #cbd5e1',
    borderRadius: 16,
    padding: 48,
    textAlign: 'center',
    cursor: 'pointer',
    transition: 'all 0.2s',
    background: '#f8fafc',
    marginBottom: 20,
  },
  dropZoneActive:  { borderColor: '#4f46e5', background: '#eef2ff' },
  dropZoneHasFile: { borderColor: '#10b981', background: '#f0fdf4', borderStyle: 'solid' },

  dropContent:  {},
  uploadIcon:   { fontSize: 48, marginBottom: 12 },
  dropText:     { fontSize: 16, color: '#374151', marginBottom: 4 },
  dropSubtext:  { fontSize: 14, color: '#64748b', marginBottom: 8 },
  dropFormats:  { fontSize: 12, color: '#94a3b8', background: '#f1f5f9', display: 'inline-block', padding: '4px 12px', borderRadius: 20 },

  fileInfo: {
    display: 'flex', alignItems: 'center', gap: 16,
    justifyContent: 'center',
  },
  fileIcon:   { fontSize: 40 },
  fileName:   { fontWeight: 600, color: '#1e293b', fontSize: 15 },
  fileSize:   { color: '#64748b', fontSize: 13 },
  removeBtn:  {
    background: '#fee2e2', color: '#ef4444', border: 'none',
    borderRadius: '50%', width: 28, height: 28, cursor: 'pointer',
    fontSize: 12, fontWeight: 700,
  },

  uploadBtn: {
    width: '100%', background: '#4f46e5', color: '#fff',
    border: 'none', padding: '14px', borderRadius: 10,
    fontSize: 16, fontWeight: 600, cursor: 'pointer',
    marginBottom: 20,
  },

  loadingCard: {
    background: '#fff', borderRadius: 12, padding: 24,
    border: '1px solid #e2e8f0', marginBottom: 20,
  },
  loadingSteps: { display: 'flex', flexDirection: 'column', gap: 8, color: '#374151', fontSize: 14 },

  resultCard: {
    background: '#fff', borderRadius: 16, padding: 28,
    border: '1px solid #e2e8f0', marginBottom: 20,
    boxShadow: '0 4px 12px rgba(0,0,0,0.06)',
  },
  resultHeader: { marginBottom: 20 },
  resultTitle:  { fontSize: 20, fontWeight: 700, color: '#1e293b' },

  scoreSection: { display: 'flex', alignItems: 'center', gap: 20, marginBottom: 24 },
  scoreBadge: {
    width: 90, height: 90, borderRadius: '50%',
    display: 'flex', flexDirection: 'column',
    alignItems: 'center', justifyContent: 'center',
    color: '#fff', flexShrink: 0,
  },
  scoreNumber: { fontSize: 28, fontWeight: 800, lineHeight: 1 },
  scoreLabel:  { fontSize: 11, fontWeight: 500, opacity: 0.9 },
  scoreInfo:   {},
  gradeText:   { fontSize: 16, color: '#374151', marginBottom: 4 },
  fileNameText:{ fontSize: 13, color: '#64748b' },

  resultActions: { display: 'flex', gap: 10, flexWrap: 'wrap' },
  viewBtn: {
    background: '#4f46e5', color: '#fff', border: 'none',
    padding: '10px 18px', borderRadius: 8, fontSize: 14,
    fontWeight: 600, cursor: 'pointer',
  },
  jobsBtn: {
    background: '#10b981', color: '#fff', border: 'none',
    padding: '10px 18px', borderRadius: 8, fontSize: 14,
    fontWeight: 600, cursor: 'pointer',
  },
  uploadAnotherBtn: {
    background: '#f1f5f9', color: '#374151', border: '1px solid #e2e8f0',
    padding: '10px 18px', borderRadius: 8, fontSize: 14,
    fontWeight: 500, cursor: 'pointer',
  },

  tipsCard: {
    background: '#fffbeb', borderRadius: 12, padding: 20,
    border: '1px solid #fde68a',
  },
  tipsTitle: { fontSize: 15, fontWeight: 600, color: '#92400e', marginBottom: 12 },
  tipsList:  { paddingLeft: 20, color: '#78350f', fontSize: 13, lineHeight: 2 },
};
