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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import warehouses.project.dto.WarehouseDto;
import warehouses.project.model.Warehouse;
import warehouses.project.service.WarehouseService;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Tag(name = "Склады", description = "Управление складами (только для администраторов)")
@SecurityRequirement(name = "bearerAuth")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(
            summary = "Получить все склады",
            description = "Получает список всех складов с пагинацией и возможностью поиска по названию"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список складов",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<Page<WarehouseDto>> getAllWarehouses(
            @Parameter(description = "Поиск по названию склада") @RequestParam(required = false) String name,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Warehouse> warehouses = warehouseService.searchWarehouses(name, pageable);
        Page<WarehouseDto> response = warehouses.map(this::mapToDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить склад по ID",
            description = "Получает информацию о складе по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Склад найден",
                    content = @Content(schema = @Schema(implementation = WarehouseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Склад не найден"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDto> getWarehouseById(
            @Parameter(description = "ID склада", required = true) @PathVariable Long id
    ) {
        Warehouse warehouse = warehouseService.getById(id);
        return ResponseEntity.ok(mapToDto(warehouse));
    }

    @Operation(
            summary = "Создать новый склад (ADMIN)",
            description = "Создает новый склад в системе. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Склад успешно создан",
                    content = @Content(schema = @Schema(implementation = WarehouseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<WarehouseDto> createWarehouse(
            @Valid @RequestBody WarehouseDto warehouseDto
    ) {
        Warehouse warehouse = mapToEntity(warehouseDto);
        Warehouse created = warehouseService.createWarehouse(warehouse);
        return ResponseEntity.ok(mapToDto(created));
    }

    @Operation(
            summary = "Обновить склад (ADMIN)",
            description = "Обновляет информацию о существующем складе. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Склад успешно обновлен",
                    content = @Content(schema = @Schema(implementation = WarehouseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Склад не найден"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDto> updateWarehouse(
            @Parameter(description = "ID склада", required = true) @PathVariable Long id,
            @Valid @RequestBody WarehouseDto warehouseDto
    ) {
        Warehouse warehouse = mapToEntity(warehouseDto);
        Warehouse updated = warehouseService.updateWarehouse(id, warehouse);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @Operation(
            summary = "Удалить склад (ADMIN)",
            description = "Удаляет склад из системы. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Склад успешно удален"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Склад не найден"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(
            @Parameter(description = "ID склада", required = true) @PathVariable Long id
    ) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    private WarehouseDto mapToDto(Warehouse warehouse) {
        return WarehouseDto.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .build();
    }

    private Warehouse mapToEntity(WarehouseDto dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(dto.getId());
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        return warehouse;
    }
}

