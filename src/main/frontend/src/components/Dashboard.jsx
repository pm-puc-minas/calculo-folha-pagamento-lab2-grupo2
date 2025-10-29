import React from 'react';
import { Link, NavLink } from 'react-router-dom';
import './Dashboard.css';

function Sidebar({ user }) {
  return (
    <aside className="sidebar">
      <div className="brand">PayZen</div>
      <div className="user-card">
        <div className="avatar" aria-hidden />
        <div className="user-info">
          <div className="user-name">{user?.nome || 'Usuário'}</div>
          <div className="user-role">{user?.cargo || 'Colaborador'}</div>
        </div>
      </div>
      <nav className="menu">
        <NavLink to="." end className="menu-item">Visão Geral</NavLink>
        <NavLink to="salarios" className="menu-item">Histórico Salarial</NavLink>
        <NavLink to="horas" className="menu-item">Horas Trabalhadas</NavLink>
        <NavLink to="config" className="menu-item">Configurações</NavLink>
        <NavLink to="relatorios" className="menu-item">Relatórios</NavLink>
      </nav>
      <div className="sidebar-footer">
        <Link className="logout" to="/logout">Sair</Link>
      </div>
    </aside>
  );
}

function Card({ title, children }) {
  return (
    <div className="card">
      <div className="card-title">{title}</div>
      <div className="card-body">{children}</div>
    </div>
  );
}

export default function Dashboard() {
  const user = JSON.parse(localStorage.getItem('user') || 'null');

  return (
    <div className="dashboard-layout">
      <Sidebar user={user} />
      <main className="content">
        <div className="content-wrap">
          <header className="content-header">
            <h1>Visão Geral</h1>
            <div className="header-actions">
              <button className="btn primary">Baixar Relatório</button>
            </div>
          </header>

          <div className="sections">
            <section className="grid grid-2">
              <Card title="Proventos">
                <ul className="list">
                  <li>Salário base</li>
                  <li>Periculosidade</li>
                  <li>Insalubridade</li>
                </ul>
              </Card>
              <Card title="Descontos">
                <ul className="list">
                  <li>INSS</li>
                  <li>FGTS</li>
                  <li>IRRF</li>
                </ul>
              </Card>
            </section>

            <section className="grid grid-3">
              <Card title="Bruto do salário">
                <div className="kpi">R$ 0,00</div>
              </Card>
              <Card title="Salário por hora">
                <div className="kpi">R$ 0,00</div>
              </Card>
              <Card title="Salário líquido">
                <div className="kpi success">R$ 0,00</div>
              </Card>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}
