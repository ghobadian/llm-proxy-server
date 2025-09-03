//package ir.dotin.llmproxyserver.controller;
//
//import ir.dotin.llmproxyserver.model.AuthRequest;
//import ir.dotin.llmproxyserver.model.SessionToken;
//import ir.dotin.llmproxyserver.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/v1/auth")
//@RequiredArgsConstructor
//@Slf4j
//public class AuthController {
//
//    private final AuthService authService;
//
//    @PostMapping("/session")
//    public ResponseEntity<SessionToken> createSession(@RequestBody AuthRequest request) {
//        log.debug("Received session creation request: {}", request);
//        SessionToken token = authService.createSession(request);
//        return ResponseEntity.ok(token);
//    }
//
//    @PostMapping("/validate")
//    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
//        log.debug("Received token validation request: {}", token);
//        boolean isValid = authService.validateToken(token.replace("Bearer ", ""));
//        return ResponseEntity.ok(isValid);
//    }
//}
