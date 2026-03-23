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

  setTimeout(() => {
    toast.remove();
  }, 3000);
}


/* ========================= */
/* DROPDOWN ANIMADO */
/* ========================= */

document.querySelectorAll('.dots').forEach(dots => {

  dots.addEventListener('click', e => {
    e.stopPropagation();

    const dropdown = dots.closest('.bot-menu').querySelector('.dropdown');

    document.querySelectorAll('.dropdown').forEach(d => {
      if (d !== dropdown) d.classList.remove('active');
    });

    dropdown.classList.toggle('active');
  });

});

document.addEventListener('click', () => {
  document.querySelectorAll('.dropdown').forEach(d => {
    d.classList.remove('active');
  });
});


/* ========================= */
/* RENOMEAR */
/* ========================= */

document.querySelectorAll('.rename-btn-project').forEach(btn => {

  btn.addEventListener('click', e => {
    e.stopPropagation();

    const chatItem = btn.closest('.chat-item');
    const nameSpan = chatItem.querySelector('.bot-name');
    const botId = chatItem.dataset.id;
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

      if (newName === "" || newName === oldName) {
        input.replaceWith(nameSpan);
        return;
      }

      fetch(`/bots/${botId}/rename`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome: newName })
      })
        .then(res => {
          if (!res.ok) throw new Error();
          nameSpan.textContent = newName;
          input.replaceWith(nameSpan);
          showToast("Bot renomeado com sucesso");
        })
        .catch(() => {
          showToast("Erro ao renomear", "error");
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

    fetch(`/bots/${botId}/delete`, {
      method: 'POST'
    })
      .then(res => {
        if (!res.ok) throw new Error();
        chatItem.remove();
        showToast("Bot excluído com sucesso");
        window.location.href = `/bots`;
      })
      .catch(() => {
        showToast("Erro ao excluir", "error");
      });
  });

});


/* ========================= */
/* AUTO SCROLL */
/* ========================= */

window.addEventListener('load', () => {
  const chatArea = document.querySelector('.chat-area');
  if (chatArea) {
    chatArea.scrollTop = chatArea.scrollHeight;
  }
});


/* ========================= */
/* MARKDOWN + MODAL */
/* ========================= */

document.addEventListener("DOMContentLoaded", function () {

  // Flash error
  const flash = document.getElementById("flash-error");
  if (flash) {
    const message = flash.dataset.message;
    showToast(message, "error");
  }

  // Markdown
  marked.setOptions({
    breaks: true,
    gfm: true
  });

  document.querySelectorAll(".markdown").forEach(el => {
    const rawText = el.textContent;
    const html = marked.parse(rawText);
    el.innerHTML = DOMPurify.sanitize(html);
  });

  // Modal compartilhar
  const shareBtn = document.querySelector('.share');
  const shareModal = document.getElementById('share-modal');
  const closeModal = document.getElementById('close-share-modal');

  if (shareBtn && shareModal && closeModal) {

    shareBtn.addEventListener('click', () => {
      shareModal.classList.add('open');
    });

    closeModal.addEventListener('click', fecharModal);

    shareModal.addEventListener('click', e => {
      if (e.target === shareModal) fecharModal();
    });

    document.querySelectorAll('.share-modal-item').forEach(item => {
      item.addEventListener('click', () => {
        const projetoId = item.dataset.id;
        const link = `http://192.168.10.72:8008/compartilharProjeto/${projetoId}`;

        if (navigator.clipboard && navigator.clipboard.writeText) {
          navigator.clipboard.writeText(link)
            .then(() => {
              showToast(`Link está na sua área de transferência!`);
              fecharModal();
            })
            .catch(() => {
              showToast("Erro ao copiar o link", "error");
            });
        } else {
          // Fallback para HTTP
          const el = document.createElement('textarea');
          el.value = link;
          el.style.position = 'fixed';
          el.style.opacity = '0';
          document.body.appendChild(el);
          el.focus();
          el.select();
          try {
            document.execCommand('copy');
            showToast(`Link está na sua área de transferência!`);
            fecharModal();
          } catch {
            showToast("Erro ao copiar o link", "error");
          }
          document.body.removeChild(el);
        }

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
/* ABRIR / FECHAR PROJETO */
/* ========================= */

document.querySelectorAll('.project-toggle').forEach(toggle => {

  toggle.addEventListener('click', e => {

    if (e.target.closest('.bot-menu')) return;

    const projectItem = toggle.closest('.project-item');
    projectItem.classList.toggle('open');

  });

});