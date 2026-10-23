package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.request.ShipmentRequest;
import application.adapters.web.dto.response.ShipmentResponse;
import application.domain.ports.input.ShipmentManagementUseCase;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShipmentController {

    private final ShipmentManagementUseCase shipmentManagement;

    public ShipmentController(ShipmentManagementUseCase shipmentManagement) {
        this.shipmentManagement = shipmentManagement;
    }

    @PostMapping("/shipments")
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentResponse createShipment(@RequestHeader(RequestHeaders.USER_ID) String operatorId,
                                           @Valid @RequestBody ShipmentRequest request) {
        return ShipmentResponse.from(shipmentManagement.createShipment(request.orderId(), operatorId));
    }

    @PatchMapping("/shipments/{trackingId}/dispatch")
    public ShipmentResponse dispatchShipment(@RequestHeader(RequestHeaders.USER_ID) String operatorId,
                                             @PathVariable String trackingId) {
        return ShipmentResponse.from(shipmentManagement.dispatchShipment(trackingId, operatorId));
    }

    @PatchMapping("/shipments/{trackingId}/deliver")
    public ShipmentResponse confirmDelivery(@RequestHeader(RequestHeaders.USER_ID) String operatorId,
                                            @PathVariable String trackingId) {
        return ShipmentResponse.from(shipmentManagement.confirmDelivery(trackingId, operatorId));
    }

    @GetMapping("/shipments/{trackingId}")
    public ShipmentResponse findByTrackingId(@PathVariable String trackingId) {
        return ShipmentResponse.from(shipmentManagement.findShipmentByTrackingId(trackingId));
    }

    @GetMapping("/orders/{orderId}/shipment")
    public ShipmentResponse findByOrder(@PathVariable String orderId) {
        return ShipmentResponse.from(shipmentManagement.findShipmentByOrder(orderId));
    }

    @GetMapping("/shipments")
    public List<ShipmentResponse> listMyShipments(@RequestHeader(RequestHeaders.USER_ID) String operatorId) {
        return shipmentManagement.listShipmentsByOperator(operatorId).stream().map(ShipmentResponse::from).toList();
    }
}
