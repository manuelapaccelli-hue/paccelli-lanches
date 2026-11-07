package br.com.paccellilanches.dao;

import br.com.paccellilanches.entity.Pedido;
import br.com.paccellilanches.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PedidoDAO implements PanacheRepository<Pedido> {

    public void salvar(Pedido pedido) {
        persist(pedido);
    }

    /** Pedidos do usuário com os itens já carregados (join fetch), dos mais recentes aos mais antigos. */
    public List<Pedido> listarDoUsuario(Usuario usuario) {
        return list("select distinct p from Pedido p left join fetch p.itens "
                + "where p.usuario = ?1 order by p.dataHora desc", usuario);
    }

    /** Todos os pedidos, com cliente e itens carregados, dos mais recentes aos mais antigos. */
    public List<Pedido> listarTodos() {
        return list("select distinct p from Pedido p join fetch p.usuario left join fetch p.itens "
                + "order by p.dataHora desc");
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return findByIdOptional(id);
    }
}
