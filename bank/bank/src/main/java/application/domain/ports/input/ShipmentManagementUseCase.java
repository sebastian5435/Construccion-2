package application.domain.ports.input;

import application.domain.models.Shipment;
import java.util.List;

/**
 * Manages the logistics process of paid orders.
 * Covers business objective OBJ-10.
 */
public interface ShipmentManagementUseCase {

    Shipment createShipment(String orderId, String logisticsOperatorId);

    Shipment findShipmentByOrder(String orderId);

    Shipment findShipmentByTrackingId(String trackingId);

    List<Shipment> listShipmentsByOperator(String operatorId);
}
