package com.localpasswordmanager.config;

import com.localpasswordmanager.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RateLimitFilterTest {

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testWhitelistRejection() throws Exception {
        // Setup Whitelist
        ReflectionTestUtils.setField(rateLimitFilter, "ipWhitelist", Arrays.asList("192.168.1.51"));

        // Request from non-whitelisted IP
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");
        when(request.getRequestURI()).thenReturn("/api/data");

        // Mock Bucket to ensure we don't crash if it GETS called (but it shouldn't)
        Bucket bucket = mock(Bucket.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);
        when(rateLimitService.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);

        // Execute
        rateLimitFilter.doFilter(request, response, filterChain);

        // Verify
        // Should return 403
        verify(response).sendError(eq(403), anyString());
        // Should NOT call rateLimitService
        verify(rateLimitService, never()).resolveBucket(anyString());
        // Should NOT proceed filter chain
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void testWhitelistAllowance() throws Exception {
        // Setup Whitelist
        ReflectionTestUtils.setField(rateLimitFilter, "ipWhitelist", Arrays.asList("192.168.1.51"));

        // Request from whitelisted IP
        when(request.getRemoteAddr()).thenReturn("192.168.1.51");
        when(request.getRequestURI()).thenReturn("/api/data");

        // Mock Bucket
        Bucket bucket = mock(Bucket.class);
        ConsumptionProbe probe = mock(ConsumptionProbe.class);
        when(rateLimitService.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);

        // Execute
        rateLimitFilter.doFilter(request, response, filterChain);

        // Verify
        // Should pass rate limit check (currently code does check it, even if whitelisted)
        verify(rateLimitService).resolveBucket("192.168.1.51");
        verify(filterChain).doFilter(request, response);
    }
}
