package com.ticket.core.domain.queue.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class QueueRedisRepositoryImpl implements QueueRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String WAIT_QUEUE_PREFIX = "queue:wait:";
    private static final String ACTIVE_QUEUE_PREFIX = "queue:active:";

    public QueueRedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // -----------------------------------------------------------------------
    // Key helpers
    // -----------------------------------------------------------------------

    private String getWaitKey(Long concertId) {
        return WAIT_QUEUE_PREFIX + concertId;
    }
    
    private String getActiveKey(Long concertId) {
        return ACTIVE_QUEUE_PREFIX + concertId;
    }

    // -----------------------------------------------------------------------
    // 대기열 (WAITING) - Redis Sorted Set, score = 진입 시각(ms)
    // -----------------------------------------------------------------------

    @Override
    public void addWaitQueue(Long concertId, String token, double timestampScore) {
        redisTemplate.opsForZSet().add(getWaitKey(concertId), token, timestampScore);
    }

    @Override
    public Optional<Long> getWaitRank(Long concertId, String token) {
        Long rank = redisTemplate.opsForZSet().rank(getWaitKey(concertId), token);
        return Optional.ofNullable(rank);
    }

    @Override
    public Set<String> popMinWaitQueue(Long concertId, long count) {
        Set<ZSetOperations.TypedTuple<Object>> popped = 
                redisTemplate.opsForZSet().popMin(getWaitKey(concertId), count);
        
        if (popped == null || popped.isEmpty()) {
            return Set.of();
        }
        
        return popped.stream()
                .map(tuple -> String.valueOf(tuple.getValue()))
                .collect(Collectors.toSet());
    }

    // -----------------------------------------------------------------------
    // 활성열 (ACTIVE) - Redis Sorted Set, score = 만료 시각(ms)
    //
    // 핵심 아이디어:
    //  - TTL을 Redis Key 단위가 아닌 ZSET의 score(만료 Timestamp)로 관리
    //  - Lazy Eviction: getActiveQueueSize()를 호출할 때 만료된 항목을
    //    ZREMRANGEBYSCORE으로 일괄 정리 → 별도 카운터나 SCAN 불필요
    // -----------------------------------------------------------------------

    @Override
    public void addActiveQueue(Long concertId, String token, long ttlSeconds) {
        // score = 현재 시각(ms) + TTL(ms) → 만료 시각
        double expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttlSeconds);
        redisTemplate.opsForZSet().add(getActiveKey(concertId), token, expireAt);
    }

    @Override
    public void addActiveQueue(Long concertId, Set<String> tokens, long ttlSeconds) {
        if (tokens == null || tokens.isEmpty()) return;

        double expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttlSeconds);
        String key = getActiveKey(concertId);
        for (String token : tokens) {
            redisTemplate.opsForZSet().add(key, token, expireAt);
        }
    }

    @Override
    public boolean isActiveToken(Long concertId, String token) {
        // ZSET에서 해당 토큰의 score(만료 시각)를 조회
        Double expireAt = redisTemplate.opsForZSet().score(getActiveKey(concertId), token);
        // score가 없으면 존재하지 않음, 있으면 만료 시각이 현재보다 미래인지 확인
        return expireAt != null && expireAt > System.currentTimeMillis();
    }

    @Override
    public void removeActiveQueue(Long concertId, String token) {
        // 결제 완료 또는 명시적 이탈 처리 시 즉시 제거
        redisTemplate.opsForZSet().remove(getActiveKey(concertId), token);
    }

    @Override
    public Long getActiveQueueSize(Long concertId) {
        // Lazy Eviction: 만료된 토큰 일괄 삭제 (score <= 현재 시각ms)
        redisTemplate.opsForZSet().removeRangeByScore(
                getActiveKey(concertId), 0, System.currentTimeMillis());

        // 정리 후 실제 활성 유저 수 반환
        Long size = redisTemplate.opsForZSet().zCard(getActiveKey(concertId));
        return size == null ? 0L : size;
    }
}
