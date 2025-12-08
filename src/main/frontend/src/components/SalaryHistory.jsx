import React, { useEffect, useState } from 'react';
import { api, downloadRelatorioPdf } from '../services/api';
import './Dashboard.css';
import { Sidebar } from './Dashboard';
import { useNavigate } from 'react-router-dom';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

function formatBRL(v){
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v||0));
}

function CustomTooltip({ active, payload }) {
  if (!active || !payload || !payload.length) return null;
  const ref = payload[0].payload.referencia;
  return (
    <div
      style={{
        background: '#111827',
        borderRadius: 8,
        padding: 10,
        boxShadow: '0 8px 18px rgba(0,0,0,0.45)',
        border: '1px solid #374151',
        minWidth: 140,
      }}
    >
      <p
        style={{
          margin: '0 0 6px 0',
          fontWeight: 700,
          fontSize: '0.9rem',
          color: '#BBF244',
        }}
      >
        {ref}
      </p>
      {payload.map((entry, index) => (
        <p
          key={index}
          style={{
            margin: '2px 0',
            fontSize: '0.8rem',
            color: '#f9fafb',
          }}
        >
          <span
            style={{
              display: 'inline-block',
              width: 8,
              height: 8,
              borderRadius: '50%',
              backgroundColor: entry.color,
              marginRight: 6,
            }}
          />
          {entry.name}: {formatBRL(entry.value)}
        </p>
      ))}
    </div>
  );
}

