package application.domain.ports.output;

import application.domain.models.Shipment;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepositoryPort {
    Shipment save(Shipment shipment);
    Optional<Shipment> findByTrackingId(String trackingId);
    Optional<Shipment> findByOrderId(String orderId);
    List<Shipment> findByOperatorId(String operatorId);
    List<Shipment> findAll();
    void deleteByTrackingId(String trackingId);
}
