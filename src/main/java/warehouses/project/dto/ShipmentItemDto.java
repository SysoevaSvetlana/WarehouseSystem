package warehouses.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Элемент отгрузки/операции")
public class ShipmentItemDto {
    @Schema(description = "ID элемента", example = "1")
    private Long id;

    @Schema(description = "Количество товара", example = "50")
    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть больше 0")
    private Integer count;

    @Schema(description = "ID товара", example = "1")
    @NotNull(message = "ID товара не может быть пустым")
    private Long productId;

    @Schema(description = "Информация о товаре")
    private ProductDto product;

    @Schema(description = "ID операции", example = "1")
    private Long shipmentId;
}

