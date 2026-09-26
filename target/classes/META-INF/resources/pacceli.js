// Lógica de Login (executa apenas se o formulário de login existir na página)
const loginForm = document.getElementById('loginForm');
if (loginForm) {
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
                        alert(msg || 'E-mail ou senha inválidos.');
                    });
                }
            })
            .catch(function() {
                alert('Não foi possível conectar ao servidor. Tente novamente.');
            });
    });
}

// Lógica da tela de Perfil (executa apenas se o box de perfil existir na página)
const perfilBox = document.getElementById('perfilBox');
if (perfilBox) {
    fetch('/perfil/dados')
        .then(function(response) {
            if (response.status === 401) {
                window.location.href = "/login";
                return null;
            }
            return response.json();
        })
        .then(function(perfil) {
            if (!perfil) return;

            document.getElementById('perfilNome').textContent = perfil.nome || '—';
            document.getElementById('perfilEmail').textContent = perfil.email || '—';
            document.getElementById('perfilTelefone').textContent = perfil.telefone || '—';
            document.getElementById('perfilCpf').textContent = perfil.cpf || '—';
            document.getElementById('perfilDataNascimento').textContent = perfil.dataNascimento || '—';
        })
        .catch(function() {
            alert('Não foi possível carregar seu perfil. Tente novamente.');
        });

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            fetch('/login/sessao', { method: 'DELETE' })
                .finally(function() {
                    window.location.href = "/login";
                });
        });
    }
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
            alert('As senhas não coincidem! Por favor, verifique.');
            return;
        }

        if (senha.length < 6) {
            alert('A senha deve ter pelo menos 6 caracteres.');
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
                    alert('✅ Cadastro realizado com sucesso!\n\nBem-vindo ao Paccelli Lanches! 🍔');
                    window.location.href = "login";
                } else {
                    return response.text().then(function(msg) {
                        alert(msg || 'Não foi possível concluir o cadastro.');
                    });
                }
            })
            .catch(function() {
                alert('Não foi possível conectar ao servidor. Tente novamente.');
            });
    });
}