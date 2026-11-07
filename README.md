# Paccelli Lanches 🍔

Sistema web de uma hamburgueria: o cliente monta o carrinho com os lanches do cardápio, finaliza o pedido
e acompanha o status; o administrador gerencia cardápio, promoções, pedidos e usuários, e consulta o log
de auditoria.

Projeto prático da disciplina **Programação para Web III** — Técnico Integrado em Informática para Internet,
IFG Campus Luziânia.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Front-end | HTML, CSS e JavaScript (templates Qute renderizados no servidor + `fetch` para as ações) |
| Back-end | Java 21, Quarkus 3.36, JAX-RS (Quarkus REST), Hibernate ORM com Panache |
| Banco de dados | PostgreSQL 16 (Docker) |
| Segurança | Sessão por cookie `HttpOnly`, senhas com hash BCrypt |

## Arquitetura (MVC em camadas)

```
navegador ──► Controller (JAX-RS) ──► BO (regras) ──► DAO (JPQL) ──► Entity ──► PostgreSQL
                  │                      │
                  └── Template (View) ◄──┘ DTO
```

| Pacote | Papel |
|---|---|
| `entity` | Estrutura dos dados persistidos (`@Entity`): `Usuario`, `TipoUsuario`, `Lanche`, `Promocao`, `ItemCarrinho`, `Pedido`, `ItemPedido`, `AuditLog` |
| `dao` | Um DAO por entidade (`PanacheRepository`), com as consultas em JPQL/HQL |
| `bo` | **Todas** as regras de negócio e validações (ex.: `PedidoBO`, `UsuarioBO`, `SenhaBO`, `AuditoriaBO`) |
| `dto` | Dados que trafegam entre back-end e front-end; nenhuma entidade é enviada à tela |
| `controller` | Endpoints REST (JAX-RS) e montagem das telas com os templates Qute |
| `audit` | Filtro que registra todas as requisições no log de auditoria |
| `bootstrap` | Dados iniciais (tipos de usuário, usuários, cardápio e promoções) |
| `templates/` e `META-INF/resources/` | View: páginas HTML, CSS e JavaScript |

## Requisitos atendidos

| # | Requisito | Como foi implementado |
|---|---|---|
| 1 | Autenticar usuário | Login por e-mail e senha; o `AutenticacaoFilter` redireciona para `/login` quem não tem sessão (só login e cadastro são públicos) |
| 2 | Manter usuário | Autocadastro, cadastro/edição/exclusão pelo admin, edição do próprio perfil e troca de senha |
| 3 | Perfil de usuário | Papéis `ADMIN` e `COMUM` (entidade `TipoUsuario`), com os recursos de cada um na tabela abaixo |
| 4 | Navegação | Menu em todas as telas (com submenu Admin e menu para celular) e botões de voltar |
| 5 | Casos de uso do domínio | **Compra** (carrinho → pedido, qualquer usuário logado) e **Gestão** de cardápio, promoções e pedidos (somente admin) |
| 6 | Auditoria | Toda requisição gera um registro com data/hora, usuário, ação, requisição, resultado e erro |

### Permissões por perfil

| Recurso | Sem login | COMUM | ADMIN |
|---|:-:|:-:|:-:|
| Login e criação de conta | ✅ | ✅ | ✅ |
| Início, cardápio, promoções, contato, perfil | ❌ | ✅ | ✅ |
| Carrinho, finalizar pedido, meus pedidos | ❌ | ✅ | ✅ |
| Gestão de usuários, cardápio, promoções e pedidos | ❌ | ❌ | ✅ |
| Log de auditoria | ❌ | ❌ | ✅ |

### Principais regras de negócio

- **Pedido:** só com carrinho não vazio; cada item guarda o nome e o preço do momento da compra (com promoção);
  o status segue *Recebido → Em preparo → Saiu para entrega → Entregue*, não volta para uma etapa anterior,
  pode ser *Cancelado* antes da entrega e não muda depois de encerrado.
- **Promoção:** vale entre as datas de início e fim; o preço promocional precisa ser menor que o normal.
- **Usuário:** nome de 3 a 100 caracteres, e-mail válido e único, CPF com dígitos verificadores e único,
  telefone com DDD, data de nascimento não futura, senha com no mínimo 6 caracteres; o admin não pode
  excluir a si mesmo nem tirar a própria permissão de administrador.

## Como executar

Pré-requisitos: **Java 21** e **Docker**.

```bash
# 1. Sobe o PostgreSQL (banco paccelli_lanches, usuário/senha postgres)
docker compose -f docker.yaml up -d

# 2. Sobe a aplicação em modo de desenvolvimento
./mvnw quarkus:dev
```

Acesse **http://localhost:8080**. As tabelas são criadas automaticamente pelo Hibernate, e na primeira
execução são cadastrados os tipos de usuário, os usuários iniciais, o cardápio e duas promoções.

### Usuários iniciais

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | `admin@paccellilanches.com` | `admin123` |
| Cliente (comum) | `cliente@paccellilanches.com` | `cliente123` |

## Testes

```bash
./mvnw test
```

Os testes sobem a aplicação e usam o mesmo PostgreSQL (precisa estar rodando). Cobrem:

- páginas protegidas e rotas públicas (`PageResourceTest`);
- permissões de cliente e admin e o fluxo do carrinho (`PermissoesECarrinhoTest`);
- validações do cadastro de usuário (`UsuarioValidacaoTest`);
- hash de senha e login (`SenhaTest`);
- registros de auditoria (`AuditoriaTest`).
