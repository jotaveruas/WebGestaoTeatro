document.addEventListener('submit', function (event) {
    const form = event.target;
    if (!(form instanceof HTMLFormElement)) return;
    const mensagem = form.dataset.confirm;
    if (mensagem && form.dataset.confirmado !== 'true') {
        event.preventDefault();
        const dialog = document.getElementById('confirmacao-exclusao');
        if (!dialog) return;
        dialog.querySelector('#confirmacao-mensagem').textContent = mensagem;
        dialog.returnValue = 'cancelar';
        dialog.addEventListener('close', function () {
            if (dialog.returnValue === 'excluir') {
                form.dataset.confirmado = 'true';
                form.requestSubmit();
            }
        }, {once: true});
        dialog.showModal();
        return;
    }
    delete form.dataset.confirmado;
    if (form.matches('[data-save-form]') && form.checkValidity()) {
        const botao = form.querySelector('button[type="submit"]');
        if (botao) {
            botao.disabled = true;
            botao.textContent = 'Salvando…';
        }
    }
});
window.addEventListener('pageshow', function () {
    document.querySelectorAll('[data-save-form] button[type="submit"]').forEach(function (botao) {
        botao.disabled = false;
        if (botao.textContent === 'Salvando…') window.location.reload();
    });
});
