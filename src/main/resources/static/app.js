const $=(s,e=document)=>e.querySelector(s);
const esc=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const enc=encodeURIComponent;
const D=s=>s?new Date(s+'T00:00:00').toLocaleDateString('pt-BR'):'—';
const DT=s=>s?new Date(s).toLocaleString('pt-BR',{dateStyle:'short',timeStyle:'short'}):'—';
const lbl=s=>{s=String(s).replace(/_/g,' ').toLowerCase();return s[0].toUpperCase()+s.slice(1)};
const badge=s=>s?`<span class="badge st-${esc(s)}">${esc(lbl(s))}</span>`:'—';
const items=d=>d?._embedded?Object.values(d._embedded).find(Array.isArray)||[]:Array.isArray(d)?d:d?.content||[];
const pageOf=d=>d?.page||{number:0,totalPages:1,totalElements:items(d).length};
const ST=['PLANEJADO','CONFIRMADO','EM_ANDAMENTO','CONCLUIDO','CANCELADO'];

async function api(path,opt={}){
  const r=await fetch(path,{...opt,headers:{'Content-Type':'application/json',Accept:'application/hal+json, application/json'}});
  if(!r.ok){
    let m=`Erro ${r.status}`;const t=await r.text();
    try{const j=JSON.parse(t);m=j.detail||j.message||(typeof j==='object'?Object.entries(j).filter(([k])=>!['timestamp','status','path','trace'].includes(k)).map(([k,v])=>typeof v==='object'?JSON.stringify(v):(isNaN(k)?`${k}: ${v}`:v)).join('; '):m)||m}catch{if(t&&t.length<300)m=t}
    throw Object.assign(new Error(m),{status:r.status});
  }
  return r.status===204?null:r.json();
}
function toast(m,bad){const t=$('#toast');t.textContent=m;t.className='show'+(bad?' bad':'');clearTimeout(t.t);t.t=setTimeout(()=>t.className='',3500)}

const R={
 eventos:{name:'Eventos',one:'evento',
  cols:[['Título',e=>esc(e.titulo)],['Data',e=>D(e.data)],['Status',e=>badge(e.status)],['Local',e=>esc(e.local?.nome||'—')],['Palestrantes',e=>(e.palestrantes||[]).map(p=>`<span class="chip">${esc(p.nome)}</span>`).join('')||'—']],
  fields:[{k:'titulo',l:'Título',req:1},{k:'data',l:'Data',t:'date',req:1},{k:'status',l:'Status',t:'select',o:ST,req:1},{k:'local',l:'Local',t:'ref',src:'locais',show:'nome'},{k:'palestrantes',l:'Palestrantes',t:'multi',src:'palestrantes',show:'nome'}],
  filters:[{l:'Título',t:'text',f:a=>`/eventos/buscar?titulo=${enc(a)}`},{l:'Status',t:'select',o:ST,f:a=>`/eventos/status/${a}`},{l:'Cidade do local',t:'text',f:a=>`/eventos/cidade/${enc(a)}`},{l:'Período',t:'range',f:(a,b)=>`/eventos/periodo?inicio=${a}&fim=${b}`}]},
 locais:{name:'Locais',one:'local',
  cols:[['Nome',e=>esc(e.nome)],['Endereço',e=>esc(e.endereco)],['Cidade',e=>esc(e.cidade)]],
  fields:[{k:'nome',l:'Nome',req:1},{k:'endereco',l:'Endereço',req:1},{k:'cidade',l:'Cidade',req:1}],
  filters:[{l:'Cidade',t:'text',f:a=>`/locais/buscar?cidade=${enc(a)}`},{l:'Nome',t:'text',f:a=>`/locais/nome?nome=${enc(a)}`},{l:'Endereço',t:'text',f:a=>`/locais/endereco?endereco=${enc(a)}`}]},
 palestrantes:{name:'Palestrantes',one:'palestrante',
  cols:[['Nome',e=>esc(e.nome)],['E-mail',e=>esc(e.email)],['Especialidade',e=>esc(e.especialidade||'—')]],
  fields:[{k:'nome',l:'Nome',req:1},{k:'email',l:'E-mail',t:'email',req:1},{k:'especialidade',l:'Especialidade'}],
  filters:[{l:'Nome',t:'text',f:a=>`/palestrantes/nome?nome=${enc(a)}`},{l:'Especialidade',t:'text',f:a=>`/palestrantes/especialidade?especialidade=${enc(a)}`}]},
 participantes:{name:'Participantes',one:'participante',
  cols:[['Nome',e=>esc(e.nome)],['E-mail',e=>esc(e.email)],['Telefone',e=>esc(e.telefone||'—')]],
  fields:[{k:'nome',l:'Nome',req:1},{k:'email',l:'E-mail',t:'email',req:1},{k:'telefone',l:'Telefone',t:'tel'}],
  filters:[{l:'Nome',t:'text',f:a=>`/participantes/buscar?nome=${enc(a)}`}]},
 inscricoes:{name:'Inscrições',one:'inscrição',
  cols:[['Evento',e=>esc(e.evento?.titulo||'—')],['Participante',e=>esc(e.participante?.nome||'—')],['Inscrito em',e=>DT(e.dataInscricao)],['Status',e=>badge(e.status)]],
  fields:[{k:'evento',l:'Evento',t:'ref',src:'eventos',show:'titulo',req:1},{k:'participante',l:'Participante',t:'ref',src:'participantes',show:'nome',req:1},{k:'dataInscricao',l:'Data da inscrição',t:'datetime-local',req:1},{k:'status',l:'Status',t:'combo',o:['ATIVA','PENDENTE','CANCELADA'],req:1}],
  filters:[{l:'Status',t:'text',f:a=>`/inscricoes/status?status=${enc(a.toUpperCase())}`},{l:'Evento',t:'ref',src:'eventos',show:'titulo',f:a=>`/inscricoes/evento/${a}`},{l:'Participante',t:'ref',src:'participantes',show:'nome',f:a=>`/inscricoes/participante/${a}`},{l:'Período',t:'range',dt:1,f:(a,b)=>`/inscricoes/periodo?inicio=${a}&fim=${b}`}]}
};

