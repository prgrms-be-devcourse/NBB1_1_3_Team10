package core.application.security.token;

import core.application.security.exception.ValueNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final Long refreshTimeout;

    private final TimeUnit timeUnit = TimeUnit.DAYS;

    public RedisServiceImpl(@Value("${token.refresh.timeout}") Long refreshTimeout,
                            RedisTemplate<String, Object> redisTemplate) {
        this.refreshTimeout = refreshTimeout;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Redis 값을 등록/수정
     *
     * @param {String}   key : redis key
     * @param {String}   value : redis value
     * @param {Long} timeout : redis 값 메모리 상의 유효시간
     * @param {TimeUnit} unit : 유효 시간의 단위
     * @return {void}
     */

    @Override
    public void setValueWithTTL(String key, String value) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, value, refreshTimeout, timeUnit);
    }

    /**
     * Redis 키를 기반으로 값을 조회
     *
     * @param {String} key : redis key
     * @return {String} redis value 값 반환 or 미 존재시 빈 값 반환
     */
    @Override
    public String getValue(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null)
            throw new ValueNotFoundException(key + "와 매칭되는 Refresh Token을 찾을 수 없습니다.");
        return String.valueOf(values.get(key));
    }

    /**
     * Redis 키값을 기반으로 row 삭제
     *
     * @param {String} key : redis key
     */
    @Override
    public void deleteValue(String key) {
        Boolean result = redisTemplate.delete(key); // 삭제 결과를 Boolean으로 받음
    }
}
