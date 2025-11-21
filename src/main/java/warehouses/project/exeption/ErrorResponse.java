package warehouses.project.exeption;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Стандартный формат ответа при ошибке.
 * Обеспечивает единообразную структуру сообщений об ошибках для клиента.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Информация об ошибке")
public class ErrorResponse {
    
    @Schema(description = "Временная метка ошибки", example = "2024-11-19T21:46:13")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP статус код", example = "400")
    private int status;
    
    @Schema(description = "Тип ошибки", example = "Ошибка валидации")
    private String error;
    
    @Schema(description = "Сообщение об ошибке", example = "Проверьте правильность введенных данных")
    private String message;
    
    @Schema(description = "Путь запроса", example = "/api/auth/sign-up")
    private String path;
    
    @Schema(description = "Детали ошибок валидации (поле -> сообщение)")
    private Map<String, String> validationErrors;
}

