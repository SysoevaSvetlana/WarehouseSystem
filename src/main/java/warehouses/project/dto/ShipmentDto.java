package warehouses.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Информация об операции (приход/списание/перемещение)")
public class ShipmentDto {
    @Schema(description = "ID операции", example = "1")
    private Long id;

    @Schema(description = "Тип операции", example = "incoming", allowableValues = {"incoming", "write-off", "outgoing", "transfer"})
    private String transactionType;

    @Schema(description = "Дата операции", example = "2024-01-15")
    private LocalDate date;

    @Schema(description = "Информация о складе")
    private WarehouseDto warehouse;

    @Schema(description = "Информация о пользователе")
    private UserResponseDto user;

    @Schema(description = "Список товаров в операции")
    private List<ShipmentItemDto> items;
}

