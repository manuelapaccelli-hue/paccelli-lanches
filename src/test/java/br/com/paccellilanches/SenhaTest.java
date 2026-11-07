package br.com.paccellilanches;

import br.com.paccellilanches.bo.SenhaBO;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Hash de senha (SenhaBO) e login com as senhas já convertidas para BCrypt.
 */
@QuarkusTest
class SenhaTest {

    @Inject
    SenhaBO senhaBO;

    @Test
    void testHashNaoGuardaASenhaEConfere() {
        String hash = senhaBO.gerarHash("minhaSenha123");

        assertTrue(senhaBO.ehHash(hash));
        assertFalse(hash.contains("minhaSenha123"));
        assertTrue(senhaBO.confere("minhaSenha123", hash));
        assertFalse(senhaBO.confere("outraSenha", hash));
        assertFalse(senhaBO.confere(null, hash));
    }

    @Test
    void testMesmaSenhaGeraHashesDiferentes() {
        // cada hash tem um salt aleatório, então duas pessoas com a mesma senha não têm o mesmo hash
        assertNotEquals(senhaBO.gerarHash("mesmaSenha"), senhaBO.gerarHash("mesmaSenha"));
    }

    @Test
    void testTextoPuroNaoEhHash() {
        assertFalse(senhaBO.ehHash("manuela123"));
        assertFalse(senhaBO.confere("manuela123", "manuela123"));
    }

    @Test
    void testLoginComSenhaCorreta() {
        given()
          .contentType("application/json")
          .body("{\"email\":\"admin@paccellilanches.com\",\"senha\":\"admin123\"}")
          .when().post("/login")
          .then()
             .statusCode(200);
    }

    @Test
    void testLoginComSenhaErrada() {
        given()
          .contentType("application/json")
          .body("{\"email\":\"admin@paccellilanches.com\",\"senha\":\"errada\"}")
          .when().post("/login")
          .then()
             .statusCode(401);
    }
}
