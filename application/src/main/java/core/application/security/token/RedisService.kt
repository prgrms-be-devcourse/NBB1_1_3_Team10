package core.application.security.token

interface RedisService {
    fun setValueWithTTL(key: String, value: String)

    fun getValue(key: String): String?

    fun deleteValue(key: String)
}
