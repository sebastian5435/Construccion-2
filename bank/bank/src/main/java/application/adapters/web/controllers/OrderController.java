package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.request.PlaceOrderRequest;
import application.adapters.web.dto.response.InvoiceResponse;
import application.adapters.web.dto.response.OrderResponse;
import application.domain.ports.input.OrderProcessingUseCase;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProcessingUseCase orderProcessing;

    public OrderController(OrderProcessingUseCase orderProcessing) {
        this.orderProcessing = orderProcessing;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestHeader(RequestHeaders.USER_ID) String buyerId,
                                    @RequestBody(required = false) PlaceOrderRequest request) {
        String deliveryAddress = request == null ? null : request.deliveryAddress();
        return OrderResponse.from(orderProcessing.placeOrder(buyerId, deliveryAddress));
    }

    @PatchMapping("/{orderId}/pay")
    public OrderResponse payOrder(@RequestHeader(RequestHeaders.USER_ID) String buyerId,
                                  @PathVariable String orderId) {
        return OrderResponse.from(orderProcessing.payOrder(orderId, buyerId));
    }

    @PatchMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(@RequestHeader(RequestHeaders.USER_ID) String buyerId,
                                     @PathVariable String orderId) {
        return OrderResponse.from(orderProcessing.cancelOrder(orderId, buyerId));
    }

    @GetMapping("/{orderId}")
    public OrderResponse findOrderById(@PathVariable String orderId) {
        return OrderResponse.from(orderProcessing.findOrderById(orderId));
    }

    @GetMapping
    public List<OrderResponse> listMyOrders(@RequestHeader(RequestHeaders.USER_ID) String buyerId) {
        return orderProcessing.listOrdersByBuyer(buyerId).stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{orderId}/invoice")
    public InvoiceResponse getInvoice(@PathVariable String orderId) {
        return InvoiceResponse.from(orderProcessing.getInvoiceByOrder(orderId));
    }
}
