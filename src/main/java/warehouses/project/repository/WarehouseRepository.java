package warehouses.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import warehouses.project.model.Warehouse;

/**
 * Репозиторий для складов
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Page<Warehouse> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
