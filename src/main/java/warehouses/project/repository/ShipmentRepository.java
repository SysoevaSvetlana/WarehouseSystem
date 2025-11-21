package warehouses.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import warehouses.project.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторий операций (приход, списание, отгрузка)
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    //  Фильтрация по складу
    Page<Shipment> findByWarehouseId(Long warehouseId, Pageable pageable);

    //  Фильтрация по диапазону дат
    Page<Shipment> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    //  Фильтрация по типу операции
    Page<Shipment> findByTransactionType(String transactionType, Pageable pageable);

    //  Фильтрация по типу операции и складу
    Page<Shipment> findByTransactionTypeAndWarehouseId(String transactionType, Long warehouseId, Pageable pageable);

    //  Фильтрация по типу операции и диапазону дат
    Page<Shipment> findByTransactionTypeAndDateBetween(String transactionType, LocalDate from, LocalDate to, Pageable pageable);

    //  Фильтрация по складу и диапазону дат
    Page<Shipment> findByWarehouseIdAndDateBetween(Long warehouseId, LocalDate from, LocalDate to, Pageable pageable);

    //  Комплексная фильтрация
    @Query("SELECT s FROM Shipment s WHERE " +
           "(:transactionType IS NULL OR s.transactionType = :transactionType) AND " +
           "(:warehouseId IS NULL OR s.warehouse.id = :warehouseId) AND " +
           "(:startDate IS NULL OR s.date >= :startDate) AND " +
           "(:endDate IS NULL OR s.date <= :endDate)")
    Page<Shipment> filterShipments(
            String transactionType,
            Long warehouseId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
