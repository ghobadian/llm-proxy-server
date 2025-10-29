package ir.dotin.llmproxyserver.controller;

import ir.dotin.llmproxyserver.security.JwtTokenProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody LoginRequest loginRequest) {
        // In production, verify credentials against database
        // This is a simplified example
        if (isValidUser(loginRequest.getUsername(), loginRequest.getPassword())) {
            String token = jwtTokenProvider.generateToken(loginRequest.getUsername(), "USER");
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", loginRequest.getUsername());
            
            return Mono.just(ResponseEntity.ok(response));
        }
        
        return Mono.just(ResponseEntity.status(401).build());
    }

    private boolean isValidUser(String username, String password) {
        // Implement actual authentication logic here
        // Check against database, LDAP, etc.
        return true; // Placeholder
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
