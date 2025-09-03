package ir.dotin.llmproxyserver.service;

import ir.dotin.llmproxyserver.model.dto.ChatRequest;
import ir.dotin.llmproxyserver.model.dto.CompletionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepseekService {
    
    private final WebClient deepseekWebClient;
    
    @Value("${deepseek.api-key}")
    private String apiKey;
    
    public Flux<String> streamChatCompletion(ChatRequest request) {
        log.debug("Forwarding chat completion request to DeepSeek");
        
        return deepseekWebClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(chunk -> log.debug("Received chunk: {}", chunk))
                .doOnError(error -> log.error("Error in chat completion stream", error));
    }
    
    public Mono<Object> chatCompletion(ChatRequest request) {
        log.debug("Forwarding non-streaming chat completion request to DeepSeek");
        
        return deepseekWebClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(error -> log.error("Error in chat completion", error));
    }
    
    public Flux<String> streamFimCompletion(CompletionRequest request) {
        log.debug("Forwarding FIM completion request to DeepSeek");
        
        return deepseekWebClient.post()
                .uri("/beta/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnError(error -> log.error("Error in FIM completion stream", error));
    }
    
    public Mono<Object> listModels() {
        log.debug("Forwarding list models request to DeepSeek");
        
        return deepseekWebClient.get()
                .uri("/models")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(error -> log.error("Error listing models", error));
    }
}
