import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import './Dashboard.css';
import { Sidebar } from './Dashboard';

export default function HoursWorked() {
  const navigate = useNavigate();
  const login = localStorage.getItem('login');
  const [funcionario, setFuncionario] = useState(null);
  const [mes, setMes] = useState(new Date().getMonth() + 1);
  const [ano, setAno] = useState(new Date().getFullYear());
  const [detalhe, setDetalhe] = useState(null);
  const [loading, setLoading] = useState(false);
  const [nenhumaFolha, setNenhumaFolha] = useState(false);

  useEffect(() => {
    if (!login) {
      navigate('/login', { replace: true });
      return;
    }
    async function loadFuncionario() {
      try {
        const res = await api.get(`/api/funcionarios/by-login/${encodeURIComponent(login)}`);
        setFuncionario(res.data);
      } catch (e) {
        console.error(e);
      }
    }
    loadFuncionario();
  }, [login, navigate]);

  async function handleCalcular(e) {
    e.preventDefault();
    if (!login) return;
    setLoading(true);
    setNenhumaFolha(false);
    setDetalhe(null);
    try {
      const res = await api.get(`/folha/by-login/${encodeURIComponent(login)}`);
      const folhas = Array.isArray(res.data) ? res.data : [];
      const alvo = folhas.find(f => Number(f.mesReferencia) === Number(mes) && Number(f.anoReferencia) === Number(ano));
      if (!alvo) {
        setNenhumaFolha(true);
      } else {
        setDetalhe(alvo);
        setNenhumaFolha(false);
      }
    } catch (err) {
      console.error(err);
      setNenhumaFolha(true);
    } finally {
      setLoading(false);
    }
  }

  function formatBRL(v) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v || 0));
  }

  return (
    <div className="dashboard-layout">
      <Sidebar user={funcionario} />
      <main className="content">
        <div className="content-wrap">
          <header className="content-header">
            <h1>Consultar folhas</h1>
          </header>
          <div className="sections">
            <section className="card">
              <div className="card-title">Referência</div>
              <div className="card-body">
                <form className="form-grid" onSubmit={handleCalcular}>
                  <div className="form-group">
                    <label>Mês de referência</label>
                    <input
                      type="number"
                      min="1"
                      max="12"
                      value={mes}
                      onChange={e => setMes(e.target.value)}
                    />
                  </div>
                  <div className="form-group">
                    <label>Ano de referência</label>
                    <input
                      type="number"
                      min="2000"
                      max="2050"
                      value={ano}
                      onChange={e => setAno(e.target.value)}
                    />
                  </div>
                  <div className="form-actions">
                    <button type="submit" className="btn primary" disabled={loading}>
                      {loading ? 'Carregando...' : 'Consultar folha'}
                    </button>
                  </div>
                </form>
              </div>
            </section>

            {detalhe && !nenhumaFolha && (
              <section className="grid grid-3">
                <div className="card">
                  <div className="card-title">Proventos</div>
                  <div className="card-body">
                    <div>Salário base: {formatBRL(detalhe.salarioBruto - detalhe.totalAdicionais)}</div>
                    <div>Adicionais: {formatBRL(detalhe.totalAdicionais)}</div>
                    <div>Bruto: {formatBRL(detalhe.salarioBruto)}</div>
                  </div>
                </div>
                <div className="card">
                  <div className="card-title">Descontos</div>
                  <div className="card-body">
                    <div>Total descontos: {formatBRL(detalhe.totalDescontos)}</div>
                  </div>
                </div>
                <div className="card">
                  <div className="card-title">Resultado</div>
                  <div className="card-body">
                    <div>Salário líquido: {formatBRL(detalhe.salarioLiquido)}</div>
                    <div>Benefícios: {formatBRL(detalhe.totalBeneficios)}</div>
                  </div>
                </div>
              </section>
            )}

            {!detalhe && nenhumaFolha && (
              <section className="card">
                <div className="card-title">Resultado</div>
                <div className="card-body">
                  <div className="muted">Nenhuma folha encontrada para o período selecionado.</div>
                </div>
              </section>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
