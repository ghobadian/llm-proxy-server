//package ir.dotin.llmproxyserver.service;
//
//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.Bucket;
//import io.github.bucket4j.Refill;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//@Slf4j
//public class RateLimitService {
//
//    @Value("${gapgpt.rate-limit.requests-per-minute}")
//    private int requestsPerMinute;
//
//    @Value("${gapgpt.rate-limit.requests-per-hour}")
//    private int requestsPerHour;
//
//    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
//
//    public boolean allowRequest(String userId) {
//        Bucket bucket = buckets.computeIfAbsent(userId, this::createNewBucket);
//        boolean allowed = bucket.tryConsume(1);
//
//        if (!allowed) {
//            log.warn("Rate limit exceeded for user: {}", userId);
//        }
//
//        return allowed;
//    }
//
//    private Bucket createNewBucket(String userId) {
//        Bandwidth minuteLimit = Bandwidth.classic(
//                requestsPerMinute,
//                Refill.intervally(requestsPerMinute, Duration.ofMinutes(1))
//        );
//
//        Bandwidth hourLimit = Bandwidth.classic(
//                requestsPerHour,
//                Refill.intervally(requestsPerHour, Duration.ofHours(1))
//        );
//
//        return Bucket.builder()
//                .addLimit(minuteLimit)
//                .addLimit(hourLimit)
//                .build();
//    }
//}
