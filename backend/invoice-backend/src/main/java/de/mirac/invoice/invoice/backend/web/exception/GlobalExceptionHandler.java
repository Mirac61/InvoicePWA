package de.mirac.invoice.invoice.backend.web.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central error handler — converts exceptions into consistent JSON error responses.
 *
 * Every error response has the shape:
 * <pre>
 * {
 *   "timestamp": "2024-01-01T12:00:00Z",
 *   "status":    404,
 *   "error":     "Not Found",
 *   "message":   "Invoice with ID xyz not found."
 * }
 * </pre>
 *
 * Validation errors additionally include a "details" array:
 * <pre>
 * {
 *   "details": ["items: must not be null", "issueDate: must not be blank"]
 * }
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 — entity not found in the database. */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** 400 — business rule violations (e.g. empty items list, unknown itemId). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** 400 — Bean Validation errors on request DTOs (@NotNull, @NotBlank, etc.). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("details", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /** 500 — any other unexpected exception. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        // Log the real cause server-side; never expose stack traces to the client.
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
    }

    // -------------------------------------------------------------------------

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(buildBody(status, message));
    }

    private Map<String, Object> buildBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
