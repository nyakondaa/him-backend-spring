package Him.admin.Repository;

import Him.admin.Models.Project;
import Him.admin.Models.Project.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(ProjectStatus status);

    // Branch-specific queries
    List<Project> findByBranchId(Long branchId);

    List<Project> findByBranchIdAndStatus(Long branchId, ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE p.currentFunding >= p.fundingGoal")
    List<Project> findFullyFundedProjects();

    @Query("SELECT p FROM Project p WHERE p.currentFunding < p.fundingGoal AND p.status = 'ACTIVE'")
    List<Project> findActiveProjectsNeedingFunding();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.project.id = :projectId")
    BigDecimal getTotalContributionsByProjectId(@Param("projectId") Long projectId);

    // Branch-specific funding queries
    @Query("SELECT p FROM Project p WHERE p.branch.id = :branchId AND p.currentFunding >= p.fundingGoal")
    List<Project> findFullyFundedProjectsByBranch(@Param("branchId") Long branchId);

    @Query("SELECT SUM(p.currentFunding) FROM Project p WHERE p.branch.id = :branchId")
    BigDecimal getTotalFundingByBranch(@Param("branchId") Long branchId);
}