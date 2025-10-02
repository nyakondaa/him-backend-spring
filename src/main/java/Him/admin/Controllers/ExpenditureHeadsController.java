package Him.admin.Controllers;


import Him.admin.DTO.Ependitures.ExpenditureRequestDTO;
import Him.admin.DTO.Ependitures.ExpenditureResponseDTO;
import Him.admin.DTO.ExpenditureHeadsDTO.ExpenditureHeadRequestDTO;
import Him.admin.DTO.ExpenditureHeadsDTO.ExpenditureHeadsResponse;
import Him.admin.Models.Expenditure;
import Him.admin.Models.ExpenditureHead;
import Him.admin.Repositories.ExpenditureHeadsRepository;
import Him.admin.Services.ExpenditureHeadsService;
import Him.admin.Services.ExpenditureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/expenditureheads")
public class ExpenditureHeadsController {
    private final ExpenditureHeadsService expenditureHeadsService;

    @PostMapping
    public ResponseEntity<ExpenditureHeadsResponse> createExpenditureHead(@Valid @RequestBody ExpenditureHeadRequestDTO dto) {
        ExpenditureHead expenditureHead = expenditureHeadsService.createExpenditureHead(

                dto.name(),
                dto.code(),
                dto.description(),
                dto.bracnhID()
        );

        ExpenditureHeadsResponse response = new ExpenditureHeadsResponse(
                expenditureHead.getName(),
                expenditureHead.getCode(),
                expenditureHead.getDescription()

        );

        return ResponseEntity.ok(response);

    }


}
