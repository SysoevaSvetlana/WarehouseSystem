package warehouses.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Конфигурация защиты от XSS атак.
 * Автоматически экранирует HTML-теги во всех строковых полях при десериализации JSON.
 */
@Configuration
public class XssProtectionConfig {

    /**
     * Кастомный десериализатор для защиты от XSS.
     * Экранирует HTML-теги в строковых значениях.
     */
    public static class XssStringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            if (value == null) {
                return null;
            }
            // Экранируем HTML-теги для защиты от XSS
            return HtmlUtils.htmlEscape(value);
        }
    }

    /**
     * Настройка ObjectMapper с защитой от XSS
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        
        SimpleModule xssModule = new SimpleModule("XssProtectionModule");
        xssModule.addDeserializer(String.class, new XssStringDeserializer());
        
        objectMapper.registerModule(xssModule);
        
        return objectMapper;
    }
}

