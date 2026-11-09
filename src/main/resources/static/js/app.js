// Adaptado das correcoes de formularios fornecidas pelo professor.
class AppTeatro {
    static iniciado = false;

    static init() {
        if (this.iniciado) return;
        this.iniciado = true;
        // Delegacao: tambem funciona nos elementos inseridos depois pelo htmx.
        document.addEventListener('click', event => this.alternarSenha(event));
        document.addEventListener('submit', event => this.prepararEnvio(event));
        window.addEventListener('pageshow', () => this.restaurarBotoes());
        document.querySelector('form[data-save-form] [aria-invalid="true"]')?.focus();
    }

    static alternarSenha(event) {
        const botao = event.target.closest('.toggle-senha');
        if (!botao || botao.disabled) return;
        const input = document.getElementById(botao.getAttribute('aria-controls'));
        if (!input || input.disabled || !['password', 'text'].includes(input.type)) return;
        event.preventDefault();
        const mostrar = input.type === 'password';
        input.type = mostrar ? 'text' : 'password';
        botao.setAttribute('aria-pressed', String(mostrar));
        botao.setAttribute('aria-label', mostrar ? 'Ocultar senha' : 'Mostrar senha');
        botao.querySelector('.icone-olho-aberto')?.classList.toggle('hidden', !mostrar);
        botao.querySelector('.icone-olho-fechado')?.classList.toggle('hidden', mostrar);
    }

    static prepararEnvio(event) {
        const form = event.target;
        if (!(form instanceof HTMLFormElement)) return;
        const mensagem = form.dataset.confirm;
        if (mensagem && form.dataset.confirmado !== 'true') {
            event.preventDefault();
            const dialog = document.getElementById('confirmacao-exclusao');
            if (!dialog) return;
            dialog.querySelector('#confirmacao-mensagem').textContent = mensagem;
            dialog.returnValue = 'cancelar';
            dialog.addEventListener('close', () => {
                if (dialog.returnValue === 'excluir') {
                    form.dataset.confirmado = 'true';
                    form.requestSubmit();
                }
            }, {once: true});
            dialog.showModal();
            return;
        }
        delete form.dataset.confirmado;
        // Datas, horarios e numeros continuam usando os inputs nativos.
        // Nao normalizar todos os valores: isso alteraria senhas e outros textos.
        if (form.matches('[data-save-form]') && form.checkValidity()) {
            const botao = form.querySelector('button[type="submit"]');
            if (botao) {
                botao.dataset.textoOriginal = botao.textContent;
                botao.disabled = true;
                botao.textContent = 'Salvando…';
            }
        }
    }

    static restaurarBotoes() {
        document.querySelectorAll('[data-save-form] button[type="submit"]').forEach(botao => {
            botao.disabled = false;
            if (botao.dataset.textoOriginal) {
                botao.textContent = botao.dataset.textoOriginal;
                delete botao.dataset.textoOriginal;
            }
        });
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => AppTeatro.init(), {once: true});
} else {
    AppTeatro.init();
}
