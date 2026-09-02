package dsm.prkhj.global.security;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Component;

// users.github_access_token처럼 DB에 평문으로 두면 안 되는 값을 암호할 때 사용
@Component
public class TokenEncryptor {

    private final BytesEncryptor encryptor;

    public TokenEncryptor(
            @Value("${encryption.password}") String password,
            @Value("${encryption.salt}") String salt
    ) {
        this.encryptor = Encryptors.stronger(password, salt);
    }

    public byte[] encrypt(String plainText) {
        return encryptor.encrypt(plainText.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(byte[] cipherText) {
        return new String(encryptor.decrypt(cipherText), StandardCharsets.UTF_8);
    }
}
