package Him.admin.Controllers;

import Him.admin.DTO.Ependitures.ExpenditureRequestDTO;
import Him.admin.DTO.Ependitures.ExpenditureResponseDTO;
import Him.admin.Models.Expenditure;
import Him.admin.Services.ExpenditureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenditures")
@RequiredArgsConstructor
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    // 1️⃣ Create expenditure
    @PostMapping
    public ResponseEntity<ExpenditureResponseDTO> createExpenditure(@Valid @RequestBody ExpenditureRequestDTO dto) {
        Expenditure expenditure = expenditureService.createExpenditure(
                dto.description(),
                dto.amount(),
                dto.date(),
                dto.branchId(),
                dto.ExpenditureHeadID(), // ExpenditureHead ID (adjust as needed)
                dto.userID()
        );

        ExpenditureResponseDTO response = mapToResponseDTO(expenditure);
        return ResponseEntity.ok(response);
    }

    // 2️⃣ Get all expenditures
    @GetMapping
    public ResponseEntity<List<ExpenditureResponseDTO>> getAllExpenditures() {
        List<ExpenditureResponseDTO> expenditures = expenditureService.getAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenditures);
    }

    // 3️⃣ Get expenditure by ID
    @GetMapping("/{id}")
    public ResponseEntity<ExpenditureResponseDTO> getExpenditureById(@PathVariable Long id) {
        return expenditureService.getById(id)
                .map(this::mapToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4️⃣ Delete expenditure
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenditure(@PathVariable Long id) {
        expenditureService.deleteExpenditure(id);
        return ResponseEntity.noContent().build();
    }

    // Utility method to map entity to DTO
    private ExpenditureResponseDTO mapToResponseDTO(Expenditure exp) {
        return new ExpenditureResponseDTO(
                exp.getId(),
                exp.getDescription(),
                exp.getAmount().doubleValue(),
                exp.getExpenditureDate().toLocalDate(),
                exp.getExpenditureHead().getName(),
                exp.getBranch().getBranchName()
        );
    }

}
