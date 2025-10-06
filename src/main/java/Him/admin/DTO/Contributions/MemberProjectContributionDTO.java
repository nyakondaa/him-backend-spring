package Him.admin.DTO.Contributions;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Builder
public record MemberProjectContributionDTO(
        Long id,
        Long projectId,
        String projectTitle,
        Long memberId,
        String memberFirstName,
        String memberLastName,
        String memberEmail,
        String memberPhone,
        BigDecimal amount,
        LocalDateTime contributionDate,
        String branchName,
        String paymentMethod,
        String processedBy


        // Additional useful fields

) {}