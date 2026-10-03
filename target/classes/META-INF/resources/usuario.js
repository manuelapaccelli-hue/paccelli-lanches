// Ações da tela /usuarios: permissão de administrador, telefone e exclusão
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

    document.querySelectorAll('.toggle-admin').forEach(function(checkbox) {
        checkbox.addEventListener('change', function() {
            const admin = checkbox.checked;
            checkbox.disabled = true;

            enviar('/usuarios/' + idDaLinha(checkbox) + '/admin', 'PUT', { admin: admin })
                .then(function() {
                    mostrarMensagem(admin ? 'Usuário promovido a administrador.' : 'Permissão de administrador removida.', 'sucesso');
                })
                .catch(function(erro) {
                    checkbox.checked = !admin;
                    mostrarMensagem(erro.message, 'erro');
                })
                .finally(function() {
                    checkbox.disabled = false;
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
