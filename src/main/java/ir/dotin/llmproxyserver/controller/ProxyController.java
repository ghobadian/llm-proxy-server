package ir.dotin.llmproxyserver.controller;

import ir.dotin.llmproxyserver.model.dto.ChatRequest;
import ir.dotin.llmproxyserver.model.dto.CompletionRequest;
import ir.dotin.llmproxyserver.service.DeepseekService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProxyController {

    private final DeepseekService deepseekService;

    @PostMapping(value = "/chat/completions")
public Mono<ResponseEntity<Object>> handleChatCompletion(
        @RequestBody ChatRequest request,
        @RequestHeader(value = "Accept", required = false) String acceptHeader,
        @RequestHeader(value = "Authorization", required = false) String authorization) {

    log.info("Received chat completion request for model: {}, stream: {}, Accept: {}",
            request.getModel(), request.getStream(), acceptHeader);

    if (authorization != null) {
        log.debug("Received Authorization header (will be ignored): {}",
                authorization.substring(0, Math.min(authorization.length(), 20)) + "...");
    }

    if (Boolean.TRUE.equals(request.getStream())) {
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(deepseekService.streamChatCompletion(request)));
    } else {
        return deepseekService.chatCompletion(request)
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));
    }
}


    @PostMapping(value = "/beta/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFimCompletion(
            @RequestBody CompletionRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        log.info("Received FIM completion request for model: {}", request.getModel());

        if (authorization != null) {
            log.debug("Received Authorization header (will be ignored)");
        }

        return deepseekService.streamFimCompletion(request);
    }

    @GetMapping("/models")
    public Mono<Object> listModels(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        log.info("Received list models request");

        if (authorization != null) {
            log.debug("Received Authorization header (will be ignored)");
        }

        return deepseekService.listModels();
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("OK");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error handling request", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
    }
}
