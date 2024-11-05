package core.application.config.mybatis;

import org.springframework.context.annotation.*;

@Configuration
public class MyBatisConfig {

    @Bean
    public UUIDTypeHandler uuidTypeHandler() {
        return new UUIDTypeHandler();
    }

}
