// Mostra uma mensagem dentro do formulário (no lugar do alert do navegador)
function mostrarMensagemForm(texto, tipo) {
    const mensagem = document.getElementById('mensagemForm');
    mensagem.textContent = texto;
    mensagem.className = 'mensagem-form ' + tipo;
    mensagem.hidden = false;
}

// Lógica de Login (executa apenas se o formulário de login existir na página)
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    // Vindo do cadastro (/login?conta=criada), avisa que a conta foi criada
    if (new URLSearchParams(window.location.search).get('conta') === 'criada') {
        mostrarMensagemForm('✅ Conta criada! Entre com o seu e-mail e senha.', 'sucesso');
    }

    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const email = document.getElementById('email').value;
        const senha = document.getElementById('senha').value;

        fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        })
            .then(function(response) {
                if (response.ok) {
                    window.location.href = "/";
                } else {
                    return response.text().then(function(msg) {
                        mostrarMensagemForm(msg || 'E-mail ou senha inválidos.', 'erro');
                    });
                }
            })
            .catch(function() {
                mostrarMensagemForm('Não foi possível conectar ao servidor. Tente novamente.', 'erro');
            });
    });
}

// Lógica de Cadastro (executa apenas se o formulário de cadastro existir na página)
const cadastroForm = document.getElementById('cadastroForm');
if (cadastroForm) {
    // Máscara simples para telefone
    const telefoneInput = document.getElementById('telefone');
    if (telefoneInput) {
        telefoneInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 11) value = value.slice(0, 11);

            if (value.length > 2) {
                value = `(${value.slice(0,2)}) ${value.slice(2)}`;
            }
            if (value.length > 9) {
                value = value.slice(0,9) + '-' + value.slice(9);
            }
            e.target.value = value;
        });
    }

    // Máscara simples para CPF
    const cpfInput = document.getElementById('cpf');
    if (cpfInput) {
        cpfInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 11) value = value.slice(0, 11);

            if (value.length > 9) {
                value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
            } else if (value.length > 6) {
                value = value.replace(/(\d{3})(\d{3})(\d{3})/, '$1.$2.$3');
            } else if (value.length > 3) {
                value = value.replace(/(\d{3})(\d{3})/, '$1.$2');
            }
            e.target.value = value;
        });
    }

    // Validação do formulário de cadastro
    cadastroForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const senha = document.getElementById('senha').value;
        const confirmarSenha = document.getElementById('confirmar-senha').value;

        if (senha !== confirmarSenha) {
            mostrarMensagemForm('As senhas não coincidem. Por favor, verifique.', 'erro');
            return;
        }

        if (senha.length < 6) {
            mostrarMensagemForm('A senha deve ter pelo menos 6 caracteres.', 'erro');
            return;
        }

        const nome = document.getElementById('nome').value;
        const email = document.getElementById('email').value;
        const telefone = document.getElementById('telefone').value;
        const cpf = document.getElementById('cpf').value;
        const dataNascimento = document.getElementById('data-nasc').value || null;

        fetch('/cadastro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, telefone, cpf, dataNascimento, senha })
        })
            .then(function(response) {
                if (response.ok) {
                    window.location.href = '/login?conta=criada';
                } else {
                    return response.text().then(function(msg) {
                        mostrarMensagemForm(msg || 'Não foi possível concluir o cadastro.', 'erro');
                    });
                }
            })
            .catch(function() {
                mostrarMensagemForm('Não foi possível conectar ao servidor. Tente novamente.', 'erro');
            });
    });
}