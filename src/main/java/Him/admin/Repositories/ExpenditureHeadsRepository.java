package Him.admin.Repositories;

import Him.admin.Models.Expenditure;
import Him.admin.Models.ExpenditureHead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureHeadsRepository extends JpaRepository<ExpenditureHead, Long> {

    boolean existsByName(String name);

    boolean existsByCode(String code);
}
