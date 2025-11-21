package warehouses.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import warehouses.project.dto.*;
import warehouses.project.model.*;
import warehouses.project.service.ShipmentService;
import warehouses.project.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@Tag(name = "Операции", description = "Управление складскими операциями (приход, списание, перемещение)")
@SecurityRequirement(name = "bearerAuth")
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final UserService userService;

    @Operation(
            summary = "Получить все операции",
            description = "Получает список всех операций с пагинацией и фильтрацией"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список операций",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ShipmentDto>> getAllShipments(
            @Parameter(description = "Фильтр по типу операции (incoming, write-off, outgoing, transfer)")
            @RequestParam(required = false) String transactionType,
            @Parameter(description = "Фильтр по ID склада")
            @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "Дата начала периода (формат: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Дата окончания периода (формат: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        LocalDateTime from = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime to = toDate != null ? toDate.atTime(23, 59, 59) : null;
        
        Page<Shipment> shipments = shipmentService.getAll(transactionType, warehouseId, from, to, pageable);
        Page<ShipmentDto> response = shipments.map(this::mapToDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить операцию по ID",
            description = "Получает информацию об операции по её идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Операция найдена",
                    content = @Content(schema = @Schema(implementation = ShipmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Операция не найдена"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDto> getShipmentById(
            @Parameter(description = "ID операции", required = true) @PathVariable Long id
    ) {
        Shipment shipment = shipmentService.getById(id);
        return ResponseEntity.ok(mapToDto(shipment));
    }

    @Operation(
            summary = "Создать операцию прихода товара",
            description = "Создает операцию прихода товара на склад. Увеличивает остатки на складе."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Операция успешно создана",
                    content = @Content(schema = @Schema(implementation = ShipmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @PostMapping("/incoming")
    public ResponseEntity<ShipmentDto> createIncoming(
            @Valid @RequestBody CreateShipmentRequest request,
            Authentication authentication
    ) {
        User user = userService.getByUsername(authentication.getName());
        List<ShipmentItem> items = mapToShipmentItems(request.getItems());
        Shipment created = shipmentService.createIncoming(request.getWarehouseId(), items, user.getId());
        return ResponseEntity.ok(mapToDto(created));
    }

    @Operation(
            summary = "Создать операцию списания товара",
            description = "Создает операцию списания товара со склада. Уменьшает остатки на складе."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Операция успешно создана",
                    content = @Content(schema = @Schema(implementation = ShipmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации или недостаточно товара на складе"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @PostMapping("/write-off")
    public ResponseEntity<ShipmentDto> createWriteOff(
            @Valid @RequestBody CreateShipmentRequest request
    ) {
        List<ShipmentItem> items = mapToShipmentItems(request.getItems());
        Shipment created = shipmentService.createWriteOff(request.getWarehouseId(), items);
        return ResponseEntity.ok(mapToDto(created));
    }

    @Operation(
            summary = "Создать операцию перемещения товара",
            description = "Создает операцию перемещения товара между складами. Уменьшает остатки на складе-источнике и увеличивает на складе-назначении."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Операция успешно создана",
                    content = @Content(schema = @Schema(implementation = ShipmentDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации или недостаточно товара на складе"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @PostMapping("/transfer")
    public ResponseEntity<ShipmentDto> createTransfer(
            @Valid @RequestBody CreateTransferRequest request
    ) {
        List<ShipmentItem> items = mapToShipmentItems(request.getItems());
        Shipment created = shipmentService.createTransfer(
                request.getFromWarehouseId(),
                request.getToWarehouseId(),
                items
        );
        return ResponseEntity.ok(mapToDto(created));
    }

    private ShipmentDto mapToDto(Shipment shipment) {
        WarehouseDto warehouseDto = WarehouseDto.builder()
                .id(shipment.getWarehouse().getId())
                .name(shipment.getWarehouse().getName())
                .location(shipment.getWarehouse().getLocation())
                .build();

        UserResponseDto userDto = null;
        if (shipment.getUser() != null) {
            userDto = UserResponseDto.builder()
                    .id(shipment.getUser().getId())
                    .username(shipment.getUser().getUsername())
                    .email(shipment.getUser().getEmail())
                    .role(shipment.getUser().getRole().name())
                    .build();
        }

        List<ShipmentItemDto> itemDtos = new ArrayList<>();
        if (shipment.getItems() != null) {
            itemDtos = shipment.getItems().stream()
                    .map(this::mapItemToDto)
                    .collect(Collectors.toList());
        }

        return ShipmentDto.builder()
                .id(shipment.getId())
                .transactionType(shipment.getTransactionType())
                .date(shipment.getDate())
                .warehouse(warehouseDto)
                .user(userDto)
                .items(itemDtos)
                .build();
    }

    private ShipmentItemDto mapItemToDto(ShipmentItem item) {
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

    private List<ShipmentItem> mapToShipmentItems(List<ShipmentItemDto> dtos) {
        return dtos.stream().map(dto -> {
            ShipmentItem item = new ShipmentItem();
            item.setCount(dto.getCount());
            Product product = new Product();
            product.setId(dto.getProductId());
            item.setProduct(product);
            return item;
        }).collect(Collectors.toList());
    }
}

