package br.com.paccellilanches;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Validações do UsuarioBO, exercitadas pelo autocadastro (POST /cadastro, rota pública).
 * Todos os casos são inválidos, então nada é gravado no banco. Os casos que não testam o CPF
 * não informam CPF, para não depender de quais CPFs já estão cadastrados.
 */
@QuarkusTest
class UsuarioValidacaoTest {

    private static final String AMANHA = LocalDate.now().plusDays(1).toString();

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', value = {
            "nome vazio          |                | novo@teste.com        | (61) 99999-9999 |                |            | Informe o nome.",
            "nome curto          | Ab             | novo@teste.com        | (61) 99999-9999 |                |            | O nome deve ter entre",
            "e-mail sem @        | Cliente Teste  | email-invalido        | (61) 99999-9999 |                |            | Informe um e-mail válido",
            "e-mail sem domínio  | Cliente Teste  | cliente@teste         | (61) 99999-9999 |                |            | Informe um e-mail válido",
            "e-mail repetido     | Cliente Teste  | cliente@paccellilanches.com | (61) 99999-9999 |          |            | Já existe uma conta cadastrada com este e-mail.",
            "sem telefone        | Cliente Teste  | novo@teste.com        |                 |                |            | Informe um telefone para contato.",
            "telefone curto      | Cliente Teste  | novo@teste.com        | 1234-5678       |                |            | Informe um telefone válido",
            "celular sem 9       | Cliente Teste  | novo@teste.com        | (61) 89999-9999 |                |            | Informe um telefone válido",
            "CPF dígito errado   | Cliente Teste  | novo@teste.com        | (61) 99999-9999 | 529.982.247-24 |            | CPF inválido.",
            "CPF repetido 1      | Cliente Teste  | novo@teste.com        | (61) 99999-9999 | 111.111.111-11 |            | CPF inválido.",
            "CPF curto           | Cliente Teste  | novo@teste.com        | (61) 99999-9999 | 123.456        |            | CPF inválido.",
            "nascimento futuro   | Cliente Teste  | novo@teste.com        | (61) 99999-9999 |                | AMANHA     | não pode ser uma data futura.",
            "nascimento antigo   | Cliente Teste  | novo@teste.com        | (61) 99999-9999 |                | 1800-01-01 | Informe uma data de nascimento válida.",
            "senha curta         | Cliente Teste  | novo@teste.com        | (61) 99999-9999 |                | 2000-05-10 | A senha deve ter pelo menos 6 caracteres."
    })
    void testCadastroInvalido(String caso, String nome, String email, String telefone, String cpf,
                              String dataNascimento, String mensagem) {
        String data = "AMANHA".equals(dataNascimento) ? AMANHA : dataNascimento;
        String senha = caso.equals("senha curta") ? "123" : "senha123";

        given()
          .contentType("application/json")
          .body(json(nome, email, telefone, cpf, data, senha))
          .when().post("/cadastro")
          .then()
             .statusCode(400)
             .body(containsString(mensagem));
    }

    private static String json(String nome, String email, String telefone, String cpf, String data, String senha) {
        return "{" + campo("nome", nome) + "," + campo("email", email) + "," + campo("telefone", telefone) + ","
                + campo("cpf", cpf) + "," + campo("dataNascimento", data) + "," + campo("senha", senha) + "}";
    }

    private static String campo(String nome, String valor) {
        return "\"" + nome + "\":" + (valor == null ? "null" : "\"" + valor + "\"");
    }
}
