package Him.admin.DTO.ProjectsDTO;

import Him.admin.Models.Project.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProjectResponseDTO(
        Long id,
        String title,
        String description,
        BigDecimal fundingGoal,
        BigDecimal currentFunding,
        ProjectStatus status,
        Long branchId,
        String branchName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    // Helper method to calculate progress percentage
    public BigDecimal fundingProgress() {
        if (fundingGoal == null || fundingGoal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentFunding.divide(fundingGoal, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}