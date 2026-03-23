/* ========================= */
/* TOAST SYSTEM */
/* ========================= */

function showToast(message, type = "success") {
  let container = document.querySelector(".toast-container");
  if (!container) {
    container = document.createElement("div");
    container.className = "toast-container";
    document.body.appendChild(container);
  }
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 3000);
}


/* ========================= */
/* DROPDOWN ANIMADO */
/* ========================= */

document.querySelectorAll('.dots').forEach(dots => {
  dots.addEventListener('click', e => {
    e.stopPropagation();
    const dropdown = dots.closest('.bot-menu').querySelector('.dropdown');

    const jaEstaAberto = dropdown.classList.contains('active');

    // fecha todos
    document.querySelectorAll('.dropdown').forEach(d => d.classList.remove('active'));

    // se não estava aberto, abre
    if (!jaEstaAberto) {
      dropdown.classList.add('active');
    }
  });
});

document.addEventListener('click', () => {
  document.querySelectorAll('.dropdown').forEach(d => d.classList.remove('active'));
});


/* ========================= */
/* RENOMEAR */
/* ========================= */

document.querySelectorAll('.rename-btn-project').forEach(btn => {
  btn.addEventListener('click', e => {
    e.stopPropagation();

    const chatItem = btn.closest('.project-item');
    const nameSpan = chatItem.querySelector('.bot-name');
    const projetoId = chatItem.dataset.id;
    const oldName = nameSpan.textContent;

    const input = document.createElement('input');
    input.type = 'text';
    input.value = oldName;
    input.classList.add('rename-input');

    nameSpan.replaceWith(input);
    input.focus();
    input.select();

    function salvar() {
      const newName = input.value.trim();
      if (newName === '' || newName === oldName) {
        input.replaceWith(nameSpan);
        return;
      }
      fetch(`/projeto/${projetoId}/rename`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome: newName })
      })
        .then(res => {
          if (!res.ok) throw new Error();
          nameSpan.textContent = newName;
          input.replaceWith(nameSpan);
          showToast('Projeto renomeado com sucesso');
        })
        .catch(() => {
          showToast('Erro ao renomear', 'error');
          input.replaceWith(nameSpan);
        });
    }

    input.addEventListener('blur', salvar);
    input.addEventListener('keydown', e => {
      if (e.key === 'Enter') salvar();
      if (e.key === 'Escape') input.replaceWith(nameSpan);
    });
  });
});


/* ========================= */
/* DELETAR */
/* ========================= */

document.querySelectorAll('.delete').forEach(btn => {
  btn.addEventListener('click', e => {
    e.stopPropagation();
    const chatItem = btn.closest('.chat-item');
    const botId = chatItem.dataset.id;
    if (!confirm("Tem certeza que deseja excluir este bot?")) return;
    fetch(`/bots/${botId}/delete`, { method: 'POST' })
      .then(res => {
        if (!res.ok) throw new Error();
        chatItem.remove();
        showToast("Bot excluído com sucesso");
        window.location.href = `/bots`;
      })
      .catch(() => showToast("Erro ao excluir", "error"));
  });
});


/* ========================= */
/* AUTO SCROLL */
/* ========================= */

window.addEventListener('load', () => {
  const chatArea = document.querySelector('.chat-area');
  if (chatArea) chatArea.scrollTop = chatArea.scrollHeight;
});


/* ========================= */
/* MARKDOWN + MODAL */
/* ========================= */

document.addEventListener("DOMContentLoaded", function () {

  const flash = document.getElementById("flash-error");
  if (flash) showToast(flash.dataset.message, "error");

  marked.setOptions({ breaks: true, gfm: true });

  document.querySelectorAll(".markdown").forEach(el => {
    const rawText = el.textContent;
    el.innerHTML = DOMPurify.sanitize(marked.parse(rawText));
  });

  const shareBtn = document.querySelector('.share');
  const shareModal = document.getElementById('share-modal');
  const closeModal = document.getElementById('close-share-modal');

  if (shareBtn && shareModal && closeModal) {
    shareBtn.addEventListener('click', () => shareModal.classList.add('open'));
    closeModal.addEventListener('click', fecharModal);
    shareModal.addEventListener('click', e => {
      if (e.target === shareModal) fecharModal();
    });

    document.querySelectorAll('.share-modal-item').forEach(item => {
      item.addEventListener('click', () => {
        const projetoId = item.dataset.id;
        const link = `https://seusite.com/compartilharProjeto/${projetoId}`;
        navigator.clipboard.writeText(link)
          .then(() => { showToast("Link copiado!"); fecharModal(); })
          .catch(() => showToast("Erro ao copiar o link", "error"));
      });
    });
  }
});

function fecharModal() {
  const shareModal = document.getElementById('share-modal');
  if (shareModal) shareModal.classList.remove('open');
}


/* ========================= */
/* EFEITO DIGITANDO */
/* ========================= */

function typeWriter(element, text, speed = 20) {
  element.classList.add("typing");
  element.textContent = "";
  let i = 0;
  function type() {
    if (i < text.length) {
      element.textContent += text.charAt(i);
      i++;
      setTimeout(type, speed);
    } else {
      element.classList.remove("typing");
    }
  }
  type();
}


/* ========================= */
/* ESTADO GLOBAL */
/* ========================= */

const filaMensagens = [];
let botRespondendo = false;
let ultimoIdGlobal = 0; // 👈 compartilhado entre todos os pollings


/* ========================= */
/* FILA DE MENSAGENS */
/* ========================= */

const form = document.querySelector('.formenviomensagem');

