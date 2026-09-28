package application.domain.ports.output;

import application.domain.models.Order;
import application.domain.valueobjects.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findByBuyerId(String buyerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findAll();
    void deleteById(String id);
}
