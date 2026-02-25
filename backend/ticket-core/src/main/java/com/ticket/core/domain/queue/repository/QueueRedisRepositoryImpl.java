package com.ticket.core.domain.queue.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class QueueRedisRepositoryImpl implements QueueRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String WAIT_QUEUE_PREFIX = "queue:wait:";
    private static final String ACTIVE_QUEUE_PREFIX = "queue:active:";

    public QueueRedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getWaitKey(Long concertId) {
        return WAIT_QUEUE_PREFIX + concertId;
    }
    
    private String getActiveKey(Long concertId) {
        return ACTIVE_QUEUE_PREFIX + concertId;
    }

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

    @Override
    public void addActiveQueue(Long concertId, String token) {
        redisTemplate.opsForSet().add(getActiveKey(concertId), token);
    }

    @Override
    public void addActiveQueue(Long concertId, Set<String> tokens) {
        if (tokens.isEmpty()) return;
        
        Object[] tokenArray = tokens.toArray();
        redisTemplate.opsForSet().add(getActiveKey(concertId), tokenArray);
    }

    @Override
    public boolean isActiveToken(Long concertId, String token) {
        Boolean isMember = redisTemplate.opsForSet().isMember(getActiveKey(concertId), token);
        return Boolean.TRUE.equals(isMember);
    }

    @Override
    public void removeActiveQueue(Long concertId, String token) {
        redisTemplate.opsForSet().remove(getActiveKey(concertId), token);
    }

    @Override
    public Long getActiveQueueSize(Long concertId) {
        Long size = redisTemplate.opsForSet().size(getActiveKey(concertId));
        return size == null ? 0L : size;
    }
}
