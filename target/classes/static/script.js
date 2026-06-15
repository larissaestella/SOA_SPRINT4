const API_URL = 'http://localhost:8080/api/v1';

window.onload = () => {
    testarConexao();
    loadUsuarios();
    loadMissoes();
};

function setTab(tab) {
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    document.getElementById('panel-' + tab).classList.add('active');

    const titles = {
        usuarios: ['Gestão de Usuários', 'Consumo dos endpoints GET, POST, PUT e DELETE de Usuários.'],
        missoes: ['Gestão de Missões', 'Consumo do CRUD completo de Missões Preventivas.'],
        avatar: ['Meu Avatar', 'Integração de regras de negócio: Consulta de Status e Conclusão de Missão.'],
        ranking: ['Ranking e Estatísticas', 'Consumo de endpoints complexos de leitura e totalização.']
    };

    document.getElementById('topbar-title').textContent = titles[tab][0];
    document.getElementById('topbar-sub').textContent = titles[tab][1];

    document.querySelectorAll('.nav-item').forEach(n => {
        if (n.getAttribute('onclick') === `setTab('${tab}')`) n.classList.add('active');
    });

    if(tab === 'avatar') popularSelectsApp();
    if(tab === 'ranking') { loadRanking(); popularSelectStats(); }
}

async function testarConexao() {
    try {
        await fetch(`${API_URL}/usuarios`);
        document.getElementById('api-status').innerHTML = '✅ API Online';
        document.getElementById('api-status').style.borderColor = 'rgba(6,214,160,.5)';
        document.getElementById('api-status').style.color = 'var(--accent2)';
    } catch(e) {
        document.getElementById('api-status').innerHTML = '❌ API Offline';
        document.getElementById('api-status').style.borderColor = 'rgba(244,63,94,.5)';
        document.getElementById('api-status').style.color = 'var(--accent4)';
        showToast('Erro de Conexão: Verifique se o Spring Boot está rodando.', true);
    }
}

function showToast(msg, isError = false) {
    const toast = document.getElementById('toast');
    toast.textContent = msg;
    toast.className = isError ? 'error show' : 'show';
    setTimeout(() => toast.className = '', 3000);
}

// ================= CRUD DE USUÁRIOS =================
let usuariosList = [];

async function loadUsuarios() {
    try {
        const res = await fetch(`${API_URL}/usuarios`);
        usuariosList = await res.json();
        const tbody = document.getElementById('tb-usuarios');
        tbody.innerHTML = usuariosList.map(u => `
            <tr>
                <td>${u.id}</td><td>${u.nome}</td><td>${u.email}</td><td>${u.dataNascimento}</td>
                <td>
                    <button class="btn btn-outline btn-sm" onclick="editarUsuario(${u.id})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="deletarUsuario(${u.id})">Excluir</button>
                </td>
            </tr>
        `).join('');
    } catch(e) {}
}

function abrirFormUsuario() {
    document.getElementById('form-usuario-card').classList.remove('hidden');
    document.getElementById('u-id').value = '';
    ['nome','email','data','avatar'].forEach(id => document.getElementById(`u-${id}`).value = '');
    document.getElementById('u-avatar').disabled = false;
}

function fecharFormUsuario() { document.getElementById('form-usuario-card').classList.add('hidden'); }

function editarUsuario(id) {
    const u = usuariosList.find(x => x.id === id);
    if(u) {
        abrirFormUsuario();
        document.getElementById('u-id').value = u.id;
        document.getElementById('u-nome').value = u.nome;
        document.getElementById('u-email').value = u.email;
        document.getElementById('u-data').value = u.dataNascimento;
        document.getElementById('u-avatar').value = 'Ignorado no PUT';
        document.getElementById('u-avatar').disabled = true;
    }
}

