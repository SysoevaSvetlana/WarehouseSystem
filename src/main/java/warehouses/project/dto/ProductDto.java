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
@Schema(description = "Информация о товаре")
public class ProductDto {
    @Schema(description = "ID товара", example = "1")
    private Long id;

    @Schema(description = "Название товара", example = "Ноутбук Lenovo")
    @NotBlank(message = "Название товара не может быть пустым")
    @Size(max = 255, message = "Название товара не может превышать 255 символов")
    private String name;

    @Schema(description = "Единица измерения", example = "шт")
    @Size(max = 50, message = "Единица измерения не может превышать 50 символов")
    private String unit;

    @Schema(description = "Описание товара", example = "Ноутбук для офисной работы")
    @Size(max = 1000, message = "Описание не может превышать 1000 символов")
    private String description;
}

