// Ações da tela /usuarios: tipo de usuário, telefone e exclusão
(function() {
    const mensagem = document.getElementById('mensagem');

    function mostrarMensagem(texto, tipo) {
        mensagem.textContent = texto;
        mensagem.className = 'mensagem ' + tipo;
        mensagem.hidden = false;
    }

    function enviar(url, method, corpo) {
        const opcoes = { method: method };
        if (corpo !== undefined) {
            opcoes.headers = { 'Content-Type': 'application/json' };
            opcoes.body = JSON.stringify(corpo);
        }
        return fetch(url, opcoes).then(function(response) {
            if (!response.ok) {
                return response.text().then(function(erro) {
                    throw new Error(erro || 'Não foi possível concluir a operação.');
                });
            }
        });
    }

    function idDaLinha(elemento) {
        return elemento.closest('tr').dataset.id;
    }

    document.querySelectorAll('.select-tipo').forEach(function(select) {
        select.addEventListener('change', function() {
            const tipo = select.value;
            const descricao = select.options[select.selectedIndex].text;
            select.disabled = true;

            enviar('/usuarios/' + idDaLinha(select) + '/tipo', 'PUT', { tipo: tipo })
                .then(function() {
                    select.dataset.atual = tipo;
                    mostrarMensagem('Tipo do usuário alterado para ' + descricao + '.', 'sucesso');
                })
                .catch(function(erro) {
                    select.value = select.dataset.atual;
                    mostrarMensagem(erro.message, 'erro');
                })
                .finally(function() {
                    select.disabled = false;
                });
        });
    });

    document.querySelectorAll('.btn-salvar').forEach(function(botao) {
        botao.addEventListener('click', function() {
            const input = botao.closest('tr').querySelector('.input-telefone');
            botao.disabled = true;

            enviar('/usuarios/' + idDaLinha(botao) + '/telefone', 'PUT', { telefone: input.value })
                .then(function() {
                    mostrarMensagem('Telefone atualizado.', 'sucesso');
                })
                .catch(function(erro) {
                    mostrarMensagem(erro.message, 'erro');
                })
                .finally(function() {
                    botao.disabled = false;
                });
        });
    });

    document.querySelectorAll('.btn-excluir').forEach(function(botao) {
        botao.addEventListener('click', function() {
            const linha = botao.closest('tr');
            const nome = linha.querySelector('td').textContent;
            if (!confirm('Excluir o usuário ' + nome + '?')) {
                return;
            }
            botao.disabled = true;

            enviar('/usuarios/' + idDaLinha(botao), 'DELETE')
                .then(function() {
                    linha.remove();
                    mostrarMensagem('Usuário excluído.', 'sucesso');
                })
                .catch(function(erro) {
                    botao.disabled = false;
                    mostrarMensagem(erro.message, 'erro');
                });
        });
    });
})();
