import React, { useEffect, useMemo, useState } from 'react'
import { api } from '../services/api'
import './Dashboard.css'
import { Link } from 'react-router-dom'

export default function AdminPage(){
  const [usuarios, setUsuarios] = useState([])
  const [filtro, setFiltro] = useState('todos')
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ login:'', senha:'', permissao:0 })
  const [creating, setCreating] = useState(false)
  const [saving, setSaving] = useState(false)
  const [novo, setNovo] = useState({
    login:'', senha:'', permissao:0,
    nome:'', cpf:'', cargo:'', dependentes:'',
    salarioBase:'', aptoPericulosidade:false, grauInsalubridade:'',
    valeTransporte:false, valorVT:'', valeAlimentacao:false, valorVA:''
  })
  const [temInsalubridade, setTemInsalubridade] = useState(false)

  const user = useMemo(()=>{ try{ return JSON.parse(localStorage.getItem('user')||'null') }catch{ return null } },[])
  const saudacao = useMemo(()=>{ const h=new Date().getHours(); return h<12?'Bom dia':(h<18?'Boa tarde':'Boa noite') },[])
  const nomeExibicao = user?.login || 'Admin'

  useEffect(() => {
    if (creating) return;
    async function load(){
      const url = filtro === 'todos' ? '/api/usuarios' : `/api/usuarios/permissao/${filtro === 'funcionario' ? 0 : 1}`;
      const res = await api.get(url);
      const lista = Array.isArray(res.data) ? res.data : []
      setUsuarios(lista.filter(u => Number(u?.permissao) !== 2))
    }
    load()
  }, [filtro, creating])

  function startEdit(u){ setEditing(u.id); setForm({ login: u.login, senha:'', permissao: u.permissao }) }

  async function save(){
    const payload = {}
    if(form.login) payload.login = form.login
    if(form.senha) payload.senha = form.senha
    if(form.permissao !== undefined) payload.permissao = Number(form.permissao)
    await api.patch(`/api/usuarios/${editing}`, payload)
    setEditing(null)
    const res = await api.get('/api/usuarios')
    const lista = Array.isArray(res.data) ? res.data : []
    setUsuarios(lista.filter(u => Number(u?.permissao) !== 2))
  }

  async function remove(id){
    if(!confirm('Remover usuário?')) return
    await api.delete(`/api/usuarios/${id}`)
    setUsuarios(usuarios.filter(u=>u.id!==id))
  }

  function maskCPF(v){
    const digits = (v||'').replace(/\D/g,'').slice(0,11)
    const p1 = digits.slice(0,3); const p2 = digits.slice(3,6); const p3 = digits.slice(6,9); const p4 = digits.slice(9,11)
    let out = p1; if(p2) out += '.'+p2; if(p3) out += '.'+p3; if(p4) out += '-'+p4; return out
  }

  function formatCurrencyBRL(v){
    const onlyDigits = String(v||'').replace(/\D/g,'')
    const asNumber = (onlyDigits === '' ? 0 : parseInt(onlyDigits,10))
    const cents = (asNumber/100).toFixed(2).split('.')
    const intPart = cents[0]
    const decPart = cents[1]
    const withDots = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.')
    return `R$ ${withDots},${decPart}`
  }
  function parseCurrencyBRLToNumber(v){
    const onlyDigits = String(v||'').replace(/\D/g,'')
    return onlyDigits ? Number(onlyDigits)/100 : 0
  }

  function toReadableError(e){
    if(e?.response?.data){
      const d = e.response.data
      if(typeof d === 'string') return d
      if(typeof d?.message === 'string') return d.message
      try { return JSON.stringify(d) } catch { return 'Erro desconhecido' }
    }
    return 'Falha ao criar usuário.'
  }

  async function criar(){
    try{
      setSaving(true)
      const payload = {
        login: novo.login,
        senha: novo.senha,
        permissao: Number(novo.permissao),
        nome: novo.nome,
        cpf: (novo.cpf||'').replace(/\D/g,''),
        cargo: novo.cargo,
        dependentes: Number(novo.dependentes||0),
        salarioBase: typeof novo.salarioBase === 'string' ? parseCurrencyBRLToNumber(novo.salarioBase) : Number(novo.salarioBase||0),
        aptoPericulosidade: !!novo.aptoPericulosidade,
        grauInsalubridade: temInsalubridade ? Number(novo.grauInsalubridade||0) : 0,
        valeTransporte: !!novo.valeTransporte,
        valorVT: !!novo.valeTransporte ? (typeof novo.valorVT === 'string' ? parseCurrencyBRLToNumber(novo.valorVT) : Number(novo.valorVT||0)) : 0,
        valeAlimentacao: !!novo.valeAlimentacao,
        valorVA: !!novo.valeAlimentacao ? (typeof novo.valorVA === 'string' ? parseCurrencyBRLToNumber(novo.valorVA) : Number(novo.valorVA||0)) : 0,
      }
      await api.post('/api/funcionarios', payload)
      setCreating(false)
      setNovo({ login:'', senha:'', permissao:0, nome:'', cpf:'', cargo:'', dependentes:'', salarioBase:'', aptoPericulosidade:false, grauInsalubridade:'', valeTransporte:false, valorVT:'', valeAlimentacao:false, valorVA:'' })
      setTemInsalubridade(false)
      const res = await api.get('/api/usuarios')
      const lista = Array.isArray(res.data) ? res.data : []
      setUsuarios(lista.filter(u => Number(u?.permissao) !== 2))
    }catch(e){
      alert(toReadableError(e))
    }finally{
      setSaving(false)
    }
  }

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <div className="brand">PayPaper</div>
        <nav className="menu">
          <button className="btn" style={{width:'100%'}} onClick={()=>setCreating(v=>!v)}>
            {creating ? 'Fechar criação' : 'Novo Usuário'}
          </button>
        </nav>
        <div className="sidebar-footer">
          <Link className="logout" to="/logout">Sair</Link>
        </div>
      </aside>

      <main className="content">
        <div className="content-wrap">
          <div className="welcome-banner">
            <h2 className="welcome-title">{saudacao}, {nomeExibicao}!</h2>
            <div className="welcome-subtitle">Bem-vindo ao PayPaper. Você está acessando como administrador.</div>
          </div>

          <div className="content-header">
            <h1>Administração de Usuários</h1>
          </div>

          {creating && (
            <div className="card">
              <div className="card-title">Criar novo usuário</div>
              <div className="card-body">
                <div className="form-grid">
                  <label>Login<input placeholder="ex.: joao.silva" value={novo.login} onChange={e=>setNovo({...novo, login:e.target.value})} /></label>
                  <label>Senha<input type="password" placeholder="••••••••" value={novo.senha} onChange={e=>setNovo({...novo, senha:e.target.value})} /></label>
                  <label>Permissão<select value={novo.permissao} onChange={e=>setNovo({...novo, permissao:e.target.value})}>
                    <option value={0}>Funcionário</option>
                    <option value={1}>Gestor</option>
                  </select></label>
                  <label>Nome<input placeholder="ex.: João da Silva" value={novo.nome} onChange={e=>setNovo({...novo, nome:e.target.value})} /></label>
                  <label>CPF<input placeholder="000.000.000-00" value={novo.cpf} onChange={e=>setNovo({...novo, cpf: maskCPF(e.target.value)})} /></label>
                  <label>Cargo<input placeholder="ex.: Analista Jr" value={novo.cargo} onChange={e=>setNovo({...novo, cargo:e.target.value})} /></label>
                  <label>Dependentes<input type="number" placeholder="0" value={novo.dependentes} onChange={e=>setNovo({...novo, dependentes:e.target.value})} /></label>
                  <label>Salário Base<input type="text" placeholder="R$ 0,00" value={novo.salarioBase} onChange={e=>setNovo({...novo, salarioBase: formatCurrencyBRL(e.target.value)})} /></label>
                  <label>Periculosidade?<select value={novo.aptoPericulosidade?1:0} onChange={e=>setNovo({...novo, aptoPericulosidade:e.target.value==='1'})}>
                    <option value={0}>Não</option>
                    <option value={1}>Sim</option>
                  </select></label>
                </div>
                <div className="form-grid" style={{marginTop:12}}>
                  <label>Insalubridade?<select value={temInsalubridade?1:0} onChange={e=>{ const on = e.target.value==='1'; setTemInsalubridade(on); if(!on) setNovo({...novo, grauInsalubridade:''}) }}>
                    <option value={0}>Não</option>
                    <option value={1}>Sim</option>
                  </select></label>
                  {temInsalubridade ? (
                    <label>Grau Insalubridade<input type="number" placeholder="0" value={novo.grauInsalubridade} onChange={e=>setNovo({...novo, grauInsalubridade:e.target.value})} /></label>
                  ) : (<div />)}

                  <label>Vale Transporte?<select value={novo.valeTransporte?1:0} onChange={e=>{ const on = e.target.value==='1'; setNovo(prev=>({ ...prev, valeTransporte:on, valorVT: on ? prev.valorVT : '' })) }}>
                    <option value={0}>Não</option>
                    <option value={1}>Sim</option>
                  </select></label>
                  {novo.valeTransporte ? (
                    <label>Valor VT/dia<input type="text" placeholder="R$ 0,00" value={novo.valorVT} onChange={e=>setNovo({...novo, valorVT: formatCurrencyBRL(e.target.value)})} /></label>
                  ) : (<div />)}

                  <label>Vale Alimentação?<select value={novo.valeAlimentacao?1:0} onChange={e=>{ const on = e.target.value==='1'; setNovo(prev=>({ ...prev, valeAlimentacao:on, valorVA: on ? prev.valorVA : '' })) }}>
                    <option value={0}>Não</option>
                    <option value={1}>Sim</option>
                  </select></label>
                  {novo.valeAlimentacao ? (
                    <label>Valor VA/dia<input type="text" placeholder="R$ 0,00" value={novo.valorVA} onChange={e=>setNovo({...novo, valorVA: formatCurrencyBRL(e.target.value)})} /></label>
                  ) : (<div />)}
                </div>

                <div className="actions-row">
                  <button className="btn primary" onClick={criar} disabled={saving}>{saving ? 'Criando...' : 'Criar'}</button>
                  <button className="btn" onClick={()=>setCreating(false)} disabled={saving}>Cancelar</button>
                </div>
              </div>
            </div>
          )}

          {!creating && (
            <>
              <div className="filters-row">
                <button className={`btn ${filtro==='todos'?'primary':''}`} onClick={()=>setFiltro('todos')}>Todos</button>
                <button className={`btn ${filtro==='funcionario'?'primary':''}`} onClick={()=>setFiltro('funcionario')}>Funcionários</button>
                <button className={`btn ${filtro==='gestor'?'primary':''}`} onClick={()=>setFiltro('gestor')}>Gestores</button>
              </div>

              <div className="card">
                <div className="card-body">
                  <table style={{width:'100%', borderCollapse:'collapse'}}>
                    <thead>
                      <tr>
                        <th style={{textAlign:'left', padding:'8px 4px'}}>ID</th>
                        <th style={{textAlign:'left', padding:'8px 4px'}}>Login</th>
                        <th style={{textAlign:'left', padding:'8px 4px'}}>Permissão</th>
                        <th style={{textAlign:'left', padding:'8px 4px'}}>Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      {usuarios.map(u => (
                        <tr key={u.id}>
                          <td style={{padding:'6px 4px'}}>{u.id}</td>
                          <td style={{padding:'6px 4px'}}>{editing===u.id ? (
                            <input value={form.login} onChange={e=>setForm({...form, login:e.target.value})} />
                          ) : u.login}</td>
                          <td style={{padding:'6px 4px'}}>{editing===u.id ? (
                            <select value={form.permissao} onChange={e=>setForm({...form, permissao:e.target.value})}>
                              <option value={0}>Funcionário</option>
                              <option value={1}>Gestor</option>
                            </select>
                          ) : u.permissao}</td>
                          <td style={{padding:'6px 4px', display:'flex', gap:8}}>
                            {editing===u.id ? (
                              <>
                                <button className="btn primary" onClick={save}>Salvar</button>
                                <button className="btn" onClick={()=>setEditing(null)}>Cancelar</button>
                              </>
                            ) : (
                              <>
                                <button className="btn" onClick={()=>startEdit(u)}>Editar</button>
                                <button className="btn" onClick={()=>remove(u.id)}>Excluir</button>
                              </>
                            )}
                          </td>
                        </tr>
                      ))}
                      {!usuarios.length && (
                        <tr>
                          <td colSpan={4} style={{padding:'10px'}}>Nenhum usuário encontrado</td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </>
          )}
        </div>
      </main>
    </div>
  )
}
