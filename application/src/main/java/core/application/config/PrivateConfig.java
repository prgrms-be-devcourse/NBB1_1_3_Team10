package core.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:privates/private.properties")
public class PrivateConfig {
    /*  privates/private.properties
      ${database.url}       :   DB url
      ${database.username}  :   DB 접속 ID
      ${database.password}  :   DB 접속 PW
     */
}
