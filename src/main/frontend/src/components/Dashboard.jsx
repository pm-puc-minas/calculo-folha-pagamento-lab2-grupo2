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
        <NavLink to="/horas" className="menu-item">Consultar folhas</NavLink>
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

  const userInfo = useMemo(()=>{ try{ return JSON.parse(localStorage.getItem('user')||'null') }catch{ return null } },[]);
  const papel = userInfo?.permissao === 2 ? 'administrador' : (userInfo?.permissao === 1 ? 'gestor' : 'funcionário');
  const saudacao = useMemo(()=>{ const h=new Date().getHours(); return h<12?'Bom dia':(h<18?'Boa tarde':'Boa noite') },[]);
  const nomeExibicao = funcionario?.nome || userInfo?.login || 'Usuário';

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

  const ultimaFolha = useMemo(() => {
    if (!folhas || !folhas.length) return null;
    const ordenadas = [...folhas].sort((a, b) => {
      if ((a.anoReferencia || 0) !== (b.anoReferencia || 0)) {
        return (a.anoReferencia || 0) - (b.anoReferencia || 0);
      }
      return (a.mesReferencia || 0) - (b.mesReferencia || 0);
    });
    return ordenadas[ordenadas.length - 1];
  }, [folhas]);

  const referenciaFolha = ultimaFolha
    ? `${String(ultimaFolha.mesReferencia).padStart(2, '0')}/${ultimaFolha.anoReferencia}`
    : '-';

  const salarioBase = funcionario?.salarioBase ?? 0;
  const salarioBruto = ultimaFolha?.salarioBruto ?? funcionario?.salarioBruto ?? 0;
  const salarioLiquido = ultimaFolha?.salarioLiquido ?? (salarioBruto - (Number(funcionario?.descontoINSS || 0)));

  const inss = Number(funcionario?.descontoINSS || 0);
  const totalDescontosFolha = Number(ultimaFolha?.totalDescontos || 0);
  const valeTransporteDesconto = Math.max(totalDescontosFolha - inss - Number(irrf || 0), 0);

  return (
    <div className="dashboard-layout">
      <Sidebar user={user} />
      <main className="content">
        <div className="content-wrap">
          <div className="welcome-banner">
            <h2 className="welcome-title">{saudacao}, {nomeExibicao}!</h2>
            <div className="welcome-subtitle">Bem-vindo ao PayPaper. Você está acessando como {papel}.</div>
          </div>
          <header className="content-header">
            <div>
              <h1>Visão Geral</h1>
              <div className="folha-ref">Folha de referência: {referenciaFolha}</div>
            </div>
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
                  <li>INSS: R$ {inss.toFixed(2)}</li>
                  <li>IRRF: R$ {Number(irrf).toFixed(2)}</li>
                  <li>Vale transporte: R$ {valeTransporteDesconto.toFixed(2)}</li>
                  <li>Total descontos: R$ {totalDescontosFolha.toFixed(2)}</li>
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
