package application.domain.ports.input;

import application.domain.models.Refund;
import application.domain.models.Return;
import java.util.List;

/**
 * Manages the post-sale flow of returns and refunds.
 * Covers business objective OBJ-11.
 */
public interface ReturnRefundUseCase {

    Return requestReturn(String orderId, String buyerId, String reason);

    Refund generateRefund(String returnId, String administratorId);

    Return rejectReturn(String returnId, String administratorId);

    Return findReturnById(String returnId);

    List<Return> listPendingReturns(String administratorId);

    Refund findRefundById(String refundId);

    Refund findRefundByReturn(String returnId);
}
