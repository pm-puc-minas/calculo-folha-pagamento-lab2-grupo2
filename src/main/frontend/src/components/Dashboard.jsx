import React, { useEffect, useState, useMemo } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import './Dashboard.css';
import { api } from '../services/api';

export function Sidebar({ user }) {
  const isManager = user?.permissao === 1;
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
        {isManager && <NavLink to="/funcionarios" className="menu-item">Funcionarios</NavLink>}
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
  const storedUser = (() => {
    try { return JSON.parse(localStorage.getItem('user') || 'null'); } catch { return null; }
  })();
  const login = storedUser?.login || localStorage.getItem('login');
  const [funcionario, setFuncionario] = useState(null);
  const [folhas, setFolhas] = useState([]);
  const [folhaSelecionada, setFolhaSelecionada] = useState(null);

  const userInfo = storedUser;

  const papel = userInfo?.permissao === 2 ? 'administrador' : (userInfo?.permissao === 1 ? 'gestor' : 'funcionário');
  const saudacao = useMemo(() => { const h = new Date().getHours(); return h < 12 ? 'Bom dia' : (h < 18 ? 'Boa tarde' : 'Boa noite') }, []);
  const nomeExibicao = funcionario?.nome || userInfo?.login || 'Usuário';

  useEffect(() => {
    async function load() {
      if (!login) {
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
        const folhasData = Array.isArray(folhasRes.data) ? folhasRes.data : [];
        setFolhas(folhasData);
        if (folhasData.length > 0) {
          const ordenadas = [...folhasData].sort((a, b) => {
            if (a.anoReferencia !== b.anoReferencia) {
              return a.anoReferencia - b.anoReferencia;
            }
            return a.mesReferencia - b.mesReferencia;
          });
          setFolhaSelecionada(ordenadas[ordenadas.length - 1]);
        }
      } catch (e) {
        console.error('Erro ao carregar dados:', e);
      }
    }
    load();
  }, [login, navigate]);

  const user = useMemo(() => {
    if (funcionario) {
      return {
        ...funcionario,
        permissao: funcionario.usuario?.permissao
      };
    }
    return userInfo;
  }, [funcionario, userInfo]);

  const folhaExibida = folhaSelecionada || (folhas.length > 0 ? folhas[folhas.length - 1] : null);

  const referenciaFolha = folhaExibida
    ? `${String(folhaExibida.mesReferencia).padStart(2, '0')}/${folhaExibida.anoReferencia}`
    : '-';

  const salarioBase = funcionario?.salarioBase ?? 0;
  const salarioBruto = folhaExibida?.salarioBruto ?? 0;
  const salarioLiquido = folhaExibida?.salarioLiquido ?? 0;

  const inss = folhaExibida?.descontoINSS ?? Number(funcionario?.descontoINSS || 0);
  const totalDescontosFolha = Number(folhaExibida?.totalDescontos || 0);
  const irrfExibido = folhaExibida?.descontoIRRF ?? 0;
  const valeTransporteDesconto = folhaExibida?.descontoValeTransporte ?? 0;

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
            <h1>Visão Geral</h1>
          </header>

          <div className="sections">
            {folhas.length > 0 ? (
              <div className="folha-selector">
                <label htmlFor="folha-select">Selecionar folha:</label>
                <select
                  id="folha-select"
                  className="folha-dropdown"
                  value={folhaSelecionada?.id || ''}
                  onChange={(e) => {
                    const selectedId = e.target.value;
                    if (selectedId) {
                      const selectedFolha = folhas.find(f => f.id === Number(selectedId));
                      setFolhaSelecionada(selectedFolha);
                    } else {
                      if (folhas.length > 0) {
                        const ordenadas = [...folhas].sort((a, b) => {
                          if (a.anoReferencia !== b.anoReferencia) {
                            return a.anoReferencia - b.anoReferencia;
                          }
                          return a.mesReferencia - b.mesReferencia;
                        });
                        setFolhaSelecionada(ordenadas[ordenadas.length - 1]);
                      }
                    }
                  }}
                >
                  <option value="">Última folha</option>
                  {folhas.map(folha => (
                    <option key={folha.id} value={folha.id}>
                      {`${String(folha.mesReferencia).padStart(2, '0')}/${folha.anoReferencia}`}
                    </option>
                  ))}
                </select>
                <span className="folha-info">({folhas.length} folha{folhas.length > 1 ? 's' : ''} disponível{folhas.length > 1 ? 'is' : ''})</span>
              </div>
            ) : (
              <div className="folha-selector">
                <p className="muted">Nenhuma folha de pagamento encontrada para este usuário.</p>
              </div>
            )}

            <section className="grid grid-2">
              <Card title="Informações Salariais">
                <ul className="list">
                  <li>Salário base: R$ {Number(salarioBase).toFixed(2)}</li>
                  <li>Periculosidade: {funcionario?.aptoPericulosidade ? 'Sim' : 'Não'}</li>
                  <li>Insalubridade: {funcionario?.grauInsalubridade > 0 ? `Grau ${funcionario.grauInsalubridade}` : 'Não'}</li>
                </ul>
              </Card>
              <Card title="Descontos">
                <ul className="list">
                  <li>INSS: R$ {Number(inss).toFixed(2)}</li>
                  <li>IRRF: R$ {Number(irrfExibido).toFixed(2)}</li>
                  <li>Vale Transporte: R$ {Number(valeTransporteDesconto).toFixed(2)}</li>
                  <li>Total descontos: R$ {totalDescontosFolha.toFixed(2)}</li>
                </ul>
              </Card>
            </section>

            <section className="grid grid-3">
              <Card title="Bruto do salário">
                <div className="kpi">R$ {Number(salarioBruto).toFixed(2)}</div>
              </Card>
              <Card title="Salário por hora">
                <div className="kpi">R$ {(Number(salarioBase) / 220).toFixed(2)}</div>
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
