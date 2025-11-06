import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import './Dashboard.css';
import { Sidebar } from './Dashboard';
import { useNavigate } from 'react-router-dom';

function formatBRL(v){
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v||0));
}

export default function SalaryHistory(){
  const navigate = useNavigate();
  const login = localStorage.getItem('login');
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

  return (
    <div className="dashboard-layout">
      <Sidebar user={funcionario} />
      <main className="content">
        <div className="content-wrap">
          <header className="content-header">
            <h1>Histórico Salarial</h1>
            <div className="header-actions">
              {login && (
                <a
                  className="btn primary"
                  href={`data:text/csv;charset=utf-8,${encodeURIComponent(['Mes/Ano','Bruto','Adicionais','Beneficios','Descontos','Liquido'].join(';')+'\n'+folhas.map(f=>[
                    `${String(f.mesReferencia).padStart(2,'0')}/${f.anoReferencia}`,
                    f.salarioBruto,
                    f.totalAdicionais,
                    f.totalBeneficios,
                    f.totalDescontos,
                    f.salarioLiquido
                  ].join(';')).join('\n'))}`}
                  download={`historico_${login}.csv`}
                >Baixar Relatório</a>
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
              <div className="card-title">Gráfico</div>
              <div className="card-body">
                <div className="muted">Falta fazer</div>
              </div>
            </section>

            <section className="card">
              <div className="card-title">Tabela</div>
              <div className="card-body">
                {folhas.length ? (
                  <div style={{overflowX:'auto'}}>
                    <table style={{width:'100%', borderCollapse:'collapse'}}>
                      <thead>
                        <tr>
                          <th style={{textAlign:'left', padding:'8px'}}>Referência</th>
                          <th style={{textAlign:'right', padding:'8px'}}>Bruto</th>
                          <th style={{textAlign:'right', padding:'8px'}}>Adicionais</th>
                          <th style={{textAlign:'right', padding:'8px'}}>Benefícios</th>
                          <th style={{textAlign:'right', padding:'8px'}}>Descontos</th>
                          <th style={{textAlign:'right', padding:'8px'}}>Líquido</th>
                        </tr>
                      </thead>
                      <tbody>
                        {folhas.map((f,i)=> (
                          <tr key={i} style={{borderTop:'1px solid var(--border)'}}>
                            <td style={{padding:'8px'}}>{String(f.mesReferencia).padStart(2,'0')}/{f.anoReferencia}</td>
                            <td style={{padding:'8px', textAlign:'right'}}>{formatBRL(f.salarioBruto)}</td>
                            <td style={{padding:'8px', textAlign:'right'}}>{formatBRL(f.totalAdicionais)}</td>
                            <td style={{padding:'8px', textAlign:'right'}}>{formatBRL(f.totalBeneficios)}</td>
                            <td style={{padding:'8px', textAlign:'right'}}>{formatBRL(f.totalDescontos)}</td>
                            <td style={{padding:'8px', textAlign:'right'}}>{formatBRL(f.salarioLiquido)}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="muted">Nenhuma folha encontrada</div>
                )}
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}
