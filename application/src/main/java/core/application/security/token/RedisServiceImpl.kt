package core.application.security.token

import core.application.security.exception.ValueNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisServiceImpl(
    @param:Value("\${token.refresh.timeout}") private val refreshTimeout: Long,
    private val redisTemplate: RedisTemplate<String, Any?>
) : RedisService {
    private val timeUnit = TimeUnit.DAYS

    /**
     * Redis 값을 등록/수정
     *
     * @param {String}   key : redis key
     * @param {String}   value : redis value
     * @param {Long} timeout : redis 값 메모리 상의 유효시간
     * @param {TimeUnit} unit : 유효 시간의 단위
     * @return {void}
     */
    override fun setValueWithTTL(key: String, value: String) {
        val values = redisTemplate.opsForValue()
        values[key, value, refreshTimeout] = timeUnit
    }

    /**
     * Redis 키를 기반으로 값을 조회
     *
     * @param {String} key : redis key
     * @return {String} redis value 값 반환 or 미 존재시 빈 값 반환
     */
    override fun getValue(key: String): String? {
        val values = redisTemplate.opsForValue()
        if (values[key] == null) throw ValueNotFoundException(key + "와 매칭되는 Refresh Token을 찾을 수 없습니다.")
        return values[key].toString()
    }

    /**
     * Redis 키값을 기반으로 row 삭제
     *
     * @param {String} key : redis key
     */
    override fun deleteValue(key: String) {
        val result = redisTemplate.delete(key) // 삭제 결과를 Boolean으로 받음
    }
}