if (form) {
  const botId = form.action.split('/bots/')[1].split('/')[0];

  form.addEventListener('submit', e => {
    e.preventDefault();

    const input = document.getElementById('centerinput');
    const mensage = input.value.trim();
    if (!mensage) return;

    input.value = '';
    input.focus();

    const nomeUsuario = document.querySelector('.user a')?.textContent?.trim() || 'Você';

    const el = adicionarMensagem(mensage, nomeUsuario, 'user');
    // mensagem do usuário não vem do banco ainda, não tem id — tudo bem

    if (botRespondendo) {
      filaMensagens.push(mensage);
      showToast(`Mensagem na fila (${filaMensagens.length})`);
      return;
    }

    enviarMensagem(botId, mensage, nomeUsuario);
  });
}

function enviarMensagem(botId, mensage, nomeUsuario) {
  botRespondendo = true;
  const typing = adicionarDigitando();

  fetch(`/bots/${botId}/messages`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ mensage })
  })
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        showToast(data.error, 'error');
        typing.remove();
        proximaDaFila(botId, nomeUsuario);
        return;
      }
      // atualiza o ultimoIdGlobal com o id da mensagem do usuário que acabou de salvar
      ultimoIdGlobal = Math.max(ultimoIdGlobal, parseInt(data.lastId));
      iniciarPolling(botId, data.lastId, typing, nomeUsuario);
    })
    .catch(() => {
      showToast('Erro ao enviar mensagem', 'error');
      typing.remove();
      proximaDaFila(botId, nomeUsuario);
    });
}

function proximaDaFila(botId, nomeUsuario) {
  botRespondendo = false;

  if (filaMensagens.length > 0) {
    const proxima = filaMensagens.shift();
    enviarMensagem(botId, proxima, nomeUsuario);
  }
}

function iniciarPolling(botId, lastId, typingEl, nomeUsuario) {
  const intervalo = setInterval(() => {
    fetch(`/bots/${botId}/messages/poll?lastId=${lastId}`)
      .then(res => res.json())
      .then(mensagens => {
        if (mensagens.length > 0) {
          clearInterval(intervalo);
          typingEl.remove();

          mensagens.forEach(m => {
            const el = adicionarMensagem(m.conteudo, m.remetente, 'bot');
            el.dataset.id = m.id;
            ultimoIdGlobal = Math.max(ultimoIdGlobal, parseInt(m.id)); // 👈 atualiza o global
          });

          proximaDaFila(botId, nomeUsuario);
        }
      })
      .catch(() => {
        clearInterval(intervalo);
        typingEl.remove();
        proximaDaFila(botId, nomeUsuario);
      });
  }, 2000);

  setTimeout(() => {
    clearInterval(intervalo);
    typingEl.remove();
    showToast('Bot demorou muito pra responder', 'error');
    proximaDaFila(botId, nomeUsuario);
  }, 30000);
}

function adicionarMensagem(conteudo, remetente, tipo) {
  const messages = document.querySelector('.messages');
  const emptyState = messages.querySelector('.empty-state');
  if (emptyState) emptyState.remove();

  const div = document.createElement('div');
  div.className = `message ${tipo}`;
  div.innerHTML = `
    <div class="message-wrapper">
      <p class="sender">${remetente}</p>
      <div class="bubble markdown">${DOMPurify.sanitize(marked.parse(conteudo))}</div>
    </div>
  `;

  messages.appendChild(div);
  document.querySelector('.chat-area').scrollTop = document.querySelector('.chat-area').scrollHeight;
  return div;
}

function adicionarDigitando() {
  const messages = document.querySelector('.messages');
  const div = document.createElement('div');
  div.className = 'message bot typing-indicator';
  div.innerHTML = `
    <div class="message-wrapper">
      <p class="sender">BOT</p>
      <div class="bubble">
        <span class="dot"></span>
        <span class="dot"></span>
        <span class="dot"></span>
      </div>
    </div>
  `;
  messages.appendChild(div);
  document.querySelector('.chat-area').scrollTop = document.querySelector('.chat-area').scrollHeight;
  return div;
}


/* ========================= */
/* ABRIR / FECHAR PROJETO */
/* ========================= */

document.querySelectorAll('.project-toggle').forEach(toggle => {
  toggle.addEventListener('click', e => {
    if (e.target.closest('.bot-menu')) return;
    const projectItem = toggle.closest('.project-item');
    projectItem.classList.toggle('open');
  });
});


/* ========================= */
/* POLLING GLOBAL (tempo real) */
/* ========================= */

(function () {
  const form = document.querySelector('.formenviomensagem');
  if (!form) return;

  const botId = form.action.split('/bots/')[1].split('/')[0];
  const nomeUsuario = document.querySelector('.user a')?.textContent?.trim();

  // inicializa o ultimoIdGlobal com o maior id já carregado na página
  function inicializarUltimoId() {
    const mensagens = document.querySelectorAll('.messages .message');
    if (mensagens.length === 0) return 0;
    const ids = Array.from(mensagens)
      .map(m => parseInt(m.dataset.id || 0))
      .filter(id => !isNaN(id) && id > 0);
    return ids.length > 0 ? Math.max(...ids) : 0;
  }

  ultimoIdGlobal = inicializarUltimoId();

  setInterval(() => {
    if (botRespondendo) return; // polling de envio já tá cuidando

    fetch(`/bots/${botId}/messages/poll?lastId=${ultimoIdGlobal}`)
      .then(res => res.json())
      .then(mensagens => {
        mensagens.forEach(m => {
          const tipo = m.remetente === nomeUsuario ? 'user' : 'bot';
          const el = adicionarMensagem(m.conteudo, m.remetente, tipo);
          el.dataset.id = m.id;
          ultimoIdGlobal = Math.max(ultimoIdGlobal, parseInt(m.id)); // 👈 atualiza o global
        });
      })
      .catch(() => { });
  }, 3000);
})();