const S={res:null,page:0,base:null,flt:null,rows:[]};
const opts=async(src,show)=>items(await api(`/${src}?size=200`)).map(x=>[x.id,x[show]]);

function nav(){
  const h=location.hash.slice(2)||'';
  $('#nav').innerHTML=`<a href="#/" class="${h?'':'on'}">Início</a>`+Object.entries(R).map(([k,r])=>`<a href="#/${k}" class="${h===k?'on':''}">${r.name}</a>`).join('');
  if(!h)return dash();
  if(!R[h])return location.hash='#/';
  Object.assign(S,{res:h,page:0,base:`/${h}`,flt:null});list();
}
addEventListener('hashchange',nav);

async function dash(){
  $('#main').innerHTML='<h1>Início</h1><p class="sub">Carregando…</p>';
  try{
    const [tot,ev]=await Promise.all([Promise.all(Object.keys(R).map(async k=>pageOf(await api(`/${k}?size=1`)).totalElements??0)),api('/eventos?size=100')]);
    const es=items(ev),hoje=new Date().toISOString().slice(0,10);
    const next=es.filter(e=>e.data>=hoje&&!['CANCELADO','CONCLUIDO'].includes(e.status)).sort((a,b)=>a.data.localeCompare(b.data))[0];
    const cnt=ST.map(s=>[s,es.filter(e=>e.status===s).length]),max=Math.max(1,...cnt.map(c=>c[1]));
    $('#main').innerHTML=`<h1>Início</h1><p class="sub">Visão geral dos seus eventos.</p>
    <div class="stats">${Object.entries(R).map(([k,r],i)=>`<a class="stat" href="#/${k}"><b>${tot[i]}</b><span>${r.name}</span></a>`).join('')}</div>
    <div class="grid">${next?`<div class="ticket"><small>Próximo evento</small><h2>${esc(next.titulo)}</h2><p><span>${D(next.data)}</span><span>${esc(next.local?.nome||'Local a definir')}</span></p></div>`:`<div class="panel empty">Nenhum evento futuro. <a href="#/eventos">Cadastre um evento</a>.</div>`}
    <div class="panel"><h2>Eventos por status</h2>${cnt.map(([s,n])=>`<div class="row"><span>${lbl(s)}</span><div class="track"><i style="width:${n/max*100}%"></i></div><b>${n}</b></div>`).join('')}</div></div>`;
  }catch(e){$('#main').innerHTML=`<h1>Início</h1><div class="err">Não foi possível falar com a API: ${esc(e.message)}</div>`}
}

