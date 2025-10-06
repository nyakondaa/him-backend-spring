package Him.admin.DTO.ProjectsDTO;
import Him.admin.Models.Project.ProjectStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProjectRequestDTO(
        @NotBlank(message = "Project title is required")
        String title,

        String description,

        @NotNull(message = "Funding goal is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Funding goal must be greater than 0")
        BigDecimal fundingGoal,

        @NotNull(message = "Branch ID is required")
        Long branchId,

        ProjectStatus status
) {}