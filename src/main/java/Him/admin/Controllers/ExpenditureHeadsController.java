package Him.admin.Controllers;

import Him.admin.DTO.ExpenditureHeadsDTO.ExpenditureHeadRequestDTO;
import Him.admin.DTO.ExpenditureHeadsDTO.ExpenditureHeadsResponse;
import Him.admin.Models.ExpenditureHead;
import Him.admin.Services.ExpenditureHeadsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/expenditure-heads")
public class ExpenditureHeadsController {
    private final ExpenditureHeadsService expenditureHeadsService;

    @PostMapping
    public ResponseEntity<ExpenditureHeadsResponse> createExpenditureHead(@Valid @RequestBody ExpenditureHeadRequestDTO dto) {
        ExpenditureHead expenditureHead = expenditureHeadsService.createExpenditureHead(
                dto.name(),
                dto.code(),
                dto.description(),
                dto.branchID()
        );

        ExpenditureHeadsResponse response = new ExpenditureHeadsResponse(
                expenditureHead.getId(),
                expenditureHead.getName(),
                expenditureHead.getCode(),
                expenditureHead.getDescription()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExpenditureHeadsResponse>> getAllExpenditureHeads() {
        List<ExpenditureHead> expenditureHeads = expenditureHeadsService.getAllExpenditureHeads();

        List<ExpenditureHeadsResponse> response = expenditureHeads.stream()
                .map(head -> new ExpenditureHeadsResponse(
                        head.getId(),

                        head.getName(),
                        head.getCode(),
                        head.getDescription()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenditureHeadsResponse> getExpenditureHeadById(@PathVariable Long id) {
        ExpenditureHead expenditureHead = expenditureHeadsService.getExpenditureHeadById(id);

        ExpenditureHeadsResponse response = new ExpenditureHeadsResponse(
                expenditureHead.getId(),

                expenditureHead.getName(),
                expenditureHead.getCode(),
                expenditureHead.getDescription()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenditureHeadsResponse> updateExpenditureHead(
            @PathVariable Long id,
            @Valid @RequestBody ExpenditureHeadRequestDTO dto) {

        ExpenditureHead expenditureHead = expenditureHeadsService.updateExpenditureHead(
                id,
                dto.name(),
                dto.code(),
                dto.description(),
                dto.branchID()
        );

        ExpenditureHeadsResponse response = new ExpenditureHeadsResponse(
                expenditureHead.getId(),

                expenditureHead.getName(),
                expenditureHead.getCode(),
                expenditureHead.getDescription()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenditureHead(@PathVariable Long id) {
        expenditureHeadsService.deleteExpenditureHeadById(id);
        return ResponseEntity.noContent().build();
    }

    // Exception handling for better error responses
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}