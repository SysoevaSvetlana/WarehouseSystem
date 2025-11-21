package warehouses.project.model;

import jakarta.persistence.*;
import lombok.Data;
import  warehouses.project.model.*;

import java.time.LocalDate;
import java.util.List;
@Data
@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate date;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)
    private List<ShipmentItem> items;

}