async function fetchPage(){
  const q=u=>`${u}${u.includes('?')?'&':'?'}page=${S.page}&size=10`;
  try{return await api(q(S.base))}
  catch(e){ // períodos podem aceitar só data: tenta de novo sem a hora
    if(S.flt?.dt&&e.status===400){const f=S.flt;S.base=f.f(f.a.slice(0,10),f.b.slice(0,10));return api(q(S.base))}
    throw e}
}
async function list(){
  const r=R[S.res];
  $('#main').innerHTML=`<div class="head"><div><h1>${r.name}</h1><p class="sub">Cadastre, edite e consulte ${r.name.toLowerCase()}.</p></div><button class="pri" id="new">Novo ${r.one}</button></div>
  <form class="bar" id="fb"><select id="fs" aria-label="Filtrar por">${r.filters.map((f,i)=>`<option value="${i}">${f.l}</option>`).join('')}</select><span class="bar" id="fi" style="margin:0"></span><button class="pri">Buscar</button><button type="button" id="fc">Limpar</button></form>
  <div class="card" id="tb"><p class="empty">Carregando…</p></div><div class="pager" id="pg"></div>`;
  $('#new').onclick=()=>form();
  fInputs();$('#fs').onchange=fInputs;
  $('#fb').onsubmit=e=>{e.preventDefault();const f=r.filters[$('#fs').value],v=[...$('#fi').querySelectorAll('input,select')].map(i=>i.value.trim());
    if(!v[0]||(f.t==='range'&&!v[1]))return toast('Preencha o filtro para buscar.',1);
    S.flt={...f,a:v[0],b:v[1]};S.base=f.f(v[0],v[1]);S.page=0;load()};
  $('#fc').onclick=()=>{S.base=`/${S.res}`;S.flt=null;S.page=0;$('#fb').reset();fInputs();load()};
  load();
}
async function fInputs(){
  const f=R[S.res].filters[$('#fs').value||0],w=$('#fi');
  if(f.t==='text')w.innerHTML='<input placeholder="Digite para buscar" aria-label="Valor">';
  else if(f.t==='select')w.innerHTML=`<select>${f.o.map(o=>`<option value="${o}">${lbl(o)}</option>`).join('')}</select>`;
  else if(f.t==='range'){const t=f.dt?'datetime-local':'date';w.innerHTML=`<input type="${t}" aria-label="De"> até <input type="${t}" aria-label="Até">`}
  else if(f.t==='ref'){w.innerHTML='<select><option value="">Carregando…</option></select>';
    try{w.innerHTML=`<select><option value="">Escolha…</option>${(await opts(f.src,f.show)).map(([i,n])=>`<option value="${i}">${esc(n)}</option>`).join('')}</select>`}catch{}}
}
async function load(){
  const r=R[S.res],tb=$('#tb');
  try{
    const d=await fetchPage();S.rows=items(d);const p=pageOf(d);
    tb.innerHTML=S.rows.length?`<table><thead><tr>${r.cols.map(c=>`<th>${c[0]}</th>`).join('')}<th></th></tr></thead><tbody>${S.rows.map((x,i)=>`<tr>${r.cols.map(c=>`<td>${c[1](x)}</td>`).join('')}<td class="act"><button data-e="${i}">Editar</button><button class="del" data-d="${i}">Excluir</button></td></tr>`).join('')}</tbody></table>`
      :`<p class="empty">${S.flt?'Nada encontrado para esse filtro.':`Nenhum registro ainda. Clique em “Novo ${r.one}” para começar.`}</p>`;
    tb.querySelectorAll('[data-e]').forEach(b=>b.onclick=()=>form(S.rows[b.dataset.e]));
    tb.querySelectorAll('[data-d]').forEach(b=>b.onclick=()=>remove(S.rows[b.dataset.d]));
    const n=p.number??0,t=p.totalPages??1;
    $('#pg').innerHTML=`<span>${p.totalElements??S.rows.length} registro(s) · página ${n+1} de ${Math.max(t,1)}</span><div><button ${n<=0?'disabled':''} id="pv">Anterior</button><button ${n+1>=t?'disabled':''} id="nx">Próxima</button></div>`;
    $('#pv').onclick=()=>{S.page--;load()};$('#nx').onclick=()=>{S.page++;load()};
  }catch(e){tb.innerHTML=`<div class="err">${esc(e.message)}</div>`;$('#pg').innerHTML=''}
}

