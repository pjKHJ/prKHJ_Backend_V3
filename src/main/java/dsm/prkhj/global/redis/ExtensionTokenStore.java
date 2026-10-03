package dsm.prkhj.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExtensionTokenStore {

    private static final String TOKEN_KEY_PREFIX = "ext:token:";
    private static final String USER_KEY_PREFIX = "ext:user:";

    private final StringRedisTemplate redisTemplate;

    public void save(String token, Long userId) {
        redisTemplate.opsForValue().set(TOKEN_KEY_PREFIX + token, String.valueOf(userId));

        // ext:user:{userId} 키에 새 토큰을 넣으면서 기존에 있었던 토큰을 꺼냄
        String oldToken = redisTemplate.opsForValue().getAndSet(USER_KEY_PREFIX + userId, token);

        if (oldToken != null && !oldToken.equals(token)) {
            redisTemplate.delete(TOKEN_KEY_PREFIX + oldToken);
        }
    }

    public void deleteByUserId(Long userId) {
        String token = redisTemplate.opsForValue().getAndDelete(USER_KEY_PREFIX + userId);

        if (token != null) {
            redisTemplate.delete(TOKEN_KEY_PREFIX + token);
        }
    }

    public Long findUserId(String token) {
        String userId = redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token);
        return (userId == null) ? null : Long.valueOf(userId);
    }

}
