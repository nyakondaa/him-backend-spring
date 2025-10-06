package Him.admin.DTO.RevenueHeadDTO;

public record RevenueHeadResponseDTO(
        Long id,
        String name,
        String code,
        String description,
        String branchName,
        Long branchId,
        String branchCode
) {
}
