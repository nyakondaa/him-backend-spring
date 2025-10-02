package Him.admin.Controllers;


import Him.admin.DTO.Branches.BranchRequest;
import Him.admin.DTO.Branches.BranchResponse;
import Him.admin.Models.Branch;
import Him.admin.Services.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    // Create
    @PostMapping
    public ResponseEntity<BranchResponse> createBranch(@Valid @RequestBody BranchRequest dto) {
        Branch branch = branchService.createBranch(
                Branch.builder()
                        .branchName(dto.branchName())
                        .branchAddress(dto.branchAddress())
                        .branchPhone(dto.branchPhone())
                        .branchEmail(dto.branchEmail())
                        .branchCode(dto.branchCode())
                        .build()
        );

        return ResponseEntity.ok(toDTO(branch));
    }

    // Read all
    @GetMapping
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        return ResponseEntity.ok(
                branchService.getAllBranches().stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(branchService.getBranchById(id)));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> updateBranch(@PathVariable Long id,
                                                          @Valid @RequestBody BranchResponse dto) {
        Branch updatedBranch = Branch.builder()
                .branchName(dto.branchName())
                .branchAddress(dto.branchAddress())
                .branchPhone(dto.branchPhone())
                .branchEmail(dto.branchEmail())
                .branchCode(dto.branchCode())
                .build();

        return ResponseEntity.ok(toDTO(branchService.updateBranch(id, updatedBranch)));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }

    private BranchResponse toDTO(Branch branch) {
        return new BranchResponse(
                branch.getId(),
                branch.getBranchName(),
                branch.getBranchAddress(),
                branch.getBranchPhone(),
                branch.getBranchEmail(),
                branch.getBranchCode()
        );
    }
}
