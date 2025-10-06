package Him.admin.Controllers;

import Him.admin.DTO.Transactions.TransactionRequestDTO;
import Him.admin.DTO.Transactions.TransactionResponseDTO;
import Him.admin.Models.Transaction;
import Him.admin.Services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = transactionService.createTransaction(transactionRequestDTO);

        TransactionResponseDTO responseDTO = mapToResponseDTO(transaction);
        return ResponseEntity.ok().body(responseDTO);
    }

    // Get all transactions
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Get all transactions with pagination
    @GetMapping("/paged")
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getAllTransactions(pageable);
        Page<TransactionResponseDTO> responseDTOs = transactions.map(this::mapToResponseDTO);
        return ResponseEntity.ok(responseDTOs);
    }

    // Get transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        TransactionResponseDTO responseDTO = mapToResponseDTO(transaction);
        return ResponseEntity.ok(responseDTO);
    }

    // Get transaction by RRN
    @GetMapping("/rrn/{rrn}")
    public ResponseEntity<TransactionResponseDTO> getTransactionByRrn(@PathVariable String rrn) {
        Transaction transaction = transactionService.getTransactionByRrn(rrn);
        TransactionResponseDTO responseDTO = mapToResponseDTO(transaction);
        return ResponseEntity.ok(responseDTO);
    }

    // Get transactions by member ID
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByMemberId(@PathVariable Long memberId) {
        List<Transaction> transactions = transactionService.getTransactionsByMemberId(memberId);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Get transactions by branch ID
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByBranchId(@PathVariable Long branchId) {
        List<Transaction> transactions = transactionService.getTransactionsByBranchId(branchId);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Get transactions by revenue head ID
    @GetMapping("/revenue-head/{revenueHeadId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByRevenueHeadId(@PathVariable Long revenueHeadId) {
        List<Transaction> transactions = transactionService.getTransactionsByRevenueHeadId(revenueHeadId);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Get transactions by payment method ID
    @GetMapping("/payment-method/{paymentMethodId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByPaymentMethod(@PathVariable Long paymentMethodId) {
        List<Transaction> transactions = transactionService.getTransactionsByPaymentMethod(paymentMethodId);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Get transactions by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByUser(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Get transactions by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Get recent transactions
    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponseDTO>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        List<Transaction> transactions = transactionService.getRecentTransactions(limit);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Update transaction (full update)
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = transactionService.updateTransaction(id, transactionRequestDTO);
        TransactionResponseDTO responseDTO = mapToResponseDTO(transaction);
        return ResponseEntity.ok(responseDTO);
    }

    // Partial update transaction
    @PatchMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> partialUpdateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = transactionService.partialUpdateTransaction(id, transactionRequestDTO);
        TransactionResponseDTO responseDTO = mapToResponseDTO(transaction);
        return ResponseEntity.ok(responseDTO);
    }

    // Delete transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    // Analytics - Get total amount by revenue head
    @GetMapping("/analytics/revenue-head/{revenueHeadId}/total")
    public ResponseEntity<BigDecimal> getTotalAmountByRevenueHead(@PathVariable Long revenueHeadId) {
        BigDecimal totalAmount = transactionService.getTotalAmountByRevenueHead(revenueHeadId);
        return ResponseEntity.ok(totalAmount);
    }

    // Analytics - Get total amount by branch
    @GetMapping("/analytics/branch/{branchId}/total")
    public ResponseEntity<BigDecimal> getTotalAmountByBranch(@PathVariable Long branchId) {
        BigDecimal totalAmount = transactionService.getTotalAmountByBranch(branchId);
        return ResponseEntity.ok(totalAmount);
    }

    // Analytics - Get total amount by member
    @GetMapping("/analytics/member/{memberId}/total")
    public ResponseEntity<BigDecimal> getTotalAmountByMember(@PathVariable Long memberId) {
        BigDecimal totalAmount = transactionService.getTotalAmountByMember(memberId);
        return ResponseEntity.ok(totalAmount);
    }

    // Analytics - Get transaction count by revenue head
    @GetMapping("/analytics/revenue-head/{revenueHeadId}/count")
    public ResponseEntity<Long> getTransactionCountByRevenueHead(@PathVariable Long revenueHeadId) {
        Long count = transactionService.getTransactionCountByRevenueHead(revenueHeadId);
        return ResponseEntity.ok(count);
    }

    // Check if transaction exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> transactionExists(@PathVariable Long id) {
        boolean exists = transactionService.transactionExists(id);
        return ResponseEntity.ok(exists);
    }

    // Helper method to map Transaction to TransactionResponseDTO
    // Helper method to map Transaction to TransactionResponseDTO with support for both revenue and project transactions
    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        // Safe handling for member name
        String payerName = "Unknown Member";
        if (transaction.getMember() != null) {
            String firstName = transaction.getMember().getFirstName() != null ? transaction.getMember().getFirstName() : "";
            String lastName = transaction.getMember().getLastName() != null ? transaction.getMember().getLastName() : "";
            payerName = (firstName + " " + lastName).trim();
            if (payerName.isEmpty()) payerName = "Unknown Member";
        }

        // Determine description based on transaction type
        String revenueHeadName;
        if (transaction.getRevenueHead() != null) {
            // Revenue transaction
            revenueHeadName = transaction.getRevenueHead().getName() != null ?
                    transaction.getRevenueHead().getName() : "Unknown Revenue Head";
        } else if (transaction.getProject() != null) {
            // Project contribution - prefix with "Project: "
            revenueHeadName = "Project: " + (transaction.getProject().getTitle() != null ?
                    transaction.getProject().getTitle() : "Unknown Project");
        } else {
            // Fallback for transactions with neither revenue head nor project
            revenueHeadName = "Unknown Transaction Type";
        }

        // Safe handling for branch
        String branchName = "Unknown Branch";
        String branchCode = "N/A";
        Long branchId = null;
        if (transaction.getBranch() != null) {
            branchName = transaction.getBranch().getBranchName() != null ?
                    transaction.getBranch().getBranchName() : "Unknown Branch";
            branchCode = transaction.getBranch().getBranchCode() != null ?
                    transaction.getBranch().getBranchCode() : "N/A";
            branchId = transaction.getBranch().getId();
        }

        // Safe handling for processed by user
        String processedBy = "System";
        if (transaction.getProcessedBy() != null && transaction.getProcessedBy().getUsername() != null) {
            processedBy = transaction.getProcessedBy().getUsername();
        }

        // Safe handling for payment method
        Long paymentMethodID = null;
        if (transaction.getPaymentMethod() != null) {
            paymentMethodID = transaction.getPaymentMethod().getId();
        }

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getRrn(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getCurrency(),
                payerName,
                revenueHeadName, // This now shows either revenue head name or project title
                branchName,
                branchCode,
                branchId,
                processedBy,
                paymentMethodID
        );
    }
}