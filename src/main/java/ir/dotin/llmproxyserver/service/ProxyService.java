//package ir.dotin.llmproxyserver.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.Map;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class ProxyService {
//
//    @Value("${gapgpt.api.key}")
//    private String apiKey;
//
//    @Value("${gapgpt.api.base-url}")
//    private String apiBaseUrl;
//
//    private final AuthService authService;
//    private final RateLimitService rateLimitService;
//    private final WebClient.Builder webClientBuilder;
//
//    public Mono<ResponseEntity<String>> forwardRequest(
//            String path,
//            String method,
//            Map<String, String> headers,
//            String body) {
//
//        String token = headers.get("authorization").replace("Bearer ", "");
//
//        // Validate token
//        if (!authService.validateToken(token)) {
//            return Mono.just(ResponseEntity.status(401).body("Invalid token"));
//        }
//
//        // Extract user info from token
//        String userId = authService.extractClaims(token).getSubject();
//
//        // Check rate limit
//        if (!rateLimitService.allowRequest(userId)) {
//            return Mono.just(ResponseEntity.status(429).body("Rate limit exceeded"));
//        }
//
//        log.info("Proxying request for user {} to path: {}", userId, path);
//
//        WebClient webClient = webClientBuilder
//                .baseUrl(apiBaseUrl)
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                .build();
//
//        WebClient.RequestBodySpec requestSpec = webClient
//                .method(HttpMethod.valueOf(method))
//                .uri(path)
//                .contentType(MediaType.APPLICATION_JSON);
//
//        // Forward relevant headers
//        headers.forEach((key, value) -> {
//            if (!key.equalsIgnoreCase("authorization") &&
//                !key.equalsIgnoreCase("host")) {
//                requestSpec.header(key, value);
//            }
//        });
//
//        if (body != null && !body.isEmpty()) {
//            requestSpec.bodyValue(body);
//        }
//
//        return requestSpec
//                .exchangeToMono(response -> {
//                    return response.bodyToMono(String.class)
//                            .map(responseBody -> ResponseEntity
//                                    .status(response.statusCode())
//                                    .headers(httpHeaders -> {
//                                        response.headers().asHttpHeaders().forEach((name, values) -> {
//                                            if (!name.equalsIgnoreCase("transfer-encoding")) {
//                                                httpHeaders.addAll(name, values);
//                                            }
//                                        });
//                                    })
//                                    .body(responseBody));
//                })
//                .onErrorResume(error -> {
//                    log.error("Error proxying request: ", error);
//                    return Mono.just(ResponseEntity.status(502).body("Bad Gateway"));
//                });
//    }
//}
