package Him.admin.Services;

import Him.admin.DTO.Transactions.TransactionRequestDTO;
import Him.admin.DTO.Transactions.TransactionResponseDTO;
import Him.admin.Exceptions.ResourceAlreadyExistsException;
import Him.admin.Exceptions.ResourceNotFoundException;
import Him.admin.Models.Transaction;
import Him.admin.Repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final RevenueHeadRepository revenueHeadRepository;
    private final MemberRepository memberRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public Transaction createTransaction(TransactionRequestDTO dto) {
        // Fetch related entities and throw ResourceNotFoundException if not found
        var branch = branchRepository.findById(dto.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", dto.branchId().toString()));

        var revenueHead = revenueHeadRepository.findById(dto.revenueHeadId())
                .orElseThrow(() -> new ResourceNotFoundException("RevenueHead", "id", dto.revenueHeadId().toString()));

        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.userId().toString()));

        var member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", dto.memberId().toString()));

        var payment = paymentMethodRepository.findById(dto.paymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", dto.paymentMethodId().toString()));

        // Generate a unique RRN
        String rrn = generateUniqueRrn();

        // Build the transaction
        Transaction transaction = Transaction.builder()
                .amount(dto.amount())
                .transactionDate(dto.transactionDate())
                .currency(dto.currency())
                .paymentMethod(payment)
                .member(member)
                .rrn(rrn)
                .branch(branch)
                .revenueHead(revenueHead)
                .processedBy(user)
                .build();

        return transactionRepository.save(transaction);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get all transactions with pagination
    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    // Get transaction by ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id.toString()));
    }

    // Get transactions by member ID
    public List<Transaction> getTransactionsByMemberId(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member", "id", memberId.toString());
        }
        return transactionRepository.findByMemberId(memberId);
    }

    // Get transactions by branch ID
    public List<Transaction> getTransactionsByBranchId(Long branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch", "id", branchId.toString());
        }
        return transactionRepository.findByBranchId(branchId);
    }

    // Get transactions by revenue head ID
    public List<Transaction> getTransactionsByRevenueHeadId(Long revenueHeadId) {
        if (!revenueHeadRepository.existsById(revenueHeadId)) {
            throw new ResourceNotFoundException("RevenueHead", "id", revenueHeadId.toString());
        }
        return transactionRepository.findByRevenueHeadId(revenueHeadId);
    }

    // Get transactions by date range
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    // Get transactions by payment method
    public List<Transaction> getTransactionsByPaymentMethod(Long paymentMethodId) {
        if (!paymentMethodRepository.existsById(paymentMethodId)) {
            throw new ResourceNotFoundException("PaymentMethod", "id", paymentMethodId.toString());
        }
        return transactionRepository.findByPaymentMethodId(paymentMethodId);
    }

    // Get transactions by user (processed by)
    public List<Transaction> getTransactionsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId.toString());
        }
        return transactionRepository.findByProcessedById(userId);
    }

    // Update transaction
    public Transaction updateTransaction(Long id, TransactionRequestDTO dto) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id.toString()));

        // Fetch related entities
        var branch = branchRepository.findById(dto.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", dto.branchId().toString()));

        var revenueHead = revenueHeadRepository.findById(dto.revenueHeadId())
                .orElseThrow(() -> new ResourceNotFoundException("RevenueHead", "id", dto.revenueHeadId().toString()));

        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.userId().toString()));

        var member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", dto.memberId().toString()));

        var payment = paymentMethodRepository.findById(dto.paymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", dto.paymentMethodId().toString()));

        // Update transaction fields
        existingTransaction.setAmount(dto.amount());
        existingTransaction.setTransactionDate(dto.transactionDate());
        existingTransaction.setCurrency(dto.currency());
        existingTransaction.setPaymentMethod(payment);
        existingTransaction.setMember(member);
        existingTransaction.setBranch(branch);
        existingTransaction.setRevenueHead(revenueHead);
        existingTransaction.setProcessedBy(user);

        return transactionRepository.save(existingTransaction);
    }

    // Partial update transaction
    public Transaction partialUpdateTransaction(Long id, TransactionRequestDTO dto) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id.toString()));

        // Update only the fields that are provided (not null)
        if (dto.amount() != null) {
            existingTransaction.setAmount(dto.amount());
        }
        if (dto.transactionDate() != null) {
            existingTransaction.setTransactionDate(dto.transactionDate());
        }
        if (dto.currency() != null) {
            existingTransaction.setCurrency(dto.currency());
        }
        if (dto.paymentMethodId() != null) {
            var payment = paymentMethodRepository.findById(dto.paymentMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", dto.paymentMethodId().toString()));
            existingTransaction.setPaymentMethod(payment);
        }
        if (dto.memberId() != null) {
            var member = memberRepository.findById(dto.memberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member", "id", dto.memberId().toString()));
            existingTransaction.setMember(member);
        }
        if (dto.branchId() != null) {
            var branch = branchRepository.findById(dto.branchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", dto.branchId().toString()));
            existingTransaction.setBranch(branch);
        }
        if (dto.revenueHeadId() != null) {
            var revenueHead = revenueHeadRepository.findById(dto.revenueHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("RevenueHead", "id", dto.revenueHeadId().toString()));
            existingTransaction.setRevenueHead(revenueHead);
        }
        if (dto.userId() != null) {
            var user = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.userId().toString()));
            existingTransaction.setProcessedBy(user);
        }

        return transactionRepository.save(existingTransaction);
    }

    // Delete transaction
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id.toString()));
        transactionRepository.delete(transaction);
    }

    // Get total amount by revenue head
    public BigDecimal getTotalAmountByRevenueHead(Long revenueHeadId) {
        if (!revenueHeadRepository.existsById(revenueHeadId)) {
            throw new ResourceNotFoundException("RevenueHead", "id", revenueHeadId.toString());
        }
        return transactionRepository.sumAmountByRevenueHeadId(revenueHeadId);
    }

    // Get total amount by branch
    public BigDecimal getTotalAmountByBranch(Long branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch", "id", branchId.toString());
        }
        return transactionRepository.sumAmountByBranchId(branchId);
    }

    // Get total amount by member
    public BigDecimal getTotalAmountByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member", "id", memberId.toString());
        }
        return transactionRepository.sumAmountByMemberId(memberId);
    }

    // Get transaction count by revenue head
    public Long getTransactionCountByRevenueHead(Long revenueHeadId) {
        if (!revenueHeadRepository.existsById(revenueHeadId)) {
            throw new ResourceNotFoundException("RevenueHead", "id", revenueHeadId.toString());
        }
        return transactionRepository.countByRevenueHeadId(revenueHeadId);
    }

    // Get recent transactions
    public List<Transaction> getRecentTransactions(int limit) {
        return transactionRepository.findTopNByOrderByTransactionDateDesc(limit);
    }

    // Search transactions by RRN
    public Transaction getTransactionByRrn(String rrn) {
        return transactionRepository.findByRrn(rrn)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "RRN", rrn));
    }

    // Check if transaction exists by ID
    public boolean transactionExists(Long id) {
        return transactionRepository.existsById(id);
    }

    public String generateUniqueRrn() {
        String rrn;
        SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        do {
            StringBuilder sb = new StringBuilder();
            // Example: 12-character RRN
            for (int i = 0; i < 12; i++) {
                sb.append(characters.charAt(random.nextInt(characters.length())));
            }
            rrn = sb.toString();
            // Check if it already exists in DB
        } while (transactionRepository.findByRrn(rrn).isPresent());

        return rrn;
    }
}