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
import warehouses.project.dto.ProductDto;
import warehouses.project.model.Product;
import warehouses.project.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Товары", description = "Управление товарами")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Получить все товары",
            description = "Получает список всех товаров с пагинацией и возможностью поиска по названию"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список товаров",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @Parameter(description = "Поиск по названию товара") @RequestParam(required = false) String name,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> products = productService.searchProducts(name, pageable);
        Page<ProductDto> response = products.map(this::mapToDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить товар по ID",
            description = "Получает информацию о товаре по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар найден",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id
    ) {
        Product product = productService.getById(id);
        return ResponseEntity.ok(mapToDto(product));
    }

    @Operation(
            summary = "Создать новый товар",
            description = "Создает новый товар в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар успешно создан",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductDto productDto
    ) {
        Product product = mapToEntity(productDto);
        Product created = productService.createProduct(product);
        return ResponseEntity.ok(mapToDto(created));
    }

    @Operation(
            summary = "Обновить товар",
            description = "Обновляет информацию о существующем товаре"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар успешно обновлен",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto
    ) {
        Product product = mapToEntity(productDto);
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @Operation(
            summary = "Удалить товар",
            description = "Удаляет товар из системы"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Товар успешно удален"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден"
            )
    })
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .unit(product.getUnit())
                .description(product.getDescription())
                .build();
    }

    private Product mapToEntity(ProductDto dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setUnit(dto.getUnit());
        product.setDescription(dto.getDescription());
        return product;
    }
}

