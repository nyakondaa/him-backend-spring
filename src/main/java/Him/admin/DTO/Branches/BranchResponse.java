package Him.admin.DTO.Branches;

public record BranchResponse(
        Long id,
        String branchName,
        String branchAddress,
        String branchPhone,
        String branchEmail,
        String branchCode
) {
}
