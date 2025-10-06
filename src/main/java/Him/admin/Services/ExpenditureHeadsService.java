package Him.admin.Services;

import Him.admin.Models.Branch;
import Him.admin.Models.ExpenditureHead;
import Him.admin.Repositories.BranchRepository;

import Him.admin.Repositories.ExpenditureHeadsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenditureHeadsService {

    private final ExpenditureHeadsRepository expenditureHeadsRepository;
    private  final BranchRepository  branchRepository;

    public ExpenditureHead createExpenditureHead( String name, String code, String description, Long branchID) {
        Branch branch = branchRepository.findById(branchID).orElseThrow(EntityNotFoundException::new);
        ExpenditureHead expenditureHead = ExpenditureHead.builder(  )
                .branch(branch)
                .code(code)
                .description(description)
                .name(name)
                .build();

        expenditureHeadsRepository.save(expenditureHead);
        return expenditureHead;

    }

    public ExpenditureHead updateExpenditureHead(Long expenditureHeadId, String name, String code, String description, Long branchID) {
        // Find by expenditure head ID, not branch ID!
        ExpenditureHead expenditureHead = expenditureHeadsRepository.findById(expenditureHeadId)
                .orElseThrow(() -> new EntityNotFoundException("Expenditure head not found with id: " + expenditureHeadId));

        // Check if branch exists
        Branch branch = branchRepository.findById(branchID)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with id: " + branchID));

        // Check for duplicate names (if name is being changed)
        if (!expenditureHead.getName().equals(name) &&
                expenditureHeadsRepository.existsByName(name)) {
            throw new IllegalArgumentException("Expenditure head with name '" + name + "' already exists");
        }

        // Check for duplicate codes (if code is being changed)
        if (!expenditureHead.getCode().equals(code) &&
                expenditureHeadsRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Expenditure head with code '" + code + "' already exists");
        }

        // Update the fields
        expenditureHead.setName(name);
        expenditureHead.setCode(code);
        expenditureHead.setDescription(description);
        expenditureHead.setBranch(branch);

        return expenditureHeadsRepository.save(expenditureHead);
    }

    public List<ExpenditureHead> getAllExpenditureHeads() {
        return expenditureHeadsRepository.findAll();
    }

    public ExpenditureHead getExpenditureHeadById(Long id) {
        return expenditureHeadsRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void deleteExpenditureHeadById(Long id) {
        expenditureHeadsRepository.deleteById(id);
    }



}
