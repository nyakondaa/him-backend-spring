package Him.admin.Repositories;

import Him.admin.Models.RevenueHeads;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RevenueHeadRepository extends JpaRepository<RevenueHeads, Long> {
    Optional<RevenueHeads> findByName(String name);
}
