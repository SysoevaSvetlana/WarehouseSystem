package warehouses.project.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import warehouses.project.model.ShipmentItem;

import java.util.List;

/**
 * Репозиторий для деталей операций
 */
@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Long> {
    Page<ShipmentItem> findByShipmentId(Long shipmentId, Pageable pageable);

}