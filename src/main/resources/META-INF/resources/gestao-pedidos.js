// Gestão de pedidos: o botão "Salvar" de cada linha envia o status escolhido no select.
(function() {
    const CHAVE_MENSAGEM = 'gestaoMensagem';
    const mensagem = document.getElementById('mensagem');

    function mostrarMensagem(texto, tipo) {
        mensagem.textContent = texto;
        mensagem.className = 'mensagem ' + tipo;
        mensagem.hidden = false;
    }

    try {
        const pendente = sessionStorage.getItem(CHAVE_MENSAGEM);
        if (pendente) {
            sessionStorage.removeItem(CHAVE_MENSAGEM);
            mostrarMensagem(pendente, 'sucesso');
        }
    } catch (e) {
        // sem sessionStorage não há mensagem pendente
    }

    document.querySelectorAll('.btn-status').forEach(function(botao) {
        botao.addEventListener('click', function() {
            const linha = botao.closest('tr');
            const select = linha.querySelector('select');
            botao.disabled = true;

            fetch('/gestao-pedidos/pedidos/' + linha.dataset.id + '/status', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: select.value })
            })
                .then(function(response) {
                    if (!response.ok) {
                        return response.text().then(function(erro) {
                            throw new Error(erro || 'Não foi possível alterar o status.');
                        });
                    }
                    try {
                        sessionStorage.setItem(CHAVE_MENSAGEM, 'Pedido #' + linha.dataset.id + ': status alterado para '
                            + select.options[select.selectedIndex].text + '.');
                    } catch (e) {
                        // sem sessionStorage a mensagem apenas não aparece
                    }
                    window.location.reload();
                })
                .catch(function(erro) {
                    mostrarMensagem(erro.message, 'erro');
                    botao.disabled = false;
                });
        });
    });
})();
