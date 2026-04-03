package es.leinadfonfria.eyteacher.ai.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-user rate limiting using the token-bucket algorithm.
 *
 * <p>Each user gets a single {@link Bucket} configured with two independent
 * bandwidth limits: one per hour and one per day. A request is allowed only
 * when both limits have available tokens.</p>
 *
 * <p>Buckets are stored in memory and will reset if the application restarts.</p>
 */
@Service
public class RateLimitService {

    private final int requestsPerHour;
    private final int requestsPerDay;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Creates a {@code RateLimitService} with the configured bandwidth limits.
     *
     * @param requestsPerHour maximum number of requests allowed per user per hour
     * @param requestsPerDay  maximum number of requests allowed per user per day
     */
    public RateLimitService(
            @Value("${rate-limit.requests-per-hour}") int requestsPerHour,
            @Value("${rate-limit.requests-per-day}") int requestsPerDay) {
        this.requestsPerHour = requestsPerHour;
        this.requestsPerDay = requestsPerDay;
    }

    /**
     * Attempts to consume one token from the given user's bucket.
     *
     * <p>Returns {@code true} if both the hourly and daily limits have tokens
     * available, consuming one token from each. Returns {@code false} if either
     * limit is exhausted, without consuming any token.</p>
     *
     * @param userId the identifier of the user making the request
     * @return {@code true} if the request is within rate limits, {@code false} otherwise
     */
    public boolean tryConsume(String userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, this::createBucket);
        return bucket.tryConsume(1);
    }

    /**
     * Creates a new {@link Bucket} for the given user with both bandwidth limits applied.
     *
     * @param userId the user identifier (unused in bucket creation, required by {@code computeIfAbsent})
     * @return a new bucket with hourly and daily bandwidth limits
     */
    private Bucket createBucket(String userId) {
        Bandwidth hourlyLimit = Bandwidth.builder()
                .capacity(requestsPerHour)
                .refillIntervally(requestsPerHour, Duration.ofHours(1))
                .build();

        Bandwidth dailyLimit = Bandwidth.builder()
                .capacity(requestsPerDay)
                .refillIntervally(requestsPerDay, Duration.ofDays(1))
                .build();

        return Bucket.builder()
                .addLimit(hourlyLimit)
                .addLimit(dailyLimit)
                .build();
    }
}
