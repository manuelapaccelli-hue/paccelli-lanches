// Carrinho de compras: botões "Adicionar" do cardápio e a tela /carrinho
(function() {
    const aviso = document.getElementById('aviso');
    let avisoTimer;

    function mostrarAviso(html, tipo) {
        aviso.innerHTML = html;
        aviso.className = 'aviso' + (tipo === 'erro' ? ' erro' : '');
        aviso.hidden = false;
        clearTimeout(avisoTimer);
        avisoTimer = setTimeout(function() { aviso.hidden = true; }, 4000);
    }

    // Faz a requisição e rejeita com { status, message } quando o servidor responde com erro
    function enviar(url, method, corpo) {
        const opcoes = { method: method };
        if (corpo !== undefined) {
            opcoes.headers = { 'Content-Type': 'application/json' };
            opcoes.body = JSON.stringify(corpo);
        }
        return fetch(url, opcoes).then(function(response) {
            if (!response.ok) {
                return response.text().then(function(erro) {
                    throw { status: response.status, message: erro || 'Não foi possível concluir a operação.' };
                });
            }
            return response.status === 204 ? null : response.json();
        });
    }

    function mostrarErro(erro) {
        if (erro.status === 401) {
            mostrarAviso('Faça <a href="/login">login</a> para usar o carrinho.', 'erro');
        } else {
            mostrarAviso(erro.message || 'Não foi possível conectar ao servidor.', 'erro');
        }
    }

    // ----- Cardápio: adicionar itens -----
    document.querySelectorAll('.btn-adicionar').forEach(function(botao) {
        botao.addEventListener('click', function() {
            const item = botao.closest('.item');
            const nome = item.querySelector('.item-nome').textContent;
            botao.disabled = true;

            enviar('/carrinho/itens', 'POST', { lancheId: Number(item.dataset.id) })
                .then(function() {
                    mostrarAviso('✅ ' + nome + ' adicionado ao carrinho. <a href="/carrinho">Ver carrinho</a>');
                })
                .catch(mostrarErro)
                .finally(function() {
                    botao.disabled = false;
                });
        });
    });

    // ----- Tela do carrinho -----
    const lista = document.getElementById('carrinhoItens');
    if (!lista) {
        return;
    }

    const vazio = document.getElementById('carrinhoVazio');
    const vazioTexto = document.getElementById('carrinhoVazioTexto');
    const vazioLink = document.getElementById('carrinhoVazioLink');
    const total = document.getElementById('carrinhoTotal');
    const modelo = document.getElementById('itemCarrinhoModelo');

    function renderizar(carrinho) {
        lista.innerHTML = '';
        vazio.hidden = carrinho.itens.length > 0;
        total.textContent = carrinho.total;

        carrinho.itens.forEach(function(item) {
            const linha = modelo.content.firstElementChild.cloneNode(true);
            linha.querySelector('.item-nome').textContent = item.nome;
            linha.querySelector('.item-desc').textContent = item.precoUnitario + ' cada';
            linha.querySelector('.item-quantidade').textContent = item.quantidade;
            linha.querySelector('.item-preco').textContent = item.subtotal;

            const url = '/carrinho/itens/' + item.lancheId;
            linha.querySelector('.btn-aumentar').addEventListener('click', function() {
                atualizar(enviar(url, 'PUT', { quantidade: item.quantidade + 1 }));
            });
            linha.querySelector('.btn-diminuir').addEventListener('click', function() {
                // Diminuir a partir de 1 tira o item do carrinho
                atualizar(item.quantidade > 1
                    ? enviar(url, 'PUT', { quantidade: item.quantidade - 1 })
                    : enviar(url, 'DELETE'));
            });
            linha.querySelector('.btn-remover').addEventListener('click', function() {
                atualizar(enviar(url, 'DELETE'));
            });

            lista.appendChild(linha);
        });
    }

    function carregar() {
        return enviar('/carrinho/itens', 'GET')
            .then(renderizar)
            .catch(function(erro) {
                if (erro.status === 401) {
                    vazioTexto.textContent = 'Faça login para ver o seu carrinho.';
                    vazioLink.textContent = 'Entrar';
                    vazioLink.href = '/login';
                } else {
                    mostrarErro(erro);
                }
            });
    }

    function atualizar(requisicao) {
        lista.querySelectorAll('button').forEach(function(botao) { botao.disabled = true; });
        requisicao.catch(mostrarErro).finally(carregar);
    }

    carregar();
})();
