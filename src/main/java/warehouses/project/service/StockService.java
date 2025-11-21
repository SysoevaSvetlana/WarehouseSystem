package warehouses.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import warehouses.project.model.Product;
import warehouses.project.model.Stock;
import warehouses.project.model.Warehouse;
import warehouses.project.repository.StockRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public Page<Stock> getAll(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }

    public Page<Stock> searchByProductOrWarehouse(String productName, Long warehouseId, Pageable pageable) {
        if (productName != null && warehouseId != null) {
            return stockRepository.findByProductNameContainingIgnoreCaseAndWarehouseId(productName, warehouseId, pageable);
        } else if (productName != null) {
            return stockRepository.findByProductNameContainingIgnoreCase(productName, pageable);
        } else if (warehouseId != null) {
            return stockRepository.findByWarehouseId(warehouseId, pageable);
        }
        return stockRepository.findAll(pageable);
    }

    public Stock getById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with id: " + id));
    }

    public Stock getStock(Product product, Warehouse warehouse) {
        return stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setProduct(product);
                    newStock.setWarehouse(warehouse);
                    newStock.setCount(0);
                    return stockRepository.save(newStock);
                });
    }

    public void increaseStock(Product product, Warehouse warehouse, int amount) {
        Stock stock = getStock(product, warehouse);
        stock.setCount(stock.getCount() + amount);
        stock.setLastUpdate(LocalDateTime.now());
        stockRepository.save(stock);
    }

    public void decreaseStock(Product product, Warehouse warehouse, int amount) {
        Stock stock = getStock(product, warehouse);
        if (stock.getCount() < amount) {
            throw new IllegalStateException("Not enough stock for product: " + product.getName());
        }
        stock.setCount(stock.getCount() - amount);
        stock.setLastUpdate(LocalDateTime.now());
        stockRepository.save(stock);
    }
}