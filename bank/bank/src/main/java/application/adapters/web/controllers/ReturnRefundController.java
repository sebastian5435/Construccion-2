package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.request.ReturnRequest;
import application.adapters.web.dto.response.RefundResponse;
import application.adapters.web.dto.response.ReturnResponse;
import application.domain.ports.input.ReturnRefundUseCase;
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
public class ReturnRefundController {

    private final ReturnRefundUseCase returnRefund;

    public ReturnRefundController(ReturnRefundUseCase returnRefund) {
        this.returnRefund = returnRefund;
    }

    @PostMapping("/returns")
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnResponse requestReturn(@RequestHeader(RequestHeaders.USER_ID) String buyerId,
                                        @Valid @RequestBody ReturnRequest request) {
        return ReturnResponse.from(returnRefund.requestReturn(request.orderId(), buyerId, request.reason()));
    }

    @PostMapping("/returns/{returnId}/refund")
    @ResponseStatus(HttpStatus.CREATED)
    public RefundResponse generateRefund(@RequestHeader(RequestHeaders.USER_ID) String administratorId,
                                         @PathVariable String returnId) {
        return RefundResponse.from(returnRefund.generateRefund(returnId, administratorId));
    }

    @PatchMapping("/returns/{returnId}/reject")
    public ReturnResponse rejectReturn(@RequestHeader(RequestHeaders.USER_ID) String administratorId,
                                       @PathVariable String returnId) {
        return ReturnResponse.from(returnRefund.rejectReturn(returnId, administratorId));
    }

    @GetMapping("/returns/pending")
    public List<ReturnResponse> listPendingReturns(@RequestHeader(RequestHeaders.USER_ID) String administratorId) {
        return returnRefund.listPendingReturns(administratorId).stream().map(ReturnResponse::from).toList();
    }

    @GetMapping("/returns/{returnId}")
    public ReturnResponse findReturnById(@PathVariable String returnId) {
        return ReturnResponse.from(returnRefund.findReturnById(returnId));
    }

    @GetMapping("/returns/{returnId}/refund")
    public RefundResponse findRefundByReturn(@PathVariable String returnId) {
        return RefundResponse.from(returnRefund.findRefundByReturn(returnId));
    }

    @GetMapping("/refunds/{refundId}")
    public RefundResponse findRefundById(@PathVariable String refundId) {
        return RefundResponse.from(returnRefund.findRefundById(refundId));
    }
}
