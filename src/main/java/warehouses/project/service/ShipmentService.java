package warehouses.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouses.project.model.*;
import warehouses.project.repository.ProductRepository;
import warehouses.project.repository.ShipmentRepository;
import warehouses.project.repository.StockRepository;
import warehouses.project.repository.WarehouseRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentItemService shipmentItemService;
    private final StockService stockService;
    private final WarehouseService warehouseService;
    private final ProductService productService;
    private final UserService userService;

    public Page<Shipment> getAll(String transactionType, Long warehouseId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        LocalDate startDate = from != null ? from.toLocalDate() : null;
        LocalDate endDate = to != null ? to.toLocalDate() : null;

        return shipmentRepository.filterShipments(transactionType, warehouseId, startDate, endDate, pageable);
    }

    public Shipment getById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + id));
    }

    @Transactional
    public Shipment createIncoming(Long warehouseId, List<ShipmentItem> items, Long userId) {
        Warehouse warehouse = warehouseService.getById(warehouseId);
        User user = userService.getById(userId);

        Shipment shipment = new Shipment();
        shipment.setWarehouse(warehouse);
        shipment.setUser(user);
        shipment.setTransactionType("incoming");
        shipment.setDate(LocalDate.now());
        shipment = shipmentRepository.save(shipment);

        for (ShipmentItem item : items) {
            Product product = productService.getById(item.getProduct().getId());
            item.setShipment(shipment);
            shipmentItemService.createShipmentItem(shipment.getId(), item);
            stockService.increaseStock(product, warehouse, item.getCount());
        }

        return shipment;
    }

    @Transactional
    public Shipment createWriteOff(Long warehouseId, List<ShipmentItem> items) {
        Warehouse warehouse = warehouseService.getById(warehouseId);

        Shipment shipment = new Shipment();
        shipment.setWarehouse(warehouse);
        shipment.setTransactionType("write-off");
        shipment.setDate(LocalDate.now());
        shipment = shipmentRepository.save(shipment);

        for (ShipmentItem item : items) {
            Product product = productService.getById(item.getProduct().getId());
            item.setShipment(shipment);
            shipmentItemService.createShipmentItem(shipment.getId(), item);
            stockService.decreaseStock(product, warehouse, item.getCount());
        }

        return shipment;
    }

    @Transactional
    public Shipment createTransfer(Long fromWarehouseId, Long toWarehouseId, List<ShipmentItem> items) {
        Warehouse from = warehouseService.getById(fromWarehouseId);
        Warehouse to = warehouseService.getById(toWarehouseId);

        Shipment shipment = new Shipment();
        shipment.setWarehouse(from);
        shipment.setTransactionType("transfer");
        shipment.setDate(LocalDate.now());
        shipment = shipmentRepository.save(shipment);

        for (ShipmentItem item : items) {
            Product product = productService.getById(item.getProduct().getId());
            item.setShipment(shipment);
            shipmentItemService.createShipmentItem(shipment.getId(), item);

            stockService.decreaseStock(product, from, item.getCount());
            stockService.increaseStock(product, to, item.getCount());
        }

        return shipment;
    }
}
