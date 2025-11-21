package warehouses.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @OneToMany(mappedBy = "warehouse")
    private List<Shipment> shipments;

    @OneToMany(mappedBy = "warehouse")
    private List<Stock> stocks;

}