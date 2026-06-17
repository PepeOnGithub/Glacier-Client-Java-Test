/* Glacier mod menu logic — renders from data/modules.json, talks to Java via bridge.js (window.glacier).
   Icon glyphs are written as HTML entities (&#xfXXX;) on purpose: the workspace linter strips raw
   Font Awesome PUA characters, so never paste them literally here. */
const CATS = [
  ["all","&#xf00a;","All"], ["hud","&#xf2bd;","HUD"], ["render","&#xf06e;","Render"],
  ["pvp","&#xf71c;","PvP"], ["performance","&#xf625;","Perf"], ["qol","&#xf013;","QoL"],
  ["advanced","&#xf085;","Advanced"], ["engine","&#xf2db;","Engine"], ["expanded","&#xf12e;","Expanded"],
];
const TABS = [
  ["modules","&#xf009;","Modules"], ["elements","&#xf1e0;","Elements"],
  ["editors","&#xf044;","Editors"], ["music","&#xf001;","Music Player"],
];

let MODULES = [];
let state = { tab:"modules", cat:"all", search:"", settingsFor:null };

const $ = (s, r=document) => r.querySelector(s);
function el(tag, cls, html){ const e=document.createElement(tag); if(cls)e.className=cls; if(html!=null)e.innerHTML=html; return e; }
function fa(ch, brand){ return `<i class="${brand?'fab':'fa'}">${ch||""}</i>`; }

function render(){
  if (state.settingsFor) return renderSettings(state.settingsFor);
  const isMods = state.tab==="modules";
  $("#subtabs").style.display = isMods ? "flex" : "none";
  $("#searchbar").style.display = isMods ? "flex" : "none";
  const grid = $("#grid"); grid.innerHTML = "";
  if (!isMods) {
    const label = TABS.find(t=>t[0]===state.tab);
    grid.innerHTML = `<div class="empty-tab">${fa((label&&label[1])||"")}<span>${label?label[2]:state.tab}</span><small>Coming soon</small></div>`;
    return;
  }
  let list = MODULES.slice();
  if (state.cat!=="all") list = list.filter(m => m.folder===state.cat);
  if (state.search) { const q=state.search.toLowerCase(); list = list.filter(m => m.name.toLowerCase().includes(q)); }
  list.forEach((m,i) => {
    const c = el("div", "card"+(m.on?" on":"")); c.style.animationDelay=(i%18*0.012)+"s";
    c.innerHTML = `<div class="gear" data-gear="1">${fa("&#xf013;")}</div>`+
                  `<div class="ic">${fa(m.icon,m.brand)}</div><div class="nm">${m.name}</div>`;
    c.onclick = (e)=>{ if(e.target.closest("[data-gear]")){ state.settingsFor=m; render(); return; }
      m.on=!m.on; c.classList.toggle("on"); glacier.send("toggle:"+m.slug); };
    grid.appendChild(c);
  });
}

function renderSettings(m){
  $("#subtabs").style.display="none"; $("#searchbar").style.display="none";
  const grid = $("#grid"); grid.innerHTML="";
  const wrap = el("div","settings");
  const head = el("div","set-head");
  head.innerHTML = `<div class="back">${fa("&#xf053;")} Back</div><div class="ic">${fa(m.icon,m.brand)}</div>`+
                   `<div class="t"><b>${m.name}</b><small>${m.folder}</small></div>`;
  head.querySelector(".back").onclick=()=>{ state.settingsFor=null; render(); };
  wrap.appendChild(head);
  // Enabled row
  wrap.appendChild(settingRow({type:"bool",name:"Enabled",on:m.on}, v=>{ m.on=v; glacier.send("toggle:"+m.slug); }));
  m.settings.forEach(s => wrap.appendChild(settingRow(s, v=>glacier.send("set:"+m.slug+":"+s.name+"="+v))));
  grid.appendChild(wrap);
}

function settingRow(s, onchange){
  const row = el("div","row");
  row.appendChild(el("div","lab",s.name));
  if (s.type==="bool"){
    const t = el("div","toggle"+((s.on||s.value)?" on":""));
    t.onclick=()=>{ t.classList.toggle("on"); onchange(t.classList.contains("on")); };
    row.appendChild(t);
  } else if (s.type==="number"){
    const min = s.min!=null?s.min:0, max = s.max!=null?s.max:100, step = s.step||1;
    let val = s.value!=null?s.value : (min+max)/2;
    const sl=el("div","slider"); const fill=el("div","fill"), knob=el("div","knob");
    sl.appendChild(fill); sl.appendChild(knob);
    const out=el("div","val","");
    const paint=()=>{ const p=((val-min)/(max-min))*100; fill.style.width=p+"%"; knob.style.left=p+"%";
      out.textContent = (step<1? val.toFixed(2) : Math.round(val)); };
    const setFromX=(clientX)=>{ const r=sl.getBoundingClientRect();
      let p=Math.max(0,Math.min(1,(clientX-r.left)/r.width)); val=min+p*(max-min);
      val=Math.round(val/step)*step; paint(); onchange(step<1?val.toFixed(2):Math.round(val)); };
    let drag=false;
    sl.addEventListener("mousedown", e=>{ drag=true; setFromX(e.clientX); e.preventDefault(); });
    window.addEventListener("mousemove", e=>{ if(drag) setFromX(e.clientX); });
    window.addEventListener("mouseup", ()=>{ drag=false; });
    paint(); row.appendChild(sl); row.appendChild(out);
  } else if (s.type==="mode"){
    const opts = s.options && s.options.length ? s.options : ["Option A","Option B","Option C"];
    let idx = Math.max(0, opts.indexOf(s.value));
    const dd=el("div","dropdown"); dd.textContent=opts[idx]+"  ▾";
    dd.onclick=()=>{ idx=(idx+1)%opts.length; dd.textContent=opts[idx]+"  ▾"; onchange(opts[idx]); };
    row.appendChild(dd);
  } else if (s.type==="color"){
    const sw=el("input","swatch"); sw.type="color"; sw.value=s.value||"#7289da";
    sw.oninput=()=>onchange(sw.value);
    row.appendChild(sw);
  } else if (s.type==="string"){
    const b=el("input","textbox"); b.value=s.value||""; b.placeholder="…";
    b.oninput=()=>onchange(b.value);
    row.appendChild(b);
  }
  return row;
}

function build(){
  const tabs=$("#tabs"); TABS.forEach(([k,ic,lbl])=>{
    const t=el("div","tab"+(state.tab===k?" active":"")); t.innerHTML=fa(ic)+" "+lbl;
    t.onclick=()=>{ state.tab=k; state.settingsFor=null; [...tabs.children].forEach(c=>c.classList.remove("active")); t.classList.add("active"); render(); };
    tabs.appendChild(t);
  });
  const sub=$("#subtabs"); CATS.forEach(([k,ic,lbl])=>{
    const c=el("div","chip"+(state.cat===k?" active":"")); c.innerHTML=fa(ic)+`<span class="lbl">${lbl}</span>`;
    c.onclick=()=>{ state.cat=k; [...sub.children].forEach(x=>x.classList.remove("active")); c.classList.add("active"); render(); };
    sub.appendChild(c);
  });
  $("#search").addEventListener("input", e=>{ state.search=e.target.value; render(); });
  render();
}

fetch("data/modules.json").then(r=>r.json()).then(d=>{ MODULES=d; build(); })
  .catch(()=>{ MODULES=[]; build(); });
