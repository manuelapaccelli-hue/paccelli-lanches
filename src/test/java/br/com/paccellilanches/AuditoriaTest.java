package br.com.paccellilanches;

import br.com.paccellilanches.bo.AuditoriaBO;
import br.com.paccellilanches.dao.AuditLogDAO;
import br.com.paccellilanches.entity.AuditLog;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Log de auditoria: cada requisição grava usuário, ação legível, requisição, status e erro.
 */
@QuarkusTest
class AuditoriaTest {

    @Inject
    AuditLogDAO auditLogDAO;

    @Inject
    AuditoriaBO auditoriaBO;

    @Test
    void testLoginRecusadoRegistraEmailEErro() {
        given()
          .contentType("application/json")
          .body("{\"email\":\"admin@paccellilanches.com\",\"senha\":\"errada\"}")
          .when().post("/login")
          .then().statusCode(401);

        AuditLog log = ultimoLog();
        assertEquals("admin@paccellilanches.com", log.usuario);
        assertEquals("Login recusado", log.acao);
        assertEquals("POST /login", log.requisicao);
        assertEquals(401, log.status);
        assertEquals("E-mail ou senha inválidos.", log.detalhe);
    }

    @Test
    void testLoginAceitoRegistraEntrada() {
        given()
          .contentType("application/json")
          .body("{\"email\":\"admin@paccellilanches.com\",\"senha\":\"admin123\"}")
          .when().post("/login")
          .then().statusCode(200);

        AuditLog log = ultimoLog();
        assertEquals("admin@paccellilanches.com", log.usuario);
        assertEquals("Entrou no sistema", log.acao);
        assertEquals(200, log.status);
    }

    @Test
    void testAcessoBarradoRegistraAnonimo() {
        given()
          .accept("text/html")
          .redirects().follow(false)
          .when().get("/cardapio")
          .then().statusCode(303);

        AuditLog log = ultimoLog();
        assertEquals("Anônimo", log.usuario);
        assertEquals("Acessou o cardápio", log.acao);
        assertEquals("GET /cardapio", log.requisicao);
        assertEquals(303, log.status);
    }

    @Test
    void testDescricaoDasRotas() {
        assertEquals("Alteração de status do pedido #7", auditoriaBO.descrever("PUT", "/gestao-pedidos/pedidos/7/status"));
        assertEquals("Acessou a página inicial", auditoriaBO.descrever("GET", "/"));
        assertEquals("GET /rota-inexistente", auditoriaBO.descrever("GET", "/rota-inexistente"));
    }

    private AuditLog ultimoLog() {
        return auditLogDAO.listarRecentes(1).get(0);
    }
}
