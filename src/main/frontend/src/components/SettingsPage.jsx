import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sidebar } from './Dashboard';
import { api } from '../services/api';
import './Dashboard.css';

export default function SettingsPage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [darkMode, setDarkMode] = useState(false);

  useEffect(() => {
    let storedUser;
    try {
      storedUser = JSON.parse(localStorage.getItem('user') || 'null');
    } catch {
      storedUser = null;
    }
    if (!storedUser) {
      navigate('/login', { replace: true });
      return;
    }

    const login = localStorage.getItem('login');
    if (!login) {
      setUser(storedUser);
    } else {
      api.get(`/api/funcionarios/by-login/${encodeURIComponent(login)}`)
        .then(res => {
          setUser(res.data || storedUser);
        })
        .catch(() => {
          setUser(storedUser);
        });
    }

    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      setDarkMode(true);
      document.body.classList.add('theme-dark');
    }
  }, [navigate]);

  function handleToggleDarkMode() {
    const next = !darkMode;
    setDarkMode(next);
    if (next) {
      document.body.classList.add('theme-dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.body.classList.remove('theme-dark');
      localStorage.setItem('theme', 'light');
    }
  }

  return (
    <div className="dashboard-layout">
      <Sidebar user={user} />
      <main className="content">
        <div className="content-wrap">
          <header className="content-header">
            <h1>Configurações</h1>
          </header>
          <div className="sections">
            <section className="card">
              <div className="card-title">Aparência</div>
              <div className="card-body">
                <label className="toggle-row">
                  <span>Modo escuro</span>
                  <input
                    type="checkbox"
                    checked={darkMode}
                    onChange={handleToggleDarkMode}
                  />
                </label>
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}
