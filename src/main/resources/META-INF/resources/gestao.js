// Telas de gestão (cardápio e promoções): formulário de cadastro/edição e remoção pela tabela.
// O formulário #formGestao envia os campos pelo "name"; cada linha da tabela guarda os valores
// atuais em atributos data-* com os mesmos nomes, usados para preencher o formulário ao editar.
(function() {
    const CHAVE_MENSAGEM = 'gestaoMensagem';
    const form = document.getElementById('formGestao');
    const titulo = document.getElementById('formTitulo');
    const cancelar = document.getElementById('cancelarEdicao');
    const mensagem = document.getElementById('mensagem');
    const url = form.dataset.url;

    function mostrarMensagem(texto, tipo) {
        mensagem.textContent = texto;
        mensagem.className = 'mensagem ' + tipo;
        mensagem.hidden = false;
    }

    // Mostra a mensagem após recarregar a página e só então recarrega
    function recarregarCom(texto) {
        try {
            sessionStorage.setItem(CHAVE_MENSAGEM, texto);
        } catch (e) {
            // sem sessionStorage a mensagem apenas não aparece
        }
        window.location.reload();
    }

    function enviar(endereco, method, corpo) {
        const opcoes = { method: method };
        if (corpo !== undefined) {
            opcoes.headers = { 'Content-Type': 'application/json' };
            opcoes.body = JSON.stringify(corpo);
        }
        return fetch(endereco, opcoes).then(function(response) {
            if (!response.ok) {
                return response.text().then(function(erro) {
                    throw new Error(erro || 'Não foi possível concluir a operação.');
                });
            }
        });
    }

    function modoNovo() {
        form.reset();
        form.elements.id.value = '';
        titulo.textContent = titulo.dataset.tituloNovo;
        cancelar.hidden = true;
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

    form.addEventListener('submit', function(evento) {
        evento.preventDefault();
        const id = form.elements.id.value;
        const corpo = {};
        Array.from(form.elements).forEach(function(campo) {
            if (campo.name && campo.name !== 'id') {
                corpo[campo.name] = campo.value.trim() === '' ? null : campo.value.trim();
            }
        });

        const botao = form.querySelector('[type="submit"]');
        botao.disabled = true;
        enviar(id ? url + '/' + id : url, id ? 'PUT' : 'POST', corpo)
            .then(function() {
                recarregarCom(id ? 'Alterações salvas.' : 'Cadastro realizado.');
            })
            .catch(function(erro) {
                mostrarMensagem(erro.message, 'erro');
                botao.disabled = false;
            });
    });

    cancelar.addEventListener('click', modoNovo);

    document.querySelectorAll('.btn-editar').forEach(function(botao) {
        botao.addEventListener('click', function() {
            const linha = botao.closest('tr');
            Array.from(form.elements).forEach(function(campo) {
                const nome = campo.name;
                if (nome && linha.dataset[nome] !== undefined) {
                    campo.value = linha.dataset[nome];
                }
            });
            titulo.textContent = titulo.dataset.tituloEdicao;
            cancelar.hidden = false;
            mensagem.hidden = true;
            form.scrollIntoView({ behavior: 'smooth' });
        });
    });

    document.querySelectorAll('.btn-remover').forEach(function(botao) {
        botao.addEventListener('click', function() {
            if (!confirm(botao.dataset.confirmar)) {
                return;
            }
            botao.disabled = true;
            enviar(url + '/' + botao.closest('tr').dataset.id, 'DELETE')
                .then(function() {
                    recarregarCom(botao.dataset.sucesso);
                })
                .catch(function(erro) {
                    mostrarMensagem(erro.message, 'erro');
                    botao.disabled = false;
                });
        });
    });
})();
