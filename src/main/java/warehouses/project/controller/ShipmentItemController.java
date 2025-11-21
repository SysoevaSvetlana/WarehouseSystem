package warehouses.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import warehouses.project.dto.ProductDto;
import warehouses.project.dto.ShipmentItemDto;
import warehouses.project.model.ShipmentItem;
import warehouses.project.service.ShipmentItemService;

@RestController
@RequestMapping("/api/shipment-items")
@RequiredArgsConstructor
@Tag(name = "Элементы операций", description = "Просмотр деталей операций (только чтение)")
@SecurityRequirement(name = "bearerAuth")
public class ShipmentItemController {

    private final ShipmentItemService shipmentItemService;

    @Operation(
            summary = "Получить все элементы операций",
            description = "Получает список всех элементов операций с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список элементов операций",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ShipmentItemDto>> getAllShipmentItems(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ShipmentItem> items = shipmentItemService.getAll(pageable);
        Page<ShipmentItemDto> response = items.map(this::mapToDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить элементы операции по ID операции",
            description = "Получает список всех элементов конкретной операции с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список элементов операции",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping("/by-shipment/{shipmentId}")
    public ResponseEntity<Page<ShipmentItemDto>> getShipmentItemsByShipmentId(
            @Parameter(description = "ID операции", required = true) @PathVariable Long shipmentId,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ShipmentItem> items = shipmentItemService.getByShipmentId(shipmentId, pageable);
        Page<ShipmentItemDto> response = items.map(this::mapToDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить элемент операции по ID",
            description = "Получает информацию об элементе операции по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Элемент операции найден",
                    content = @Content(schema = @Schema(implementation = ShipmentItemDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Элемент операции не найден"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentItemDto> getShipmentItemById(
            @Parameter(description = "ID элемента операции", required = true) @PathVariable Long id
    ) {
        ShipmentItem item = shipmentItemService.getById(id);
        return ResponseEntity.ok(mapToDto(item));
    }

    private ShipmentItemDto mapToDto(ShipmentItem item) {
        ProductDto productDto = ProductDto.builder()
                .id(item.getProduct().getId())
                .name(item.getProduct().getName())
                .unit(item.getProduct().getUnit())
                .description(item.getProduct().getDescription())
                .build();

        return ShipmentItemDto.builder()
                .id(item.getId())
                .count(item.getCount())
                .productId(item.getProduct().getId())
                .product(productDto)
                .shipmentId(item.getShipment() != null ? item.getShipment().getId() : null)
                .build();
    }
}

