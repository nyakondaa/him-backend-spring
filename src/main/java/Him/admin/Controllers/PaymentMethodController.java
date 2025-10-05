package Him.admin.Controllers;

import Him.admin.DTO.PaymentsDTO.PaymentMethodRequestDTO;
import Him.admin.DTO.PaymentsDTO.PaymentMethodResponseDTO;
import Him.admin.Models.PaymentMethod;
import Him.admin.Services.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<PaymentMethodResponseDTO> createPaymentMethod(
            @Valid @RequestBody PaymentMethodRequestDTO dto) {
        PaymentMethodResponseDTO createdPaymentMethod = paymentMethodService.createPaymentMethod(dto);
        return new ResponseEntity<>(createdPaymentMethod, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethodResponseDTO>> getAllPaymentMethods() {
        List<PaymentMethodResponseDTO> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return ResponseEntity.ok(paymentMethods);
    }

    @GetMapping("/simple")
    public ResponseEntity<List<PaymentMethod>> getAllPaymentMethodsSimple() {
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethodsSimple();
        return ResponseEntity.ok(paymentMethods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponseDTO> getPaymentMethodById(@PathVariable Long id) {
        PaymentMethodResponseDTO paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(paymentMethod);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodResponseDTO> updatePaymentMethod(
            @PathVariable Long id,
            @Valid @RequestBody PaymentMethodRequestDTO dto) {
        PaymentMethodResponseDTO updatedPaymentMethod = paymentMethodService.updatePaymentMethod(id, dto);
        return ResponseEntity.ok(updatedPaymentMethod);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }

    // Exception handling for the controller
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}