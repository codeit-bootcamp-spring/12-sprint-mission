package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";

    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_REQUEST_METHOD = "requestMethod";
    private static final String MDC_REQUEST_URI = "requestUri";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String requestId = UUID.randomUUID().toString();

        MDC.put(MDC_REQUEST_ID, requestId);
        MDC.put(MDC_REQUEST_METHOD, request.getMethod());
        MDC.put(MDC_REQUEST_URI, request.getRequestURI());

        response.setHeader(REQUEST_ID_HEADER, requestId);

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        MDC.clear();
    }
}