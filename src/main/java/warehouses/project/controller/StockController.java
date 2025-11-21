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
import warehouses.project.dto.StockDto;
import warehouses.project.dto.WarehouseDto;
import warehouses.project.model.Stock;
import warehouses.project.service.StockService;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Остатки", description = "Просмотр остатков товаров на складах (только чтение)")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final StockService stockService;

    @Operation(
            summary = "Получить все остатки",
            description = "Получает список всех остатков товаров на складах с пагинацией и фильтрацией"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список остатков",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<Page<StockDto>> getAllStock(
            @Parameter(description = "Поиск по названию товара") @RequestParam(required = false) String productName,
            @Parameter(description = "Фильтр по ID склада") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Stock> stocks = stockService.searchByProductOrWarehouse(productName, warehouseId, pageable);
        Page<StockDto> response = stocks.map(this::mapToDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить остаток по ID",
            description = "Получает информацию об остатке по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Остаток найден",
                    content = @Content(schema = @Schema(implementation = StockDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Остаток не найден"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getStockById(
            @Parameter(description = "ID остатка", required = true) @PathVariable Long id
    ) {
        Stock stock = stockService.getById(id);
        return ResponseEntity.ok(mapToDto(stock));
    }

    private StockDto mapToDto(Stock stock) {
        ProductDto productDto = ProductDto.builder()
                .id(stock.getProduct().getId())
                .name(stock.getProduct().getName())
                .unit(stock.getProduct().getUnit())
                .description(stock.getProduct().getDescription())
                .build();

        WarehouseDto warehouseDto = WarehouseDto.builder()
                .id(stock.getWarehouse().getId())
                .name(stock.getWarehouse().getName())
                .location(stock.getWarehouse().getLocation())
                .build();

        return StockDto.builder()
                .id(stock.getId())
                .count(stock.getCount())
                .lastUpdate(stock.getLastUpdate())
                .warehouse(warehouseDto)
                .product(productDto)
                .build();
    }
}

