package com.localpasswordmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
@lombok.RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    @org.springframework.beans.factory.annotation.Value("${app.default.locale:en}")
    private String defaultLocale;

    private final SecurityTrackingInterceptor securityTrackingInterceptor;

    @Bean
    public LocaleResolver localeResolver() {
        org.springframework.web.servlet.i18n.CookieLocaleResolver r = new org.springframework.web.servlet.i18n.CookieLocaleResolver();
        r.setDefaultLocale(new Locale("es")); 
        r.setCookieName("LOCALE");
        r.setCookieMaxAge(3600 * 24 * 30); // 30 days
        return r;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(securityTrackingInterceptor);
    }
}
