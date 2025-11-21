package warehouses.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import warehouses.project.model.Warehouse;
import warehouses.project.repository.WarehouseRepository;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public Page<Warehouse> getAllWarehouses(Pageable pageable) {
        return warehouseRepository.findAll(pageable);
    }

    public Page<Warehouse> searchWarehouses(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return warehouseRepository.findAll(pageable);
        }
        return warehouseRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Warehouse getById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));
    }

    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouse(Long id, Warehouse updatedWarehouse) {
        Warehouse existing = getById(id);
        existing.setName(updatedWarehouse.getName());
        existing.setLocation(updatedWarehouse.getLocation());
        return warehouseRepository.save(existing);
    }

    public void deleteWarehouse(Long id) {
        warehouseRepository.deleteById(id);
    }
}