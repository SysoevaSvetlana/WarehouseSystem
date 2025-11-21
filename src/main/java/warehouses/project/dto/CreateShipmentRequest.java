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
@Schema(description = "Запрос на создание операции прихода/списания")
public class CreateShipmentRequest {
    @Schema(description = "ID склада", example = "1", required = true)
    @NotNull(message = "ID склада не может быть пустым")
    private Long warehouseId;

    @Schema(description = "Список товаров", required = true)
    @NotEmpty(message = "Список товаров не может быть пустым")
    @Valid
    private List<ShipmentItemDto> items;
}

