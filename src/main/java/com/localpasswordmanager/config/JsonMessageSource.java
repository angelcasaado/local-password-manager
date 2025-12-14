package com.localpasswordmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component("messageSource")
public class JsonMessageSource extends AbstractMessageSource {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Locale, Map<String, String>> messagesCache = new HashMap<>();

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String message = getMessage(code, locale);
        return message != null ? new MessageFormat(message, locale) : null;
    }

    private String getMessage(String code, Locale locale) {
        Map<String, String> data = messagesCache.get(locale);
        if (data == null) {
            data = loadMessages(locale);
            messagesCache.put(locale, data);
        }
        
        // Fallback to English if not found in requested locale
        String message = data.get(code);
        if (message == null && !Locale.ENGLISH.equals(locale)) {
               return getMessage(code, Locale.ENGLISH);
        }
        
        return message;
    }

    private Map<String, String> loadMessages(Locale locale) {
        String fileName = "messages_" + locale.getLanguage() + ".json";
        if (Locale.ENGLISH.equals(locale)) {
            fileName = "messages.json"; 
        }
        
        // For Spanish fallback if just "es" is requested and filename matches
        if (locale.getLanguage().equals("es")) {
             fileName = "messages_es.json";
        }

        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            if (!resource.exists()) {
                // Try default
                return new HashMap<>();
            }
            return objectMapper.readValue(resource.getInputStream(), Map.class);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}
