import React, { useEffect, useState, useMemo } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import './Dashboard.css';
import { api } from '../services/api';

export function Sidebar({ user }) {
  return (
    <aside className="sidebar">
      <div className="brand">PayPaper</div>
      <div className="user-card">
        <div className="avatar" aria-hidden />
        <div className="user-info">
          <div className="user-name">{user?.nome || 'Usuário'}</div>
          <div className="user-role">{user?.cargo || 'Colaborador'}</div>
        </div>
      </div>
      <nav className="menu">
        <NavLink to="/" end className="menu-item">Visão Geral</NavLink>
        <NavLink to="/salarios" className="menu-item">Histórico Salarial</NavLink>
        <NavLink to="/horas" className="menu-item">Horas Trabalhadas</NavLink>
        <NavLink to="/config" className="menu-item">Configurações</NavLink>
        <NavLink to="/relatorios" className="menu-item">Relatórios</NavLink>
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
  const navigate = useNavigate();
  const login = localStorage.getItem('login');
  const [funcionario, setFuncionario] = useState(null);
  const [folhas, setFolhas] = useState([]);
  const [irrf, setIrrf] = useState(0);

  useEffect(() => {
    async function load() {
      if(!login){
        navigate('/login', { replace: true });
        return;
      }
      try {
        const [fRes, folhasRes] = await Promise.all([
          api.get(`/api/funcionarios/by-login/${encodeURIComponent(login)}`),
          api.get(`/folha/by-login/${encodeURIComponent(login)}`)
        ]);
        const fData = fRes.data;
        setFuncionario(fData);
        setFolhas(folhasRes.data || []);
        if (fData) {
          const det = await api.post('/folha/calcular', { funcionario: fData, diasUteis: 22 });
          setIrrf(Number(det.data?.descontoIRRF || 0));
        }
      } catch (e) {
        console.error(e);
      }
    }
    load();
  }, [login, navigate]);

  const user = funcionario || JSON.parse(localStorage.getItem('user') || 'null');

  const ultimaFolha = useMemo(() => folhas && folhas.length ? folhas[folhas.length-1] : null, [folhas]);

  const salarioBase = funcionario?.salarioBase ?? 0;
  const salarioBruto = ultimaFolha?.salarioBruto ?? funcionario?.salarioBruto ?? 0;
  const salarioLiquido = ultimaFolha?.salarioLiquido ?? (salarioBruto - (Number(funcionario?.descontoINSS || 0)));

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
                  <li>Salário base: R$ {Number(salarioBase).toFixed(2)}</li>
                  <li>Periculosidade: {funcionario?.aptoPericulosidade ? 'Sim' : 'Não'}</li>
                  <li>Insalubridade: {funcionario?.grauInsalubridade ?? 0}%</li>
                </ul>
              </Card>
              <Card title="Descontos">
                <ul className="list">
                  <li>INSS: R$ {Number(funcionario?.descontoINSS || 0).toFixed(2)}</li>
                  <li>IRRF: R$ {Number(irrf).toFixed(2)}</li>
                  <li>Total descontos: R$ {Number(ultimaFolha?.totalDescontos || 0).toFixed(2)}</li>
                </ul>
              </Card>
            </section>

            <section className="grid grid-3">
              <Card title="Bruto do salário">
                <div className="kpi">R$ {Number(salarioBruto).toFixed(2)}</div>
              </Card>
              <Card title="Salário por hora">
                <div className="kpi">R$ {(Number(salarioBase)/220).toFixed(2)}</div>
              </Card>
              <Card title="Salário líquido">
                <div className="kpi success">R$ {Number(salarioLiquido).toFixed(2)}</div>
              </Card>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}
