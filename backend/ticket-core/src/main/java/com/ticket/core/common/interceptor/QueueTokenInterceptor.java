package com.ticket.core.common.interceptor;

import com.ticket.core.domain.queue.repository.QueueRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class QueueTokenInterceptor implements HandlerInterceptor {

    private final QueueRedisRepository queueRedisRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Queue-Token");
        String concertIdStr = request.getHeader("Concert-Id");

        if (token == null || concertIdStr == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Queue-Token or Concert-Id header");
            return false;
        }

        try {
            Long concertId = Long.valueOf(concertIdStr);
            if (!queueRedisRepository.isActiveToken(concertId, token)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired Queue Token");
                return false;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Concert-Id format");
            return false;
        }

        return true;
    }
}
