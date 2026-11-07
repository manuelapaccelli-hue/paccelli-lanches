package br.com.paccellilanches;

import br.com.paccellilanches.dao.ItemCarrinhoDAO;
import br.com.paccellilanches.dao.LancheDAO;
import br.com.paccellilanches.dao.UsuarioDAO;
import br.com.paccellilanches.entity.Lanche;
import br.com.paccellilanches.entity.Usuario;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fluxo do cliente no carrinho e as permissões distintas entre cliente (COMUM) e administrador,
 * usando os usuários iniciais criados pelo UsuarioBootstrap.
 */
@QuarkusTest
class PermissoesECarrinhoTest {

    @Inject
    LancheDAO lancheDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    ItemCarrinhoDAO itemCarrinhoDAO;

    @ParameterizedTest
    @ValueSource(strings = {"/usuarios", "/auditoria", "/gestao-cardapio", "/gestao-promocoes", "/gestao-pedidos"})
    void testClienteVeAcessoNegadoNasTelasDeAdmin(String pagina) {
        given()
          .cookie("sessionId", login("cliente@paccellilanches.com", "cliente123"))
          .accept("text/html")
          .when().get(pagina)
          .then()
             .statusCode(403)
             .body(containsString("<title>Paccelli Lanches • Acesso negado</title>"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/usuarios", "/auditoria", "/gestao-cardapio", "/gestao-promocoes", "/gestao-pedidos"})
    void testAdminAbreAsTelasDeAdmin(String pagina) {
        given()
          .cookie("sessionId", login("admin@paccellilanches.com", "admin123"))
          .accept("text/html")
          .when().get(pagina)
          .then()
             .statusCode(200)
             // pelo título: a própria auditoria pode listar ações com o resultado "Acesso negado"
             .body(not(containsString("<title>Paccelli Lanches • Acesso negado</title>")));
    }

    @Test
    void testClienteNaoAlteraDadosDeAdminPelaApi() {
        String sessao = login("cliente@paccellilanches.com", "cliente123");

        given().cookie("sessionId", sessao)
          .contentType("application/json")
          .body("{\"nome\":\"X\",\"email\":\"x@x.com\",\"tipo\":\"ADMIN\",\"senha\":\"abcdef\"}")
          .when().post("/usuarios")
          .then().statusCode(403);

        given().cookie("sessionId", sessao)
          .contentType("application/json")
          .body("{\"status\":\"ENTREGUE\"}")
          .when().put("/gestao-pedidos/pedidos/1/status")
          .then().statusCode(403);
    }

    @Test
    void testClienteAdicionaConsultaERemoveDoCarrinho() {
        String sessao = login("cliente@paccellilanches.com", "cliente123");
        Usuario cliente = usuarioDAO.buscarAtivoPorEmail("cliente@paccellilanches.com").orElseThrow();
        Lanche lanche = lancheDAO.listarAtivos().stream()
                .filter(candidato -> itemCarrinhoDAO.buscar(cliente, candidato.id).isEmpty())
                .findFirst()
                .orElseThrow();

        given().cookie("sessionId", sessao)
          .contentType("application/json")
          .body("{\"lancheId\":" + lanche.id + "}")
          .when().post("/carrinho/itens")
          .then().statusCode(204);

        given().cookie("sessionId", sessao)
          .contentType("application/json")
          .body("{\"quantidade\":3}")
          .when().put("/carrinho/itens/" + lanche.id)
          .then().statusCode(204);

        given().cookie("sessionId", sessao)
          .when().get("/carrinho/itens")
          .then()
             .statusCode(200)
             .body(containsString("\"nome\":\"" + lanche.nome + "\""))
             .body(containsString("\"quantidade\":3"));

        given().cookie("sessionId", sessao)
          .when().delete("/carrinho/itens/" + lanche.id)
          .then().statusCode(204);

        assertTrue(itemCarrinhoDAO.buscar(cliente, lanche.id).isEmpty());
    }

    @Test
    void testCarrinhoExigeLogin() {
        given()
          .contentType("application/json")
          .body("{\"lancheId\":1}")
          .when().post("/carrinho/itens")
          .then().statusCode(401);
    }

    /** Faz login e devolve o id da sessão (cookie sessionId). */
    private String login(String email, String senha) {
        return given()
          .contentType("application/json")
          .body("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}")
          .when().post("/login")
          .then().statusCode(200)
          .extract().cookie("sessionId");
    }
}