async function salvarUsuario() {
    const id = document.getElementById('u-id').value;
    const method = id ? 'PUT' : 'POST';
    const endpoint = id ? `${API_URL}/usuarios/${id}` : `${API_URL}/usuarios`;

    const body = {
        nome: document.getElementById('u-nome').value,
        email: document.getElementById('u-email').value,
        dataNascimento: document.getElementById('u-data').value
    };
    if (!id) body.nomeAvatar = document.getElementById('u-avatar').value;

    try {
        const res = await fetch(endpoint, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if(!res.ok) throw await res.json();
        showToast(id ? 'Usuário atualizado!' : 'Usuário cadastrado!');
        fecharFormUsuario();
        loadUsuarios();
    } catch(err) { showToast(err.mensagem || 'Erro ao salvar usuário', true); }
}

async function deletarUsuario(id) {
    if(confirm('Tem certeza que deseja excluir o usuário e todos os seus dados?')) {
        try {
            await fetch(`${API_URL}/usuarios/${id}`, { method: 'DELETE' });
            showToast('Usuário deletado.');
            loadUsuarios();
        } catch(e) {}
    }
}

// ================= CRUD DE MISSÕES (5 Bônus) =================
let missoesList = [];

async function loadMissoes() {
    try {
        const res = await fetch(`${API_URL}/missoes`);
        missoesList = await res.json();
        const tbody = document.getElementById('tb-missoes');
        tbody.innerHTML = missoesList.map(m => `
            <tr>
                <td>${m.id}</td><td>${m.titulo}</td><td>${m.categoria}</td><td>${m.pontosRecompensa}</td>
                <td>
                    <button class="btn btn-outline btn-sm" onclick="editarMissao(${m.id})">Editar</button>
                    ${m.ativa ? `<button class="btn btn-danger btn-sm" onclick="inativarMissao(${m.id})">Inativar</button>` : ''}
                </td>
            </tr>
        `).join('');
    } catch(e) {}
}

function abrirFormMissao() {
    document.getElementById('form-missao-card').classList.remove('hidden');
    document.getElementById('m-id').value = '';
    ['titulo','desc','cat','pts','saude','hidra','sono','exerc','bem'].forEach(id => {
        document.getElementById(`m-${id}`).value = (id==='cat')?'SAUDE':(id==='titulo'||id==='desc')?'':0;
    });
}

function fecharFormMissao() { document.getElementById('form-missao-card').classList.add('hidden'); }

function editarMissao(id) {
    const m = missoesList.find(x => x.id === id);
    if(m) {
        abrirFormMissao();
        document.getElementById('m-id').value = m.id;
        document.getElementById('m-titulo').value = m.titulo;
        document.getElementById('m-cat').value = m.categoria;
        document.getElementById('m-desc').value = m.descricao;
        document.getElementById('m-pts').value = m.pontosRecompensa;
        document.getElementById('m-saude').value = m.bonusSaude || 0;
        document.getElementById('m-hidra').value = m.bonusHidratacao || 0;
        document.getElementById('m-sono').value = m.bonusSono || 0;
        document.getElementById('m-exerc').value = m.bonusExercicio || 0;
        document.getElementById('m-bem').value = m.bonusBemEstar || 0;
    }
}

async function salvarMissao() {
    const id = document.getElementById('m-id').value;
    const body = {
        titulo: document.getElementById('m-titulo').value,
        categoria: document.getElementById('m-cat').value,
        descricao: document.getElementById('m-desc').value,
        pontosRecompensa: parseInt(document.getElementById('m-pts').value) || 0,
        bonusSaude: parseInt(document.getElementById('m-saude').value) || 0,
        bonusHidratacao: parseInt(document.getElementById('m-hidra').value) || 0,
        bonusSono: parseInt(document.getElementById('m-sono').value) || 0,
        bonusExercicio: parseInt(document.getElementById('m-exerc').value) || 0,
        bonusBemEstar: parseInt(document.getElementById('m-bem').value) || 0
    };

    try {
        const res = await fetch(id ? `${API_URL}/missoes/${id}` : `${API_URL}/missoes`, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if(!res.ok) throw await res.json();
        showToast('Missão salva!');
        fecharFormMissao();
        loadMissoes();
    } catch(err) { showToast(err.mensagem || 'Erro ao salvar', true); }
}

async function inativarMissao(id) {
    try {
        await fetch(`${API_URL}/missoes/${id}`, { method: 'DELETE' });
        showToast('Missão inativada.');
        loadMissoes();
    } catch(e) {}
}

// ================= AVATAR (5 Barras) =================
function popularSelectsApp() {
    const sUsr = document.getElementById('select-usuario-app');
    sUsr.innerHTML = '<option value="">-- Selecione o Usuário --</option>' + usuariosList.map(u => `<option value="${u.id}">${u.nome}</option>`).join('');

    const sMis = document.getElementById('select-missao-app');
    sMis.innerHTML = missoesList.filter(m => m.ativa).map(m => `<option value="${m.id}">${m.titulo} (+${m.pontosRecompensa} pts)</option>`).join('');
}

async function carregarAvatarDoUsuario() {
    const uId = document.getElementById('select-usuario-app').value;
    if(!uId) return;

    try {
        const res = await fetch(`${API_URL}/usuarios/${uId}/avatar`);
        if(!res.ok) throw new Error();
        const av = await res.json();
        atualizarVisualAvatar(av);
    } catch(e) { showToast('Erro ao buscar Avatar', true); }
}

function atualizarVisualAvatar(av) {
    document.getElementById('av-nome').textContent = av.nomeAvatar;
    document.getElementById('av-nivel').textContent = `Nível ${av.nivel} | ${av.pontosTotal} pts totais`;

    const fields = [
        {id: 'saude', val: av.saude || 0}, {id: 'hidra', val: av.hidratacao || 0},
        {id: 'sono', val: av.sono || 0}, {id: 'exerc', val: av.exercicio || 0},
        {id: 'bem', val: av.bemEstar || 0}
    ];

    fields.forEach(f => {
        document.getElementById(`av-${f.id}-val`).textContent = f.val + '%';
        document.getElementById(`av-${f.id}`).style.width = f.val + '%';
    });
}

async function enviarConclusaoMissao() {
    const uId = document.getElementById('select-usuario-app').value;
    const mId = document.getElementById('select-missao-app').value;
    const obs = document.getElementById('input-obs-app').value;

    if(!uId || !mId) return showToast('Selecione Usuário e Missão!', true);

    try {
        const res = await fetch(`${API_URL}/usuarios/${uId}/missoes/${mId}/completar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ observacao: obs })
        });
        if(!res.ok) throw await res.json();
        const data = await res.json();

        // Verifica se subiu de nível pra avisar na tela
        if (data.avatarAtualizado.saude === 0 && data.avatarAtualizado.hidratacao === 0) {
            showToast('INCRÍVEL! Nível concluído e barras resetadas!');
        } else {
            showToast('Missão Completada com Sucesso!');
        }

        document.getElementById('input-obs-app').value = '';
        atualizarVisualAvatar(data.avatarAtualizado);
    } catch(err) {
        showToast(err.mensagem || 'Falha ao completar missão', true);
    }
}

// ================= RANKING E ESTATÍSTICAS =================
async function loadRanking() {
    try {
        const res = await fetch(`${API_URL}/ranking`);
        const data = await res.json();
        document.getElementById('tb-ranking').innerHTML = data.map(r => `
            <tr><td>#${r.posicao}</td><td>${r.nomeUsuario}</td><td>${r.nomeAvatar}</td>
            <td>${r.nivel}</td><td style="font-weight:bold; color:var(--accent2)">${r.pontosTotal}</td></tr>
        `).join('');
    } catch(e) {}
}

function popularSelectStats() {
    document.getElementById('select-usuario-stats').innerHTML = '<option value="">-- Selecione --</option>' + usuariosList.map(u => `<option value="${u.id}">${u.nome}</option>`).join('');
}

async function carregarEstatisticas() {
    const uId = document.getElementById('select-usuario-stats').value;
    if(!uId) return;

    try {
        const res = await fetch(`${API_URL}/usuarios/${uId}/estatisticas`);
        const stats = await res.json();

        let htmlCats = '';
        for (const [cat, qtd] of Object.entries(stats.missoesPorCategoria)) {
            htmlCats += `<li>${cat}: <b>${qtd}</b> missões</li>`;
        }

        document.getElementById('stats-area').innerHTML = `
            <p>Missões Concluídas na vida: <b style="color:var(--accent)">${stats.totalMissoesCompletadas}</b></p>
            <p>Nível Atual: <b>${stats.nivelAtual}</b> (Pts Totais: ${stats.pontosTotal})</p>
            <p>Faltam <b style="color:var(--accent3)">${stats.pontosParaProximoNivel} pts</b> para o próximo Nível.</p>
            <hr style="border-color:var(--border); margin:10px 0;">
            <p><b>Distribuição por Categoria:</b></p>
            <ul>${htmlCats || '<li>Nenhuma missão concluída.</li>'}</ul>
        `;
    } catch(e) {}
}