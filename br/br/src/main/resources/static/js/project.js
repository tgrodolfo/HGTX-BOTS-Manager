
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

      fetch(`/projeto/${botId}/rename`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome: newName })
      })
      .then(res => {
        if (!res.ok) throw new Error();
        nameSpan.textContent = newName;
        input.replaceWith(nameSpan);
        showToast("Projeto renomeado com sucesso");
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

document.querySelectorAll('.delete-project').forEach(btn => {

  btn.addEventListener('click', e => {
    e.stopPropagation();

    const chatItem = btn.closest('.chat-item');
    const botId = chatItem.dataset.id;

    if (!confirm("Tem certeza que deseja excluir este Projeto?")) return;

    fetch(`/projeto/${botId}/delete`, {
      method: 'POST'
    })
    .then(res => {
      if (!res.ok) throw new Error();
      chatItem.remove();
      showToast("Projeto excluído com sucesso");
    })
    .catch(() => {
      showToast("Erro ao excluir", "error");
    });

  });

});