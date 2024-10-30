package core.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 * Redis 연결을 위해 RedisTemplate 및 직렬화 방식을 설정
 */
@Configuration
public class RedisConfig {

    // Redis 서버 호스트 주소
    @Value("${spring.data.redis.host}")
    private String host;

    // Redis 서버 포트 번호
    @Value("${spring.data.redis.port}")
    private int port;

    // Redis 서버 접속 비밀번호
    @Value("${spring.data.redis.password}")
    private String password;

    /**
     * Redis 서버 연결을 위해 Connection Factory를 생성
     *
     * @return RedisConnectionFactory LettuceConnectionFactory로 구성된 Redis Connection Factory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(RedisPassword.of(password)); // 비밀번호 설정
        return new LettuceConnectionFactory(config);
    }

    /**
     * RedisTemplate을 설정하는 빈(Bean) 생성 메서드
     * 데이터를 직렬화하여 Redis에 저장하며, String을 키로, Object를 값으로 사용
     *
     * @return RedisTemplate<String, Object> - key는 String, value는 Object로 설정된 Redis 템플릿
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // Redis 서버 연결 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key, Value 각각 직렬화 설정 (Key: String, Value: JSON)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}

