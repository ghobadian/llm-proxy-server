package ir.dotin.llmproxyserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    
    @Value("${deepseek.base-url}")
    private String deepseekBaseUrl;
    
    @Bean
    public WebClient deepseekWebClient() {
        return WebClient.builder()
                .baseUrl(deepseekBaseUrl)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }
}