package Him.admin.Services;

import Him.admin.DTO.Transactions.TransactionRequestDTO;
import Him.admin.DTO.Transactions.TransactionResponseDTO;
import Him.admin.Exceptions.ResourceAlreadyExistsException;
import Him.admin.Exceptions.ResourceNotFoundException;
import Him.admin.Models.Transaction;
import Him.admin.Repositories.BranchRepository;
import Him.admin.Repositories.RevenueHeadRepository;
import Him.admin.Repositories.TransactionRepository;
import Him.admin.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final RevenueHeadRepository revenueHeadRepository;


    public Transaction createTransaction(TransactionRequestDTO dto) {

        // Fetch related entities and throw ResourceNotFoundException if not found
        var branch = branchRepository.findById(dto.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", dto.branchId().toString()));

        var revenueHead = revenueHeadRepository.findById(dto.revenueHeadId())
                .orElseThrow(() -> new ResourceNotFoundException("RevenueHead", "id", dto.revenueHeadId().toString()));

        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.userId().toString()));

        log.info(branch.toString(), revenueHead.toString(), user.toString());
        System.out.println("wsit");

        // Generate a unique RRN
        String rrn = generateUniqueRrn();

        // Build the transaction
        Transaction transaction = Transaction.builder()
                .amount(dto.amount())
                .transactionDate(dto.transactionDate())
                .currency(dto.currency())
                .payerName(dto.payerName())
                .rrn(rrn)
                .branch(branch)
                .revenueHead(revenueHead)
                .processedBy(user)
                .build();

        return transactionRepository.save(transaction);
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
