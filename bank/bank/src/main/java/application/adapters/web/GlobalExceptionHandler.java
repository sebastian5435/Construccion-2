package application.adapters.web;

import application.adapters.web.dto.response.ErrorResponse;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.DuplicateEmailException;
import application.domain.exceptions.DuplicateIdentifierException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.InvalidOrderStateTransitionException;
import application.domain.exceptions.NegativeInventoryException;
import application.domain.exceptions.OrderAlreadyFinalizedException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.exceptions.UnauthorizedRoleException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Translates domain and request errors into consistent HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException exception,
                                                               HttpServletRequest request) {
        return build(statusFor(exception), exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                          HttpServletRequest request) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "The request has invalid fields.", request, details);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMalformedRequest(Exception exception, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "The request body or parameters are malformed.", request, List.of());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException exception,
                                                             HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED,
                "The header " + exception.getHeaderName() + " identifying the requester is required.",
                request, List.of());
    }

    private HttpStatus statusFor(DomainException exception) {
        if (exception instanceof EntityNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (exception instanceof UnauthorizedRoleException) {
            return HttpStatus.FORBIDDEN;
        }
        if (exception instanceof DuplicateEmailException
                || exception instanceof DuplicateIdentifierException
                || exception instanceof InvalidOrderStateTransitionException
                || exception instanceof OrderAlreadyFinalizedException
                || exception instanceof NegativeInventoryException
                || exception instanceof ProductNotAvailableException) {
            return HttpStatus.CONFLICT;
        }
        if (exception instanceof InvalidOperationException) {
            return HttpStatus.UNPROCESSABLE_CONTENT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request,
                                                List<String> details) {
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                message, request.getRequestURI(), details);
        return ResponseEntity.status(status).body(body);
    }
}
