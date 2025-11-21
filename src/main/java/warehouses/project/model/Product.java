package warehouses.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String unit;
    private String description;

    @OneToMany(mappedBy = "product")
    private List<ShipmentItem> shipmentItems;

    @OneToMany(mappedBy = "product")
    private List<Stock> stocks;

}
