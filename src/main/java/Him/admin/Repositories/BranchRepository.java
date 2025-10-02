package Him.admin.Repositories;


import Him.admin.Models.Branch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    boolean existsByBranchName(@NotBlank(message = "Branch name is required") @Size(min = 2, max = 100, message = "Branch name must be between 2 and 100 characters") String branchName);

    boolean existsByBranchCode(@NotBlank(message = "Branch code is required") @Size(min = 2, max = 10, message = "Branch code must be between 2 and 10 characters") String branchCode);
    // Optional custom query methods can go here
}
