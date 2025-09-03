//package ir.dotin.llmproxyserver.service;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import ir.dotin.llmproxyserver.model.AuthRequest;
//import ir.dotin.llmproxyserver.model.SessionToken;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class AuthService {
//
//    @Value("${gapgpt.jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${gapgpt.jwt.expiration}")
//    private Long jwtExpiration;
//
//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
//    }
//
//    public SessionToken createSession(AuthRequest request) {
//        log.info("Creating session for user: {} from IDE: {}", request.getUserId(), request.getIdeType());
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", request.getUserId());
//        claims.put("ideType", request.getIdeType());
//        claims.put("ideVersion", request.getIdeVersion());
//        claims.put("pluginVersion", request.getPluginVersion());
//
//        String token = createToken(claims, request.getUserId());
//
//        return SessionToken.builder()
//                .token(token)
//                .expiresIn(jwtExpiration / 1000)
//                .userId(request.getUserId())
//                .build();
//    }
//
//    private String createToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    @Cacheable(value = "tokenValidation", key = "#token")
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser()
//                    .setSigningKey(getSigningKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            log.error("Token validation failed: ", e);
//            return false;
//        }
//    }
//
//    public Claims extractClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}
