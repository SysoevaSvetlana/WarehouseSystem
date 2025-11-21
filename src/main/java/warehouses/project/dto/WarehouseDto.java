package warehouses.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Информация о складе")
public class WarehouseDto {
    @Schema(description = "ID склада", example = "1")
    private Long id;

    @Schema(description = "Название склада", example = "Центральный склад")
    @NotBlank(message = "Название склада не может быть пустым")
    @Size(max = 255, message = "Название склада не может превышать 255 символов")
    private String name;

    @Schema(description = "Местоположение склада", example = "г. Москва, ул. Складская, д. 1")
    @Size(max = 500, message = "Местоположение не может превышать 500 символов")
    private String location;
}

