package Him.admin.Controllers;

import Him.admin.DTO.RevenueHeadDTO.RevenueHeadRequestDTO;
import Him.admin.DTO.RevenueHeadDTO.RevenueHeadResponseDTO;
import Him.admin.Models.RevenueHeads;
import Him.admin.Services.RevenueHeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/revenue-heads")
public class RevenueHeadController {
    private final RevenueHeadService revenueHeadService;

    @PostMapping
    public ResponseEntity<RevenueHeadResponseDTO> createRevenueHead(@Valid @RequestBody RevenueHeadRequestDTO dto) {
        RevenueHeads revenueHeads = revenueHeadService.createRevenueHeads(dto.name(), dto.description(), dto.code(), dto.branchID());

        RevenueHeadResponseDTO responseDTO = new RevenueHeadResponseDTO(
                revenueHeads.getId(),
                revenueHeads.getName(),
                revenueHeads.getCode(),
                revenueHeads.getDescription(),
                revenueHeads.getBranch().getBranchName(),
                revenueHeads.getBranch().getId(),
                revenueHeads.getBranch().getBranchCode()
        );

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<RevenueHeadResponseDTO>> getAllRevenueHeads() {
        List<RevenueHeads> revenueHeads = revenueHeadService.getAllRevenueHeads();

        List<RevenueHeadResponseDTO> responseDTOs = revenueHeads.stream()
                .map(head -> new RevenueHeadResponseDTO(
                        head.getId(),
                        head.getName(),
                        head.getCode(),
                        head.getDescription(),
                        head.getBranch().getBranchName(),
                        head.getBranch().getId(),
                        head.getBranch().getBranchCode()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    // Get revenue heads by branch ID
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<RevenueHeadResponseDTO>> getRevenueHeadsByBranch(@PathVariable Long branchId) {
        List<RevenueHeads> revenueHeads = revenueHeadService.getRevenueHeadsByBranchId(branchId);

        List<RevenueHeadResponseDTO> responseDTOs = revenueHeads.stream()
                .map(head -> new RevenueHeadResponseDTO(
                        head.getId(),
                        head.getName(),
                        head.getCode(),
                        head.getDescription(),
                        head.getBranch().getBranchName(),
                        head.getBranch().getId(),
                        head.getBranch().getBranchCode()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RevenueHeadResponseDTO> updateRevenueHead(
            @Valid @RequestBody RevenueHeadRequestDTO dto, @PathVariable Long id) {
        RevenueHeads revenueHeads = revenueHeadService.updateRevenueHeads(dto.name(), dto.description(), dto.code(), id);

        RevenueHeadResponseDTO responseDTO = new RevenueHeadResponseDTO(
                revenueHeads.getId(),
                revenueHeads.getName(),
                revenueHeads.getCode(),
                revenueHeads.getDescription(),
                revenueHeads.getBranch().getBranchName(),
                revenueHeads.getBranch().getId(),
                revenueHeads.getBranch().getBranchCode()
        );

        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRevenueHead(@PathVariable Long id) {
        revenueHeadService.deleteRevenueHead(id);
        return ResponseEntity.noContent().build();
    }
}