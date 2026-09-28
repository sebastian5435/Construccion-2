package application.adapters.persistence.inmemory;

import application.domain.models.Shipment;
import application.domain.ports.output.ShipmentRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryShipmentRepository implements ShipmentRepositoryPort {

    private final Map<String, Shipment> shipments = new ConcurrentHashMap<>();

    @Override
    public Shipment save(Shipment shipment) {
        shipments.put(shipment.getTrackingId(), shipment);
        return shipment;
    }

    @Override
    public Optional<Shipment> findByTrackingId(String trackingId) {
        return Optional.ofNullable(shipments.get(trackingId));
    }

    @Override
    public Optional<Shipment> findByOrderId(String orderId) {
        return shipments.values().stream()
                .filter(s -> s.getOrder() != null
                        && orderId.equals(s.getOrder().getId()))
                .findFirst();
    }

    @Override
    public List<Shipment> findByOperatorId(String operatorId) {
        return shipments.values().stream()
                .filter(s -> s.getLogisticsOperator() != null
                        && operatorId.equals(s.getLogisticsOperator().getIdentifier()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Shipment> findAll() {
        return new ArrayList<>(shipments.values());
    }

    @Override
    public void deleteByTrackingId(String trackingId) {
        shipments.remove(trackingId);
    }
}
