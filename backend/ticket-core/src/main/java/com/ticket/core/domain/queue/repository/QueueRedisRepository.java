package com.ticket.core.domain.queue.repository;

import java.util.Optional;
import java.util.Set;

public interface QueueRedisRepository {
    
    // ------------------------------------------------------------------------
    // 대기열 (WAITING) - Redis Sorted Set
    // ------------------------------------------------------------------------
    
    /**
     * 대기열에 진입합니다. (현재 시간을 score로 사용)
     */
    void addWaitQueue(Long concertId, String token, double timestampScore);
    
    /**
     * 대기열에서의 순번을 조회합니다. (0부터 시작)
     */
    Optional<Long> getWaitRank(Long concertId, String token);
    
    /**
     * 대기열에서 가장 오래 대기한 N명을 가져오고 대기열에서 삭제합니다.
     */
    Set<String> popMinWaitQueue(Long concertId, long count);

    // ------------------------------------------------------------------------
    // 활성열 (ACTIVE) - Redis Set
    // ------------------------------------------------------------------------
    
    /**
     * 활성열에 토큰을 추가합니다.
     */
    void addActiveQueue(Long concertId, String token);
    
    /**
     * 활성열에 여러 토큰을 한 번에 추가합니다.
     */
    void addActiveQueue(Long concertId, Set<String> tokens);
    
    /**
     * 유효한 활성 토큰인지 확인합니다.
     */
    boolean isActiveToken(Long concertId, String token);
    
    /**
     * 활성열에서 토큰을 제거합니다. (만료 처리 혹은 예매 완료 후)
     */
    void removeActiveQueue(Long concertId, String token);
    
    /**
     * 활성열의 현재 인원 수를 조회합니다.
     */
    Long getActiveQueueSize(Long concertId);
}
