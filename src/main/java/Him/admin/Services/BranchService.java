package Him.admin.Services;

import Him.admin.Models.Branch;
import Him.admin.Repositories.BranchRepository;
import Him.admin.Exceptions.ResourceAlreadyExistsException;
import Him.admin.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    // Create branch
    public Branch createBranch(Branch branch) {
        if (branchRepository.existsByBranchName(branch.getBranchName())) {
            throw new ResourceAlreadyExistsException("Branch", "branchName", branch.getBranchName());
        }
        if (branchRepository.existsByBranchCode(branch.getBranchCode())) {
            throw new ResourceAlreadyExistsException("Branch", "branchCode", branch.getBranchCode());
        }
        return branchRepository.save(branch);
    }

    // Get all branches
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    // Get branch by ID
    public Branch getBranchById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));
    }

    // Update branch
    public Branch updateBranch(Long id, Branch updatedBranch) {
        Branch branch = getBranchById(id);
        branch.setBranchName(updatedBranch.getBranchName());
        branch.setBranchAddress(updatedBranch.getBranchAddress());
        branch.setBranchPhone(updatedBranch.getBranchPhone());
        branch.setBranchEmail(updatedBranch.getBranchEmail());
        branch.setBranchCode(updatedBranch.getBranchCode());
        return branchRepository.save(branch);
    }

    // Delete branch
    public void deleteBranch(Long id) {
        Branch branch = getBranchById(id);
        branchRepository.delete(branch);
    }
}
