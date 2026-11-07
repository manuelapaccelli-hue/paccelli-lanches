package br.com.paccellilanches;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;

@QuarkusTest
class PageResourceTest {

    @Test
    void testLoginPageIsPublic() {
        given()
          .accept("text/html")
          .when().get("/login")
          .then()
             .statusCode(200)
             .body(containsString("Entrar"));
    }

    @Test
    void testCadastroPageIsPublic() {
        given()
          .accept("text/html")
          .when().get("/cadastro")
          .then()
             .statusCode(200)
             .body(containsString("Cadastro"));
    }

    @Test
    void testSessionStatusIsPublic() {
        given()
          .when().get("/login/status")
          .then()
             .statusCode(200)
             .body(containsString("\"logado\":false"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/cardapio", "/promocoes", "/contato", "/carrinho", "/perfil",
            "/pedidos", "/usuarios", "/auditoria", "/gestao-cardapio", "/gestao-promocoes", "/gestao-pedidos"})
    void testPagesRedirectToLoginWithoutSession(String pagina) {
        given()
          .accept("text/html")
          .redirects().follow(false)
          .when().get(pagina)
          .then()
             .statusCode(303)
             .header("Location", endsWith("/login"));
    }

    @Test
    void testApiReturns401WithoutSession() {
        given()
          .accept("application/json")
          .when().get("/carrinho/itens")
          .then()
             .statusCode(401);
    }
}
