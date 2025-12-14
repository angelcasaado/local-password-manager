package com.localpasswordmanager.config;

import com.localpasswordmanager.model.SecurityTracker;
import com.localpasswordmanager.repository.SecurityTrackerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final SecurityTrackerRepository securityTrackerRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication.getName();
        
        SecurityTracker tracker = new SecurityTracker();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        tracker.setTimestamp(LocalDateTime.now().format(formatter));
        tracker.setResource("/login");
        tracker.setUsername(username);
        tracker.setAction("LOGIN_SUCCESS");

        Object details = authentication.getDetails();
        if (details instanceof WebAuthenticationDetails) {
            tracker.setIpAddress(((WebAuthenticationDetails) details).getRemoteAddress());
        } else {
            tracker.setIpAddress("UNKNOWN");
        }

        securityTrackerRepository.save(tracker);
    }
}
