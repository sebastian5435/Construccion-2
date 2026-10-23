package application.domain.services.returnprocess;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Administrator;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.ports.input.ReturnRefundUseCase;
import application.domain.ports.output.OrderRepositoryPort;
import application.domain.ports.output.RefundRepositoryPort;
import application.domain.ports.output.ReturnRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.services.inventory.StockMovementService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReturnRefundService implements ReturnRefundUseCase {

    private final ReturnRepositoryPort returnRepository;
    private final RefundRepositoryPort refundRepository;
    private final OrderRepositoryPort orderRepository;
    private final StockMovementService stockMovementService;
    private final AccessGuard accessGuard;

    public ReturnRefundService(ReturnRepositoryPort returnRepository,
                               RefundRepositoryPort refundRepository,
                               OrderRepositoryPort orderRepository,
                               StockMovementService stockMovementService,
                               AccessGuard accessGuard) {
        this.returnRepository = returnRepository;
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.stockMovementService = stockMovementService;
        this.accessGuard = accessGuard;
    }

    @Override
    public Return requestReturn(String orderId, String buyerId, String reason) {
        accessGuard.requireActiveUser(buyerId, "requestReturn", SystemRole.BUYER);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        if (!order.isPlacedBy(buyerId)) {
            throw new UnauthorizedRoleException("requestReturn",
                    "the buyer who placed the order");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidOperationException(
                    "Returns can only be requested for DELIVERED orders. "
                            + "Current status: " + order.getStatus());
        }
        if (reason == null || reason.isBlank()) {
            throw new InvalidOperationException("A reason is required to request a return.");
        }
        if (returnRepository.findByOrderId(orderId).isPresent()) {
            throw new InvalidOperationException(
                    "A return has already been requested for order " + orderId + ".");
        }
        Return returnProcess = new Return();
        returnProcess.setId(UUID.randomUUID().toString());
        returnProcess.setOrder(order);
        returnProcess.setReason(reason);
        returnProcess.setStatus(ReturnStatus.REQUESTED);
        returnProcess.setRequestedAt(LocalDateTime.now());
        return returnRepository.save(returnProcess);
    }

    @Override
    public Refund generateRefund(String returnId, String administratorId) {
        Administrator administrator = requireAdministrator(administratorId, "generateRefund");
        Return returnProcess = findReturnById(returnId);
        if (refundRepository.findByReturnId(returnId).isPresent()) {
            throw new InvalidOperationException(
                    "A refund has already been generated for return " + returnId + ".");
        }
        returnProcess.approve(administrator);
        stockMovementService.restockForOrder(returnProcess.getOrder(), administrator);
        returnRepository.save(returnProcess);

        Refund refund = new Refund();
        refund.setId(UUID.randomUUID().toString());
        refund.setReturnProcess(returnProcess);
        refund.setAmount(returnProcess.getOrder().getTotalAmount());
        refund.setIssuedAt(LocalDateTime.now());
        return refundRepository.save(refund);
    }

    @Override
    public Return rejectReturn(String returnId, String administratorId) {
        Administrator administrator = requireAdministrator(administratorId, "rejectReturn");
        Return returnProcess = findReturnById(returnId);
        returnProcess.reject(administrator);
        return returnRepository.save(returnProcess);
    }

    @Override
    public Return findReturnById(String returnId) {
        return returnRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return", returnId));
    }

    @Override
    public List<Return> listPendingReturns(String administratorId) {
        requireAdministrator(administratorId, "listPendingReturns");
        return returnRepository.findAll().stream()
                .filter(returnProcess -> returnProcess.getStatus() == ReturnStatus.REQUESTED)
                .toList();
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

    private Administrator requireAdministrator(String userId, String action) {
        return (Administrator) accessGuard.requireActiveUser(userId, action, SystemRole.ADMINISTRATOR);
    }
}
