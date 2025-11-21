package warehouses.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import warehouses.project.model.Product;
import warehouses.project.model.Stock;
import warehouses.project.model.Warehouse;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Репозиторий остатков товаров на складах.
 * Эти данные не редактируются напрямую — только через Shipments.
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    // Найти запись остатков по товару и складу
    Optional<Stock> findByProductAndWarehouse(Product product, Warehouse warehouse);

    // Фильтрация по названию товара (через вложенные поля)
    Page<Stock> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    // Фильтрация по складу
    Page<Stock> findByWarehouseId(Long warehouseId, Pageable pageable);

    // Фильтрация по названию товара и складу одновременно
    Page<Stock> findByProductNameContainingIgnoreCaseAndWarehouseId(String productName, Long warehouseId, Pageable pageable);
}
