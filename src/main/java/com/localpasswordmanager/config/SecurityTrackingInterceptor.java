package com.localpasswordmanager.config;

import com.localpasswordmanager.model.SecurityTracker;
import com.localpasswordmanager.repository.SecurityTrackerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class SecurityTrackingInterceptor implements HandlerInterceptor {

    private final SecurityTrackerRepository securityTrackerRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Only track HTTPS requests as requested (or all, but checking scheme is good practice if strictly following 'every HTTPS request')
        // Often behind proxies X-Forwarded-Proto headers are used, checking scheme directly might return http if behind reverse proxy.
        // Assuming local direct access or standard setup.
        
        SecurityTracker tracker = new SecurityTracker();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        tracker.setTimestamp(LocalDateTime.now().format(formatter));
        tracker.setResource(request.getRequestURI());
        tracker.setIpAddress(request.getRemoteAddr());
        tracker.setAction("PAGE_ACCESS");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            tracker.setUsername(authentication.getName());
        } else {
            tracker.setUsername("ANONYMOUS");
        }
         // MAC address is Layer 2, not available in Layer 7 requests

        securityTrackerRepository.save(tracker);
        
        return true;
    }
}
