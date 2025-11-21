package warehouses.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Конфигурация OpenAPI/Swagger документации.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Warehouse Management API",
                description = "API для управления складским учетом",
                version = "1.0.0",
                contact = @Contact(
                        name = "Warehouse Team"
                )
        ),
        servers = {
                @Server(
                        description = "Local Server",
                        url = "http://localhost:8080"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT токен аутентификации",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}

