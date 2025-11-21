package warehouses.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import warehouses.project.model.Shipment;
import warehouses.project.model.ShipmentItem;
import warehouses.project.repository.ShipmentItemRepository;
import warehouses.project.repository.ShipmentRepository;

@Service
@RequiredArgsConstructor
public class ShipmentItemService {

    private final ShipmentItemRepository shipmentItemRepository;
    private final ShipmentRepository shipmentRepository;

    /**
     * Получить все ShipmentItems с пагинацией.
     */
    public Page<ShipmentItem> getAll(Pageable pageable) {
        return shipmentItemRepository.findAll(pageable);
    }

    /**
     * Получить все ShipmentItems по ID поставки (с пагинацией).
     */
    public Page<ShipmentItem> getByShipmentId(Long shipmentId, Pageable pageable) {
        return shipmentItemRepository.findByShipmentId(shipmentId, pageable);
    }

    /**
     * Получить ShipmentItem по ID.
     */
    public ShipmentItem getById(Long id) {
        return shipmentItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ShipmentItem not found with id: " + id));
    }

    /**
     * Создать ShipmentItem, привязанный к существующей поставке.
     */
    public ShipmentItem createShipmentItem(Long shipmentId, ShipmentItem shipmentItem) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with id: " + shipmentId));

        shipmentItem.setShipment(shipment);
        return shipmentItemRepository.save(shipmentItem);
    }
}