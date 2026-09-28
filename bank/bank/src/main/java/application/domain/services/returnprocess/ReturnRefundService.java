package application.domain.services.returnprocess;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.input.ReturnRefundUseCase;
import application.domain.ports.output.OrderRepositoryPort;
import application.domain.ports.output.RefundRepositoryPort;
import application.domain.ports.output.ReturnRepositoryPort;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ReturnRefundService implements ReturnRefundUseCase {

    private final ReturnRepositoryPort returnRepository;
    private final RefundRepositoryPort refundRepository;
    private final OrderRepositoryPort orderRepository;
    private final UserRepositoryPort userRepository;

    public ReturnRefundService(ReturnRepositoryPort returnRepository,
                               RefundRepositoryPort refundRepository,
                               OrderRepositoryPort orderRepository,
                               UserRepositoryPort userRepository) {
        this.returnRepository = returnRepository;
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Return requestReturn(String orderId, String buyerId) {
        requireRole(buyerId, SystemRole.BUYER, "requestReturn");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));

        if (!order.getBuyer().getIdentifier().equals(buyerId)) {
            throw new UnauthorizedRoleException("requestReturn",
                    "the buyer who placed the order");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidOperationException(
                    "Returns can only be requested for DELIVERED orders. "
                            + "Current status: " + order.getStatus());
        }
        if (returnRepository.findByOrderId(orderId).isPresent()) {
            throw new InvalidOperationException(
                    "A return has already been requested for order " + orderId + ".");
        }

        Return returnProcess = new Return();
        returnProcess.setId(UUID.randomUUID().toString());
        returnProcess.setOrder(order);
        return returnRepository.save(returnProcess);
    }

    @Override
    public Refund generateRefund(String returnId, String administratorId) {
        requireRole(administratorId, SystemRole.ADMINISTRATOR, "generateRefund");
        Return returnProcess = findReturnById(returnId);

        if (refundRepository.findByReturnId(returnId).isPresent()) {
            throw new InvalidOperationException(
                    "A refund has already been generated for return " + returnId + ".");
        }

        Refund refund = new Refund();
        refund.setId(UUID.randomUUID().toString());
        refund.setReturnProcess(returnProcess);
        return refundRepository.save(refund);
    }

    @Override
    public Return findReturnById(String returnId) {
        return returnRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return", returnId));
    }

    @Override
    public Refund findRefundById(String refundId) {
        return refundRepository.findById(refundId)
                .orElseThrow(() -> new EntityNotFoundException("Refund", refundId));
    }

    @Override
    public Refund findRefundByReturn(String returnId) {
        return refundRepository.findByReturnId(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Refund for return", returnId));
    }

    private User requireRole(String userId, SystemRole requiredRole, String action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        if (user.getRole() != requiredRole) {
            throw new UnauthorizedRoleException(action, requiredRole.name());
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidOperationException("User " + userId + " is not active.");
        }
        return user;
    }
}
