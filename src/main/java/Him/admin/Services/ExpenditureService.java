package Him.admin.Services;

import Him.admin.Models.Branch;
import Him.admin.Models.Expenditure;
import Him.admin.Models.ExpenditureHead;
import Him.admin.Models.User;
import Him.admin.Repositories.BranchRepository;
import Him.admin.Repositories.ExpenditureHeadRepository;
import Him.admin.Repositories.ExpenditureRepository;
import Him.admin.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final BranchRepository branchRepository;
    private final ExpenditureHeadRepository expenditureHeadRepository;
    private final UserRepository userRepository;

    public Expenditure createExpenditure(String description, Double amount, LocalDate date, Long branchId, Long expenditurHeadID, Long UserId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        ExpenditureHead expenditureHead = expenditureHeadRepository.findById(expenditurHeadID).orElseThrow(
                () -> new RuntimeException("expenditure head not found")
        );

        User user = userRepository.findById(UserId).orElseThrow(
                () -> new RuntimeException("user not found")
        );

        Expenditure expenditure = Expenditure.builder()
                .description(description)
                .amount(BigDecimal.valueOf(amount))
                .expenditureDate(date.atStartOfDay())
                .branch(branch)
                .expenditureHead(expenditureHead)
                .approvedBy(user)
                .build();

        return expenditureRepository.save(expenditure);
    }

    public List<Expenditure> getAll() {
        return expenditureRepository.findAll();
    }

    public Optional<Expenditure> getById(Long id) {
        return expenditureRepository.findById(id);
    }

    public void deleteExpenditure(Long id) {
        expenditureRepository.deleteById(id);
    }

}
