// Tela /perfil: editar os próprios dados, trocar a senha e sair da conta
(function() {
    function mostrarMensagem(form, texto, tipo) {
        const mensagem = form.querySelector('.mensagem');
        mensagem.textContent = texto;
        mensagem.className = 'mensagem ' + tipo;
        mensagem.hidden = false;
    }

    function enviar(url, corpo) {
        return fetch(url, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(corpo)
        }).then(function(response) {
            if (response.status === 401) {
                window.location.href = '/login';
                return new Promise(function() {});
            }
            if (!response.ok) {
                return response.text().then(function(erro) {
                    throw new Error(erro || 'Não foi possível salvar.');
                });
            }
        });
    }

    function valor(form, nome) {
        const texto = form.elements[nome].value.trim();
        return texto === '' ? null : texto;
    }

    // Salva os dados pessoais
    const formPerfil = document.getElementById('formPerfil');
    formPerfil.addEventListener('submit', function(evento) {
        evento.preventDefault();
        const botao = formPerfil.querySelector('[type="submit"]');
        botao.disabled = true;

        enviar('/perfil/dados', {
            nome: valor(formPerfil, 'nome'),
            email: valor(formPerfil, 'email'),
            telefone: valor(formPerfil, 'telefone'),
            cpf: valor(formPerfil, 'cpf'),
            dataNascimento: valor(formPerfil, 'dataNascimento')
        })
            .then(function() {
                mostrarMensagem(formPerfil, '✅ Dados atualizados.', 'sucesso');
            })
            .catch(function(erro) {
                mostrarMensagem(formPerfil, erro.message, 'erro');
            })
            .finally(function() {
                botao.disabled = false;
            });
    });

    // Troca a senha (a confirmação é conferida aqui; as regras ficam no servidor)
    const formSenha = document.getElementById('formSenha');
    formSenha.addEventListener('submit', function(evento) {
        evento.preventDefault();
        const novaSenha = formSenha.elements.novaSenha.value;
        if (novaSenha !== formSenha.elements.confirmarSenha.value) {
            mostrarMensagem(formSenha, 'A confirmação não é igual à nova senha.', 'erro');
            return;
        }
        const botao = formSenha.querySelector('[type="submit"]');
        botao.disabled = true;

        enviar('/perfil/senha', { senhaAtual: formSenha.elements.senhaAtual.value, novaSenha: novaSenha })
            .then(function() {
                formSenha.reset();
                mostrarMensagem(formSenha, '✅ Senha alterada.', 'sucesso');
            })
            .catch(function(erro) {
                mostrarMensagem(formSenha, erro.message, 'erro');
            })
            .finally(function() {
                botao.disabled = false;
            });
    });

    document.getElementById('logoutBtn').addEventListener('click', function() {
        fetch('/login/sessao', { method: 'DELETE' })
            .finally(function() {
                window.location.href = '/login';
            });
    });
})();
