package br.com.paccellilanches;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class PageResourceTest {

    @Test
    void testIndexPage() {
        given()
          .when().get("/")
          .then()
             .statusCode(200)
             .body(containsString("Paccelli Lanches"));
    }

    @Test
    void testLoginPage() {
        given()
          .when().get("/login")
          .then()
             .statusCode(200)
             .body(containsString("Entrar"));
    }

    @Test
    void testCardapioPage() {
        given()
          .when().get("/cardapio")
          .then()
             .statusCode(200)
             .body(containsString("Cardápio"));
    }

    @Test
    void testPromocoesPage() {
        given()
          .when().get("/promocoes")
          .then()
             .statusCode(200)
             .body(containsString("Promoções"));
    }

    @Test
    void testContatoPage() {
        given()
          .when().get("/contato")
          .then()
             .statusCode(200)
             .body(containsString("Contato"));
    }

    @Test
    void testCadastroPage() {
        given()
          .when().get("/cadastro")
          .then()
             .statusCode(200)
             .body(containsString("Cadastro"));
    }
}
