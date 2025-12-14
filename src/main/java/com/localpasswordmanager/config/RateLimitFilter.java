package com.localpasswordmanager.config;

import com.localpasswordmanager.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RateLimitService rateLimitService;

    @Value("#{'${app.security.ip-whitelist:}'.split(',')}")
    private List<String> ipWhitelist;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Skip static resources
        if (request.getRequestURI().startsWith("/css") || request.getRequestURI().startsWith("/js")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();

        // Whitelist Check: If list is not empty (checks for empty strings from split empty prop)
        boolean hasWhitelist = ipWhitelist != null && !ipWhitelist.isEmpty() && !ipWhitelist.get(0).isEmpty();
        
        if (hasWhitelist) {
             boolean allowed = false;
             for (String allowedIp : ipWhitelist) {
                 if (allowedIp.trim().equals(ip)) {
                     allowed = true;
                     break;
                 }
             }
             if (!allowed) {
                 response.setStatus(HttpStatus.FORBIDDEN.value());
                 response.getWriter().write("Access Denied / Acceso Denegado");
                 return;
             }
             // If allowed by whitelist, we SKIP rate limiting
             filterChain.doFilter(request, response);
             return;
        }

        // Rate Limit Check (Only if whitelist is not active)
        Bucket tokenBucket = rateLimitService.resolveBucket(ip);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            // Return 429 with localized message
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests / Demasiadas peticiones");
        }
    }
}
