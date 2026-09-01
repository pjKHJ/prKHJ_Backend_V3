package dsm.prkhj.global.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    public void save(String tokenId, Long userId) {
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + tokenId, String.valueOf(userId), Duration.ofSeconds(refreshTokenValiditySeconds));
    }

    public Long findUserId(String tokenId) {
        String userId = redisTemplate.opsForValue().get(KEY_PREFIX + tokenId);
        return (userId == null) ? null : Long.valueOf(userId);
    }

    public void delete(String tokenId) {
        redisTemplate.delete(KEY_PREFIX + tokenId);
    }

    private static final String KEY_PREFIX = "auth:refresh:";
}
