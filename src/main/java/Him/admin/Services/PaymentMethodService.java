package Him.admin.Services;

import Him.admin.DTO.PaymentsDTO.PaymentMethodRequestDTO;
import Him.admin.DTO.PaymentsDTO.PaymentMethodResponseDTO;
import Him.admin.Models.PaymentMethod;
import Him.admin.Repositories.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodResponseDTO createPaymentMethod(PaymentMethodRequestDTO dto) {
        // Check if payment method with same name already exists
        if (paymentMethodRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("Payment method with name '" + dto.name() + "' already exists");
        }

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .name(dto.name())
                .details(dto.details())
                .build();

        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return convertToResponseDTO(savedPaymentMethod);
    }

    public List<PaymentMethodResponseDTO> getAllPaymentMethods() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentMethod> getAllPaymentMethodsSimple() {
        return paymentMethodRepository.findAll();
    }

    public PaymentMethodResponseDTO getPaymentMethodById(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found with id: " + id));
        return convertToResponseDTO(paymentMethod);
    }

    public PaymentMethodResponseDTO updatePaymentMethod(Long id, PaymentMethodRequestDTO dto) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!paymentMethod.getName().equals(dto.name()) &&
                paymentMethodRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("Payment method with name '" + dto.name() + "' already exists");
        }

        paymentMethod.setName(dto.name());
        paymentMethod.setDetails(dto.details());

        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return convertToResponseDTO(updatedPaymentMethod);
    }

    public void deletePaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found with id: " + id));

        // Check if payment method is being used in any transactions
        if (paymentMethod.getTransactions() != null && !paymentMethod.getTransactions().isEmpty()) {
            throw new IllegalStateException("Cannot delete payment method. It is being used in " +
                    paymentMethod.getTransactions().size() + " transaction(s).");
        }

        paymentMethodRepository.delete(paymentMethod);
    }

    // Add the missing convertToResponseDTO method
    private PaymentMethodResponseDTO convertToResponseDTO(PaymentMethod paymentMethod) {
        return new PaymentMethodResponseDTO(
                paymentMethod.getId(),
                paymentMethod.getName(),
                paymentMethod.getDetails()
        );
    }
}