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

    public ExpenditureHead updateExpenditureHead(String code, String name, String description, Long branchID) {
        ExpenditureHead expenditureHead =  expenditureHeadsRepository.findById(branchID).orElseThrow(EntityNotFoundException::new);
        expenditureHead.setCode(code);
        expenditureHead.setName(name);
        expenditureHead.setDescription(description);
        expenditureHeadsRepository.save(expenditureHead);

        return expenditureHead;
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
