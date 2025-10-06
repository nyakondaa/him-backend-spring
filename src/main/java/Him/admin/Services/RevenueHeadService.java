package Him.admin.Services;

import Him.admin.Exceptions.ResourceAlreadyExistsException;
import Him.admin.Exceptions.ResourceNotFoundException;
import Him.admin.Models.Branch;
import Him.admin.Models.RevenueHeads;
import Him.admin.Repositories.BranchRepository;
import Him.admin.Repositories.RevenueHeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueHeadService {

    private final RevenueHeadRepository revenueHeadRepository;
    private final BranchRepository branchRepository;

    public RevenueHeads createRevenueHeads(String name, String description, String code, Long branchID) {
        revenueHeadRepository.findByName(name)
                .ifPresent(revenueHead -> {
                    throw new ResourceAlreadyExistsException("RevenueHead", "name", name);
                });

        Branch branch = branchRepository.findById(branchID)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", branchID));

        RevenueHeads revenueHead = RevenueHeads
                .builder()
                .name(name)
                .description(description)
                .code(code)
                .branch(branch)
                .build();

        revenueHeadRepository.save(revenueHead);
        return revenueHead;
    }

    public RevenueHeads updateRevenueHeads(String name, String description, String code, Long id) {
        RevenueHeads revenueHead = revenueHeadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RevenueHead", "id", id));
        revenueHead.setName(name);
        revenueHead.setDescription(description);
        revenueHead.setCode(code);

        revenueHeadRepository.save(revenueHead);
        return revenueHead;
    }

    // Get all revenue heads with branch information
    public List<RevenueHeads> getAllRevenueHeads() {
        return revenueHeadRepository.findAll();
    }

    // Get revenue heads by branch ID
    public List<RevenueHeads> getRevenueHeadsByBranchId(Long branchId) {
        return revenueHeadRepository.findByBranchId(branchId);
    }

    // Delete a revenue-head
    public void deleteRevenueHead(Long id) {
        RevenueHeads revenueHead = revenueHeadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RevenueHead", "id", id));
        revenueHeadRepository.delete(revenueHead);
    }
}