export default function SalaryHistory(){
  const navigate = useNavigate();
  const storedUser = (() => {
    try { return JSON.parse(localStorage.getItem('user') || 'null'); } catch { return null; }
  })();
  const login = storedUser?.login || localStorage.getItem('login');
  const [funcionario, setFuncionario] = useState(null);
  const [folhas, setFolhas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(()=>{
    async function load(){
      if(!login){
        setLoading(false);
        navigate('/login', { replace: true });
        return;
      }
      try{
        const [f, fs] = await Promise.all([
          api.get(`/api/funcionarios/by-login/${encodeURIComponent(login)}`),
          api.get(`/folha/by-login/${encodeURIComponent(login)}`)
        ]);
        setFuncionario(f.data);
        setFolhas((fs.data || []).sort((a,b)=> (a.anoReferencia - b.anoReferencia) || (a.mesReferencia - b.mesReferencia)));
      }catch(e){
        console.error(e);
        alert('Não foi possível carregar histórico.');
      }finally{ setLoading(false); }
    }
    load();
  },[login, navigate]);

  const ultimo = folhas.length ? folhas[folhas.length-1] : null;

  const chartData = folhas.map(f => ({
    referencia: `${String(f.mesReferencia).padStart(2,'0')}/${f.anoReferencia}`,
    salarioBruto: Number(f.salarioBruto || 0),
    salarioLiquido: Number(f.salarioLiquido || 0),
    descontos: Number(f.totalDescontos || 0),
    beneficios: Number(f.totalBeneficios || 0),
  }));

  const user = funcionario ? {
    ...funcionario,
    permissao: funcionario.usuario?.permissao
  } : null;

  async function handleDownloadPdf() {
    if (!login || !folhas.length) return;
    try {
      const blobData = await downloadRelatorioPdf(login);
      const blob = new Blob([blobData], { type: 'application/pdf' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `historico_${login}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (e) {
      console.error(e);
      alert('Não foi possível gerar o relatório em PDF.');
    }
  }

  return (
    <div className="dashboard-layout">
      <Sidebar user={user} />
      <main className="content">
        <div className="content-wrap">
          <header className="content-header">
            <h1>Histórico Salarial</h1>
            <div className="header-actions">
              {login && folhas.length > 0 && (
                <button type="button" className="btn primary" onClick={handleDownloadPdf}>
                  📄 Baixar Relatório
                </button>
              )}
            </div>
          </header>

          <div className="sections">
            <section className="grid grid-3">
              <div className="card">
                <div className="card-title">Colaborador</div>
                <div className="card-body">
                  <div><strong>Nome:</strong> {funcionario?.nome || '-'}</div>
                  <div><strong>Cargo:</strong> {funcionario?.cargo || '-'}</div>
                  <div><strong>Salário base:</strong> {formatBRL(funcionario?.salarioBase || 0)}</div>
                </div>
              </div>
              <div className="card">
                <div className="card-title">Última Folha</div>
                <div className="card-body">
                  <div><strong>Referência:</strong> {ultimo ? `${String(ultimo.mesReferencia).padStart(2,'0')}/${ultimo.anoReferencia}` : '-'}</div>
                  <div><strong>Bruto:</strong> {formatBRL(ultimo?.salarioBruto || 0)}</div>
                  <div><strong>Líquido:</strong> <span className="kpi success">{formatBRL(ultimo?.salarioLiquido || 0)}</span></div>
                </div>
              </div>
              <div className="card">
                <div className="card-title">Descontos/Benefícios</div>
                <div className="card-body">
                  <div><strong>Adicionais:</strong> {formatBRL(ultimo?.totalAdicionais || 0)}</div>
                  <div><strong>Benefícios:</strong> {formatBRL(ultimo?.totalBeneficios || 0)}</div>
                  <div><strong>Descontos:</strong> {formatBRL(ultimo?.totalDescontos || 0)}</div>
                </div>
              </div>
            </section>

            <section className="card">
              <div className="card-title">Evolução Salarial</div>
              <div className="card-body">
                {folhas.length > 0 ? (
                  <ResponsiveContainer width="100%" height={350}>
                    <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                      <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" opacity={0.3} />
                      <XAxis 
                        dataKey="referencia" 
                        stroke="var(--muted)"
                        style={{ fontSize: '0.85rem' }}
                      />
                      <YAxis 
                        stroke="var(--muted)"
                        style={{ fontSize: '0.85rem' }}
                        tickFormatter={(value) => `R$ ${(value/1000).toFixed(1)}k`}
                      />
                      <Tooltip content={<CustomTooltip />} />
                      <Legend
                        wrapperStyle={{ paddingTop: '20px' }}
                        iconType="circle"
                      />
                      <Line 
                        type="monotone" 
                        dataKey="salarioBruto"
                        stroke="#94BF36"
                        strokeWidth={3}
                        dot={{ fill: '#94BF36', r: 5 }}
                        activeDot={{ r: 7 }}
                      />
                      <Line
                        type="monotone"
                        dataKey="salarioLiquido"
                        stroke="#BBF244"
                        strokeWidth={3}
                        dot={{ fill: '#BBF244', r: 5 }}
                        activeDot={{ r: 7 }}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                ) : (
                  <div className="empty-state">Nenhum dado disponível para exibição.</div>
                )}
              </div>
            </section>

            <section className="card">
              <div className="card-title">Folhas de Pagamento</div>
              <div className="card-body">
                {loading ? (
                  <div>Carregando...</div>
                ) : folhas.length === 0 ? (
                  <div className="empty-state">Nenhuma folha de pagamento encontrada.</div>
                ) : (
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Mês/Ano</th>
                        <th>Bruto</th>
                        <th>Adicionais</th>
                        <th>Benefícios</th>
                        <th>Descontos</th>
                        <th>Líquido</th>
                      </tr>
                    </thead>
                    <tbody>
                      {folhas.map(f => (
                        <tr key={`${f.mesReferencia}/${f.anoReferencia}`}>
                          <td>{`${String(f.mesReferencia).padStart(2,'0')}/${f.anoReferencia}`}</td>
                          <td>{formatBRL(f.salarioBruto)}</td>
                          <td>{formatBRL(f.totalAdicionais)}</td>
                          <td>{formatBRL(f.totalBeneficios)}</td>
                          <td>{formatBRL(f.totalDescontos)}</td>
                          <td className="highlight-value">{formatBRL(f.salarioLiquido)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}