async function form(row){
  const r=R[S.res],dlg=$('#dlg'),ed=!!row,cache={};
  dlg.innerHTML='<form><p>Carregando…</p></form>';dlg.showModal();
  try{await Promise.all(r.fields.filter(f=>f.src).map(async f=>cache[f.k]=await opts(f.src,f.show)))}catch(e){dlg.close();return toast('Não foi possível carregar as listas: '+e.message,1)}
  const val=f=>{const v=row?.[f.k];return v==null?'':f.t==='datetime-local'?String(v).slice(0,16):v};
  const inp=f=>{
    const a=`name="${f.k}" ${f.req?'required':''}`;
    if(f.t==='select')return `<select ${a}>${f.o.map(o=>`<option value="${o}" ${val(f)===o?'selected':''}>${lbl(o)}</option>`).join('')}</select>`;
    if(f.t==='ref')return `<select ${a}><option value="">${f.req?'Escolha…':'Nenhum'}</option>${cache[f.k].map(([i,n])=>`<option value="${i}" ${row?.[f.k]?.id===i?'selected':''}>${esc(n)}</option>`).join('')}</select>`;
    if(f.t==='multi'){const ids=(row?.[f.k]||[]).map(x=>x.id);return `<div class="multi">${cache[f.k].map(([i,n])=>`<label><input type="checkbox" name="${f.k}" value="${i}" ${ids.includes(i)?'checked':''}>${esc(n)}</label>`).join('')||'<span>Nenhum cadastrado ainda.</span>'}</div>`}
    if(f.t==='combo')return `<input ${a} list="dl-${f.k}" value="${esc(val(f))}"><datalist id="dl-${f.k}">${f.o.map(o=>`<option value="${o}">`).join('')}</datalist>`;
    return `<input ${a} type="${f.t||'text'}" value="${esc(val(f))}">`;
  };
  dlg.innerHTML=`<form><h2>${ed?'Editar':'Novo'} ${r.one}</h2><div id="fe"></div>${r.fields.map(f=>`<label>${f.l}${f.t==='multi'?'<span></span>':''}${inp(f)}</label>`).join('')}<div class="foot"><button type="button" id="cx">Cancelar</button><button class="pri">Salvar</button></div></form>`;
  $('#cx').onclick=()=>dlg.close();
  $('form',dlg).onsubmit=async e=>{
    e.preventDefault();const fd=new FormData(e.target),body={};
    r.fields.forEach(f=>{
      if(f.t==='multi')body[f.k]=fd.getAll(f.k).map(i=>({id:+i}));
      else if(f.t==='ref'){const v=fd.get(f.k);body[f.k]=v?{id:+v}:null}
      else{let v=(fd.get(f.k)||'').trim();if(f.t==='datetime-local'&&v.length===16)v+=':00';body[f.k]=v===''?null:v}
    });
    if(ed)body.id=row.id;
    try{await api(ed?`/${S.res}/${row.id}`:`/${S.res}`,{method:ed?'PUT':'POST',body:JSON.stringify(body)});dlg.close();toast(ed?'Alterações salvas.':'Cadastro criado.');load()}
    catch(er){$('#fe').innerHTML=`<div class="err">${esc(er.message)}</div>`}
  };
}
function remove(row){
  const dlg=$('#dlg'),r=R[S.res];
  dlg.innerHTML=`<form><h2>Excluir ${r.one}?</h2><p>Esta ação não pode ser desfeita.</p><div id="fe"></div><div class="foot"><button type="button" id="cx">Cancelar</button><button class="pri" style="background:var(--er);border-color:var(--er)">Excluir</button></div></form>`;
  dlg.showModal();$('#cx').onclick=()=>dlg.close();
  $('form',dlg).onsubmit=async e=>{e.preventDefault();
    try{await api(`/${S.res}/${row.id}`,{method:'DELETE'});dlg.close();toast('Registro excluído.');if(S.rows.length===1&&S.page>0)S.page--;load()}
    catch(er){$('#fe').innerHTML=`<div class="err">${esc(er.message)}</div>`}};
}
nav();