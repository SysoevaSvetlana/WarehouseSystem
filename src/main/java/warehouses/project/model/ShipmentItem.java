package warehouses.project.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "shipmentitems")
public class ShipmentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer count;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
