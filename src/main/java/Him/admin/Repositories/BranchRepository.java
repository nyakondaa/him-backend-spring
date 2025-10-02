package Him.admin.Repositories;


import Him.admin.Models.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    // Optional custom query methods can go here
}
