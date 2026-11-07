package br.com.paccellilanches.bo;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Senhas nunca são gravadas como foram digitadas: o banco guarda só o hash BCrypt
 * (ex.: "$2a$10$..."), que inclui um salt aleatório. Para conferir uma senha, o BCrypt
 * calcula o hash da senha digitada com o mesmo salt e compara; não dá para voltar do hash à senha.
 */
@ApplicationScoped
public class SenhaBO {

    /** Hash BCrypt da senha (custo 10, o padrão do BcryptUtil). */
    public String gerarHash(String senha) {
        return BcryptUtil.bcryptHash(senha);
    }

    /** Se a senha digitada corresponde ao hash gravado. */
    public boolean confere(String senhaDigitada, String hashGravado) {
        if (senhaDigitada == null || !ehHash(hashGravado)) {
            return false;
        }
        return BcryptUtil.matches(senhaDigitada, hashGravado);
    }

    /** Hashes BCrypt começam com $2a$, $2b$ ou $2y$; o que não começa assim é senha antiga em texto puro. */
    public boolean ehHash(String valor) {
        return valor != null && valor.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
}
