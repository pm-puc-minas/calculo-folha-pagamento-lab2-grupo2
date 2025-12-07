import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { Sidebar } from './Dashboard';
import { api } from '../services/api';
import './FuncionariosPage.css';

function Modal({ open, title, children, onClose }) {
  if (!open) return null;
  return (
    <div className="modal-backdrop">
      <div className="modal">
        <div className="modal-header">
          <h2>{title}</h2>
          <button onClick={onClose}>X</button>
        </div>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  );
}

export default function GerirFolhasPage() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const [funcionario, setFuncionario] = useState(location.state?.funcionario || null);
  const [funcionarioLogado, setFuncionarioLogado] = useState(null);
  const [folhas, setFolhas] = useState([]);
  const [novaFolha, setNovaFolha] = useState({ mes: '', ano: '', diasUteis: 22 });
  const [selectedFolha, setSelectedFolha] = useState(null);
  const [showFolhaEdit, setShowFolhaEdit] = useState(false);
  const [folhaEditData, setFolhaEditData] = useState({ diasUteis: 22 });

  useEffect(() => {
    async function load() {
      try {
        const login = localStorage.getItem('login');
        if (login) {
          try {
            const fLogadoRes = await api.get(`/api/funcionarios/by-login/${encodeURIComponent(login)}`);
            setFuncionarioLogado(fLogadoRes.data);
          } catch (e) {
            console.error('Erro ao carregar funcionário logado:', e);
          }
        }
        if (!funcionario) {
          const fRes = await api.get(`/api/funcionarios/${id}`);
          setFuncionario(fRes.data);
        }
        const res = await api.get(`/api/funcionarios/${id}/folhas`);
        setFolhas(res.data || []);
      } catch (e) {
        console.error(e);
      }
    }
    load();
  }, [id, funcionario]);

  async function criarFolha(e) {
    e.preventDefault();
    try {
      const body = {
        mes: Number(novaFolha.mes),
        ano: Number(novaFolha.ano),
        diasUteis: Number(novaFolha.diasUteis) || 22
      };
      await api.post(`/api/funcionarios/${id}/folhas`, body);
      const res = await api.get(`/api/funcionarios/${id}/folhas`);
      setFolhas(res.data || []);
      setNovaFolha({ mes: '', ano: '', diasUteis: 22 });
      alert('Folha criada com sucesso!');
    } catch (error) {
      console.error('Erro ao criar folha:', error);
      alert(`Erro ao criar folha: ${error.response?.data?.message || error.message}`);
    }
  }

  async function excluirFolha(folha) {
    if (!window.confirm('Excluir esta folha?')) return;
    await api.delete(`/api/funcionarios/${id}/folhas/${folha.id}`);
    setFolhas(prev => prev.filter(f => f.id !== folha.id));
  }

  function openFolhaEdit(folha) {
    setSelectedFolha(folha);
    setFolhaEditData({ diasUteis: folha.diasUteis || 22 });
    setShowFolhaEdit(true);
  }

  async function saveFolhaEdit(e) {
    e.preventDefault();
    if (!selectedFolha) return;
    await api.put(`/api/funcionarios/${id}/folhas/${selectedFolha.id}`, folhaEditData);
    const res = await api.get(`/api/funcionarios/${id}/folhas`);
    setFolhas(res.data || []);
    setShowFolhaEdit(false);
  }

  const user = funcionarioLogado ? {
    ...funcionarioLogado,
    permissao: funcionarioLogado.usuario?.permissao
  } : null;

  return (
    <div className="dashboard-layout">
      <Sidebar user={user} />
      <main className="content">
        <div className="content-wrap">
          <header className="content-header">
            <div>
              <h1>Folhas de pagamento</h1>
              {funcionario && (
                <div className="folha-ref">
                  Funcionário: {funcionario.nome} ({funcionario.cargo})
                </div>
              )}
            </div>
            <div className="header-actions">
              <button className="btn" onClick={() => navigate('/funcionarios')}>Voltar</button>
            </div>
          </header>

          <div className="sections">
            <section className="grid grid-1">
              <form className="nova-folha-form" onSubmit={criarFolha}>
                <div className="row">
                  <label>
                    Mês
                    <input
                      type="number"
                      min="1"
                      max="12"
                      value={novaFolha.mes}
                      onChange={e => setNovaFolha({ ...novaFolha, mes: e.target.value })}
                      required
                    />
                  </label>
                  <label>
                    Ano
                    <input
                      type="number"
                      min="2000"
                      value={novaFolha.ano}
                      onChange={e => setNovaFolha({ ...novaFolha, ano: e.target.value })}
                      required
                    />
                  </label>
                  <label>
                    Dias úteis
                    <input
                      type="number"
                      min="1"
                      max="31"
                      value={novaFolha.diasUteis}
                      onChange={e => setNovaFolha({ ...novaFolha, diasUteis: e.target.value })}
                    />
                  </label>
                  <button type="submit">Criar nova folha</button>
                </div>
              </form>
            </section>

            <section className="grid grid-1">
              <table className="folhas-table">
                <thead>
                  <tr>
                    <th>Mês/Ano</th>
                    <th>Bruto</th>
                    <th>INSS</th>
                    <th>IRRF</th>
                    <th>Total Desc.</th>
                    <th>Líquido</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {folhas.map(f => (
                    <tr key={f.id}>
                      <td>{String(f.mesReferencia).padStart(2, '0')}/{f.anoReferencia}</td>
                      <td>R$ {Number(f.salarioBruto || 0).toFixed(2)}</td>
                      <td>R$ {Number(f.inss || 0).toFixed(2)}</td>
                      <td>R$ {Number(f.irrf || 0).toFixed(2)}</td>
                      <td>R$ {Number(f.totalDescontos || 0).toFixed(2)}</td>
                      <td>R$ {Number(f.salarioLiquido || 0).toFixed(2)}</td>
                      <td className="actions">
                        <button onClick={() => openFolhaEdit(f)}>Editar</button>
                        <button className="danger" onClick={() => excluirFolha(f)}>Excluir</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>
          </div>
        </div>

        <Modal
          open={showFolhaEdit}
          title="Editar folha"
          onClose={() => setShowFolhaEdit(false)}
        >
          {selectedFolha && (
            <form className="edit-form" onSubmit={saveFolhaEdit}>
              <label>
                Mês referência
                <input type="number" value={selectedFolha.mesReferencia} disabled />
              </label>
              <label>
                Ano referência
                <input type="number" value={selectedFolha.anoReferencia} disabled />
              </label>
              <label>
                Dias úteis
                <input
                  type="number"
                  min="1"
                  max="31"
                  value={folhaEditData.diasUteis}
                  onChange={e => setFolhaEditData({ diasUteis: Number(e.target.value) })}
                />
              </label>
              <div className="modal-actions">
                <button type="submit">Salvar</button>
              </div>
            </form>
          )}
        </Modal>
      </main>
    </div>
  );
}

