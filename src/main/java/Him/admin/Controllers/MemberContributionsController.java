package Him.admin.Controllers;

import Him.admin.DTO.Contributions.ContributionRequestDTO;
import Him.admin.DTO.Contributions.MemberProjectContributionDTO;
import Him.admin.Models.Transaction;
import Him.admin.Services.MemberProjectContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contributions")
@RequiredArgsConstructor

public class MemberContributionsController {

    private final MemberProjectContributionService memberProjectContributionService;

    // ========== CREATE CONTRIBUTION (UPDATED) ==========

    /**
     * Create a new contribution - UPDATED RETURN TYPE
     */
    @PostMapping
    public ResponseEntity<?> createContribution(
            @Valid @RequestBody ContributionRequestDTO request) {
        try {
            MemberProjectContributionDTO contribution = memberProjectContributionService.createContribution(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(contribution);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create contribution: " + e.getMessage());
        }
    }

    /**
     * Update an existing contribution - UPDATED RETURN TYPE
     */
    @PutMapping("/{contributionId}")
    public ResponseEntity<?> updateContribution(
            @PathVariable Long contributionId,
            @Valid @RequestBody ContributionRequestDTO request) {
        try {
            // You'll need to create an update method that returns DTO in your service
            // For now, you can keep returning Transaction or create a similar update method
            Transaction contribution = memberProjectContributionService.updateContribution(contributionId, request);

            // Option 1: Return the entity (quick fix)
            return ResponseEntity.ok(contribution);

            // Option 2: Convert to DTO (better approach)
            // MemberProjectContributionDTO response = convertToDTO(contribution);
            // return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update contribution: " + e.getMessage());
        }
    }

    // ========== EXISTING GET ENDPOINTS (NO CHANGES NEEDED) ==========

    /**
     * Get all contributions for a specific project
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<MemberProjectContributionDTO>> getContributionsByProject(
            @PathVariable Long projectId) {
        try {
            List<MemberProjectContributionDTO> contributions =
                    memberProjectContributionService.getContributionsByProject(projectId);
            return ResponseEntity.ok(contributions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all projects a member has contributed to
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<MemberProjectContributionDTO>> getContributionsByMember(
            @PathVariable Long memberId) {
        try {
            List<MemberProjectContributionDTO> contributions =
                    memberProjectContributionService.getContributionsByMember(memberId);
            return ResponseEntity.ok(contributions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ... ALL OTHER GET ENDPOINTS REMAIN THE SAME ...

    /**
     * Get contribution by ID - UPDATED TO RETURN DTO
     */
    @GetMapping("/{contributionId}")
    public ResponseEntity<?> getContributionById(@PathVariable Long contributionId) {
        try {
            Transaction contribution = memberProjectContributionService.getContributionById(contributionId);
            if (contribution != null) {
                // Convert entity to DTO
                MemberProjectContributionDTO response = convertToDTO(contribution);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== HELPER METHOD ==========

    /**
     * Helper method to convert Transaction entity to MemberProjectContributionDTO
     */
    private MemberProjectContributionDTO convertToDTO(Transaction transaction) {
        return new MemberProjectContributionDTO(
                transaction.getId(),
                transaction.getProject().getId(),
                transaction.getProject().getTitle(),
                transaction.getMember().getId(),
                transaction.getMember().getFirstName(),
                transaction.getMember().getLastName(),
                transaction.getMember().getEmail(),
                transaction.getMember().getPhone(), // or "N/A" if phone doesn't exist
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getBranch().getBranchName(),
                transaction.getPaymentMethod() != null ? transaction.getPaymentMethod().getName() : "Unknown",
                transaction.getProcessedBy() != null ?
                        transaction.getProcessedBy().getFirstName() + " " + transaction.getProcessedBy().getLastName() : "System"
        );
    }
}