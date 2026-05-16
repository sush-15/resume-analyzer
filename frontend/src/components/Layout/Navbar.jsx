import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';

export default function Navbar() {
  const navigate  = useNavigate();
  const location  = useLocation();
  const user      = JSON.parse(localStorage.getItem('user') || '{}');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav style={styles.nav}>
      <div style={styles.container}>
        {/* Logo */}
        <Link to="/dashboard" style={styles.logo}>
          🎯 ResumeAI
        </Link>

        {/* Nav Links */}
        <div style={styles.links}>
          <Link to="/dashboard" style={{
            ...styles.link,
            ...(isActive('/dashboard') ? styles.activeLink : {})
          }}>
            Dashboard
          </Link>
          <Link to="/upload" style={{
            ...styles.link,
            ...(isActive('/upload') ? styles.activeLink : {})
          }}>
            Upload Resume
          </Link>
          <Link to="/jobs" style={{
            ...styles.link,
            ...(isActive('/jobs') ? styles.activeLink : {})
          }}>
            Jobs
          </Link>
        </div>

        {/* User Info + Logout */}
        <div style={styles.userSection}>
          <Link to="/profile" style={{
            ...styles.userName,
            textDecoration: 'none',
            ...(isActive('/profile') ? { background: 'rgba(255,255,255,0.15)', borderRadius: 8, padding: '4px 10px' } : {}),
          }}>
            👤 {user.fullName || 'User'}
          </Link>
          <button onClick={handleLogout} style={styles.logoutBtn}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}

const styles = {
  nav: {
    background: '#4f46e5',
    boxShadow: '0 2px 8px rgba(79,70,229,0.3)',
    position: 'sticky',
    top: 0,
    zIndex: 100,
    height: 64,
    display: 'flex',
    alignItems: 'center',
  },
  container: {
    maxWidth: 1200,
    margin: '0 auto',
    padding: '0 24px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    width: '100%',
  },
  logo: {
    color: '#fff',
    fontSize: 20,
    fontWeight: 700,
    letterSpacing: '-0.5px',
  },
  links: {
    display: 'flex',
    gap: 8,
  },
  link: {
    color: 'rgba(255,255,255,0.8)',
    padding: '6px 14px',
    borderRadius: 8,
    fontSize: 14,
    fontWeight: 500,
    transition: 'all 0.2s',
  },
  activeLink: {
    color: '#fff',
    background: 'rgba(255,255,255,0.15)',
  },
  userSection: {
    display: 'flex',
    alignItems: 'center',
    gap: 12,
  },
  userName: {
    color: 'rgba(255,255,255,0.9)',
    fontSize: 14,
    fontWeight: 500,
  },
  logoutBtn: {
    background: 'rgba(255,255,255,0.15)',
    color: '#fff',
    border: '1px solid rgba(255,255,255,0.3)',
    padding: '6px 16px',
    borderRadius: 8,
    fontSize: 13,
    fontWeight: 500,
    cursor: 'pointer',
    transition: 'all 0.2s',
  },
};
