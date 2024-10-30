package core.application.security.token;

public interface RedisService {
    void setValueWithTTL(String key, String value);

    String getValue(String key);

    void deleteValue(String key);
}
