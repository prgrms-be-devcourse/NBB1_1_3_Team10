package core.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	@Value("${kmdb.api.url}")
	private String apiUrl;

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.baseUrl(apiUrl)
			.codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(10*1024*1024))
			.build();
	}
}
