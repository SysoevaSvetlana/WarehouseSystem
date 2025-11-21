package warehouses.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Информация об остатках товара на складе")
public class StockDto {
    @Schema(description = "ID записи остатка", example = "1")
    private Long id;

    @Schema(description = "Количество товара", example = "100")
    private Integer count;

    @Schema(description = "Дата последнего обновления", example = "2024-01-15T10:30:00")
    private LocalDateTime lastUpdate;

    @Schema(description = "Информация о складе")
    private WarehouseDto warehouse;

    @Schema(description = "Информация о товаре")
    private ProductDto product;
}

