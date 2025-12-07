import React, { useEffect, useState } from 'react';
import { Sidebar } from './Dashboard';
import { api } from '../services/api';
import { useNavigate } from 'react-router-dom';
import './FuncionariosPage.css';

function FuncionarioRow({ funcionario, onView, onEdit, onDelete, onManageFolhas }) {
  return (
    <tr>
      <td>{funcionario.id}</td>
      <td>{funcionario.nome}</td>
      <td>{funcionario.cargo}</td>
      <td>{funcionario.usuario?.login}</td>
      <td>{funcionario.usuario?.permissao === 1 ? 'Gerente' : 'Colaborador'}</td>
      <td className="actions">
        <button onClick={() => onView(funcionario)}>Ver detalhes</button>
        <button onClick={() => onEdit(funcionario)}>Editar</button>
        <button onClick={() => onManageFolhas(funcionario)}>Gerir folhas</button>
        <button className="danger" onClick={() => onDelete(funcionario)}>Excluir</button>
      </td>
    </tr>
  );
}

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

export default function FuncionariosPage() {
  const navigate = useNavigate();
  const [funcionarios, setFuncionarios] = useState([]);
  const [funcionarioLogado, setFuncionarioLogado] = useState(null);
  const [selected, setSelected] = useState(null);
  const [showDetails, setShowDetails] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [editData, setEditData] = useState({});

  useEffect(() => {
    async function load() {
      const login = localStorage.getItem('login');
      if (login) {
        try {
          const fRes = await api.get(`/api/funcionarios/by-login/${encodeURIComponent(login)}`);
          setFuncionarioLogado(fRes.data);
        } catch (e) {
          console.error('Erro ao carregar funcionário logado:', e);
        }
      }
      const res = await api.get('/api/funcionarios');
      setFuncionarios(res.data || []);
    }
    load();
  }, []);

  async function handleDelete(funcionario) {
    if (!window.confirm('Deseja realmente excluir este funcionário?')) return;
    await api.delete(`/api/funcionarios/${funcionario.id}`);
    setFuncionarios(prev => prev.filter(f => f.id !== funcionario.id));
  }

  function openDetails(funcionario) {
    setSelected(funcionario);
    setShowDetails(true);
  }

  function openEdit(funcionario) {
    setSelected(funcionario);
    setEditData({
      cargo: funcionario.cargo || '',
      dependentes: funcionario.dependentes ?? 0,
      salarioBase: funcionario.salarioBase ?? 0,
      aptoPericulosidade: funcionario.aptoPericulosidade || false,
      grauInsalubridade: funcionario.grauInsalubridade ?? 0,
      valeTransporte: funcionario.valeTransporte || false,
      valorVT: funcionario.valorVT ?? 0,
      valeAlimentacao: funcionario.valeAlimentacao || false,
      valorVA: funcionario.valorVA ?? 0
    });
    setShowEdit(true);
  }

  async function saveEdit() {
    if (!selected) return;
    await api.patch(`/api/funcionarios/${selected.id}`, editData);
    const res = await api.get('/api/funcionarios');
    setFuncionarios(res.data || []);
    setShowEdit(false);
  }

  function goToFolhas(funcionario) {
    navigate(`/funcionarios/${funcionario.id}/folhas`, { state: { funcionario } });
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
              <h1>Funcionarios</h1>
            </div>
          </header>

          <div className="sections">
            <table className="func-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nome</th>
                  <th>Cargo</th>
                  <th>Login</th>
                  <th>Permissão</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {funcionarios.map(f => (
                  <FuncionarioRow
                    key={f.id}
                    funcionario={f}
                    onView={openDetails}
                    onEdit={openEdit}
                    onDelete={handleDelete}
                    onManageFolhas={goToFolhas}
                  />
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <Modal open={showDetails} title="Detalhes do funcionário" onClose={() => setShowDetails(false)}>
          {selected && (
            <div className="details-grid">
              <div><strong>Nome:</strong> {selected.nome}</div>
              <div><strong>CPF:</strong> {selected.cpf}</div>
              <div><strong>Cargo:</strong> {selected.cargo}</div>
              <div><strong>Dependentes:</strong> {selected.dependentes}</div>
              <div><strong>Salário base:</strong> R$ {Number(selected.salarioBase || 0).toFixed(2)}</div>
              <div><strong>Periculosidade:</strong> {selected.aptoPericulosidade ? 'Sim' : 'Não'}</div>
              <div><strong>Insalubridade:</strong> {selected.grauInsalubridade}</div>
              <div><strong>VT:</strong> {selected.valeTransporte ? 'Sim' : 'Não'}</div>
              <div><strong>VA:</strong> {selected.valeAlimentacao ? 'Sim' : 'Não'}</div>
            </div>
          )}
        </Modal>

        <Modal open={showEdit} title="Editar funcionário" onClose={() => setShowEdit(false)}>
          {selected && (
            <form className="edit-form" onSubmit={e => { e.preventDefault(); saveEdit(); }}>
              <label>
                Cargo
                <input
                  type="text"
                  value={editData.cargo}
                  onChange={e => setEditData({ ...editData, cargo: e.target.value })}
                />
              </label>
              <label>
                Dependentes
                <input
                  type="number"
                  value={editData.dependentes}
                  onChange={e => setEditData({ ...editData, dependentes: Number(e.target.value) })}
                />
              </label>
              <label>
                Salário base
                <input
                  type="number"
                  step="0.01"
                  value={editData.salarioBase}
                  onChange={e => setEditData({ ...editData, salarioBase: Number(e.target.value) })}
                />
              </label>
              <label>
                Periculosidade
                <select
                  value={editData.aptoPericulosidade ? 'true' : 'false'}
                  onChange={e => setEditData({ ...editData, aptoPericulosidade: e.target.value === 'true' })}
                >
                  <option value="false">Não</option>
                  <option value="true">Sim</option>
                </select>
              </label>
              <label>
                Insalubridade (grau)
                <input
                  type="number"
                  value={editData.grauInsalubridade}
                  onChange={e => setEditData({ ...editData, grauInsalubridade: Number(e.target.value) })}
                />
              </label>
              <label>
                Vale transporte
                <select
                  value={editData.valeTransporte ? 'true' : 'false'}
                  onChange={e => setEditData({ ...editData, valeTransporte: e.target.value === 'true' })}
                >
                  <option value="false">Não</option>
                  <option value="true">Sim</option>
                </select>
              </label>
              {editData.valeTransporte && (
                <label>
                  Valor VT (dia)
                  <input
                    type="number"
                    step="0.01"
                    value={editData.valorVT}
                    onChange={e => setEditData({ ...editData, valorVT: Number(e.target.value) })}
                  />
                </label>
              )}
              <label>
                Vale alimentação
                <select
                  value={editData.valeAlimentacao ? 'true' : 'false'}
                  onChange={e => setEditData({ ...editData, valeAlimentacao: e.target.value === 'true' })}
                >
                  <option value="false">Não</option>
                  <option value="true">Sim</option>
                </select>
              </label>
              {editData.valeAlimentacao && (
                <label>
                  Valor VA (dia)
                  <input
                    type="number"
                    step="0.01"
                    value={editData.valorVA}
                    onChange={e => setEditData({ ...editData, valorVA: Number(e.target.value) })}
                  />
                </label>
              )}

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
