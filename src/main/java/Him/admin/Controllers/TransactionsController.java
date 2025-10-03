package Him.admin.Controllers;

import Him.admin.DTO.Transactions.TransactionRequestDTO;
import Him.admin.DTO.Transactions.TransactionResponseDTO;
import Him.admin.Models.Transaction;
import Him.admin.Repositories.TransactionRepository;
import Him.admin.Services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;


    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = transactionService.createTransaction(transactionRequestDTO);
        System.out.println("createTransaction");


        TransactionResponseDTO responseDTO = new TransactionResponseDTO(
                transaction.getRrn(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getCurrency(),
                transaction.getPayerName(),
                transaction.getRevenueHead().getName(),
                transaction.getBranch().getBranchName(),
                transaction.getProcessedBy().getUsername()




        );

        return ResponseEntity.ok().body(responseDTO);

    }
}
