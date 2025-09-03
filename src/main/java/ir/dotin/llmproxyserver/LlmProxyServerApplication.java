package ir.dotin.llmproxyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class LlmProxyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmProxyServerApplication.class, args);
    }

}
