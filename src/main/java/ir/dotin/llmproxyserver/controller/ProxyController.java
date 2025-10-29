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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProxyController {

    private final DeepseekService deepseekService;

    @PostMapping(value = "/chat/completions")
    public Object handleChatCompletion(
        @RequestBody ChatRequest request,
        @RequestHeader(value = "Accept", required = false) String acceptHeader,
        @RequestHeader(value = "Authorization", required = false) String authorization) {


        log.info("Received chat completion request for model: {}, stream: {}, Accept: {}, request: {}",
                request.getModel(), request.getStream(), acceptHeader, request);

        if (authorization != null) {
            log.debug("Received Authorization header (will be ignored): {}",
                    authorization);
    //                authorization.substring(0, Math.min(authorization.length(), 20)) + "...");
        }

        if (Boolean.TRUE.equals(request.getStream())) {
            return streamChatCompletion(request);
        } else {
            return deepseekService.chatCompletion(request)
                    .map(response -> ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(response));
        }
    }

    private SseEmitter streamChatCompletion(ChatRequest request) {
        SseEmitter emitter = new SseEmitter(300_000L);

        CompletableFuture.runAsync(() -> {
            try {
                deepseekService.streamChatCompletion(request)
                    .doOnNext(chunk -> log.debug("Sending chunk to client"))
                    .doOnError(error -> log.error("Error in stream", error))
                    .doOnComplete(() -> log.debug("Stream completed"))
                    .subscribe(
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                log.error("Error sending chunk", e);
                                emitter.completeWithError(e);
                            }
                        },
                        error -> {
                            log.error("Stream error", error);
                            emitter.completeWithError(error);
                        },
                        () -> {
                            log.debug("Completing emitter");
                            emitter.complete();
                        }
                    );
            } catch (Exception e) {
                log.error("Exception starting stream", e);
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> {
            log.warn("SSE connection timeout");
            emitter.complete();
        });

        emitter.onCompletion(() -> log.debug("SSE connection completed"));

        emitter.onError(throwable -> log.error("SSE error", throwable));

        return emitter;
    }

    @PostMapping(value = "/beta/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamFimCompletion(
            @RequestBody CompletionRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        log.info("Received FIM completion request for model: {}", request.getModel());

        if (authorization != null) {
            log.debug("Received Authorization header (will be ignored)");
        }

        SseEmitter emitter = new SseEmitter(300_000L);

        CompletableFuture.runAsync(() -> {
            try {
                deepseekService.streamFimCompletion(request)
                    .subscribe(
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                log.error("Error sending FIM chunk", e);
                                emitter.completeWithError(e);
                            }
                        },
                        error -> {
                            log.error("FIM stream error", error);
                            emitter.completeWithError(error);
                        },
                        () -> emitter.complete()
                    );
            } catch (Exception e) {
                log.error("Exception starting FIM stream", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @GetMapping("/models")
    public Mono<ResponseEntity<Object>> listModels(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        log.info("Received list models request");

        if (authorization != null) {
            log.debug("Received Authorization header (will be ignored)");
        }

        return deepseekService.listModels()
                .map(models -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(models));
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<String>> health() {
        return Mono.just(ResponseEntity.ok("OK"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error handling request", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Error: " + e.getMessage());
    }
}
