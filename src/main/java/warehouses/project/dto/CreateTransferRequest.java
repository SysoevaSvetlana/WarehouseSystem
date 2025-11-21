package warehouses.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на создание операции перемещения между складами")
public class CreateTransferRequest {
    @Schema(description = "ID склада-источника", example = "1", required = true)
    @NotNull(message = "ID склада-источника не может быть пустым")
    private Long fromWarehouseId;

    @Schema(description = "ID склада-назначения", example = "2", required = true)
    @NotNull(message = "ID склада-назначения не может быть пустым")
    private Long toWarehouseId;

    @Schema(description = "Список товаров для перемещения", required = true)
    @NotEmpty(message = "Список товаров не может быть пустым")
    @Valid
    private List<ShipmentItemDto> items;
}

