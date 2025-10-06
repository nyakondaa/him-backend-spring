package Him.admin.Services;

import Him.admin.DTO.Contributions.ContributionRequestDTO;
import Him.admin.DTO.Contributions.MemberProjectContributionDTO;
import Him.admin.Models.*;
import Him.admin.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberProjectContributionService {

    private final TransactionRepository transactionRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final BranchRepository branchRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final RevenueHeadRepository revenueHeadsRepository;
    private final TransactionService transactionService;

    // ========== QUERY METHODS ==========

    @Transactional(readOnly = true)
    public List<MemberProjectContributionDTO> getContributionsByProject(Long projectId) {
        return transactionRepository.findContributionsByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<MemberProjectContributionDTO> getContributionsByMember(Long memberId) {
        return transactionRepository.findContributionsByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalContributionsForProject(Long projectId) {
        return transactionRepository.getTotalContributionsByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getMemberContributionToProject(Long memberId, Long projectId) {
        return transactionRepository.getMemberContributionToProject(memberId, projectId);
    }

    @Transactional(readOnly = true)
    public List<MemberProjectContributionDTO> getTopContributorsForProject(Long projectId) {
        return transactionRepository.findTopContributorsByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public boolean hasMemberContributedToProject(Long memberId, Long projectId) {
        return transactionRepository.hasMemberContributedToProject(memberId, projectId);
    }

    @Transactional(readOnly = true)
    public List<MemberProjectContributionDTO> getContributionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findContributionsByDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<MemberProjectContributionDTO> getRecentContributions() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return transactionRepository.findContributionsByDateRange(thirtyDaysAgo, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public ProjectContributionStats getProjectContributionStats(Long projectId) {
        List<MemberProjectContributionDTO> contributions = getContributionsByProject(projectId);
        List<MemberProjectContributionDTO> topContributors = getTopContributorsForProject(projectId);
        BigDecimal totalContributions = getTotalContributionsForProject(projectId);

        // Count unique contributors
        long uniqueContributors = contributions.stream()
                .map(MemberProjectContributionDTO::memberId)
                .distinct()
                .count();

        return new ProjectContributionStats(
                contributions,
                topContributors,
                totalContributions,
                contributions.size(),
                (int) uniqueContributors
        );
    }

    @Transactional(readOnly = true)
    public List<MemberProjectContributionDTO> getContributionsByBranch(Long branchId) {
        return transactionRepository.findContributionsByBranchId(branchId);
    }

    @Transactional(readOnly = true)
    public ContributionStatistics getContributionStatistics(Long projectId) {
        List<MemberProjectContributionDTO> contributions = getContributionsByProject(projectId);
        List<MemberProjectContributionDTO> topContributors = getTopContributorsForProject(projectId);
        BigDecimal totalAmount = getTotalContributionsForProject(projectId);

        long uniqueContributors = contributions.stream()
                .map(MemberProjectContributionDTO::memberId)
                .distinct()
                .count();

        return new ContributionStatistics(
                totalAmount,
                contributions.size(),
                (int) uniqueContributors,
                contributions,
                topContributors
        );
    }

    // ========== CRUD OPERATIONS ==========

    @Transactional
    public MemberProjectContributionDTO createContribution(ContributionRequestDTO request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + request.projectId()));

        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + request.memberId()));

        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + request.branchId()));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.paymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found with id: " + request.paymentMethodId()));



        // Create transaction
        String rrn = transactionService.generateUniqueRrn();
        Transaction transaction = Transaction.builder()
                .rrn(rrn)
                .amount(request.amount())
                .transactionDate(LocalDateTime.now())
                .currency("USD")
                .member(member)
                .project(project)
                .branch(branch)
                .paymentMethod(paymentMethod)
                .build();

        // Set processed by user if provided
        User processedByUser = null;
        if (request.processedByUserId() != null) {
            processedByUser = userRepository.findById(request.processedByUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.processedByUserId()));
            transaction.setProcessedBy(processedByUser);
        }

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Update project funding (remove duplicate)
        project.addTransaction(savedTransaction);
        projectRepository.save(project);

        // Convert to response DTO
        return new MemberProjectContributionDTO(
                savedTransaction.getId(),
                project.getId(),
                project.getTitle(),
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhone(), // Make sure your Member entity has this field
                savedTransaction.getAmount(),
                savedTransaction.getTransactionDate(),
                branch.getBranchName(),
                paymentMethod.getName(),
                processedByUser != null ?
                        processedByUser.getFirstName() + " " + processedByUser.getLastName() : "System"
        );
    }

    @Transactional
    public Transaction updateContribution(Long contributionId, ContributionRequestDTO request) {
        Transaction existingTransaction = transactionRepository.findById(contributionId)
                .orElseThrow(() -> new RuntimeException("Contribution not found with id: " + contributionId));

        // Validate RRN uniqueness (if changed)

        existingTransaction.setAmount(request.amount());

        // Update relationships if changed
        if (!existingTransaction.getProject().getId().equals(request.projectId())) {
            Project newProject = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new RuntimeException("Project not found with id: " + request.projectId()));
            existingTransaction.setProject(newProject);
        }

        if (!existingTransaction.getMember().getId().equals(request.memberId())) {
            Member newMember = memberRepository.findById(request.memberId())
                    .orElseThrow(() -> new RuntimeException("Member not found with id: " + request.memberId()));
            existingTransaction.setMember(newMember);
        }

        if (!existingTransaction.getBranch().getId().equals(request.branchId())) {
            Branch newBranch = branchRepository.findById(request.branchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found with id: " + request.branchId()));
            existingTransaction.setBranch(newBranch);
        }

        if (!existingTransaction.getPaymentMethod().getId().equals(request.paymentMethodId())) {
            PaymentMethod newPaymentMethod = paymentMethodRepository.findById(request.paymentMethodId())
                    .orElseThrow(() -> new RuntimeException("Payment method not found with id: " + request.paymentMethodId()));
            existingTransaction.setPaymentMethod(newPaymentMethod);
        }

        return transactionRepository.save(existingTransaction);
    }

    @Transactional
    public void deleteContribution(Long contributionId) {
        Transaction transaction = transactionRepository.findById(contributionId)
                .orElseThrow(() -> new RuntimeException("Contribution not found with id: " + contributionId));

        // Remove from project funding before deletion
        Project project = transaction.getProject();
        project.setCurrentFunding(project.getCurrentFunding().subtract(transaction.getAmount()));
        projectRepository.save(project);

        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public Transaction getContributionById(Long contributionId) {
        return transactionRepository.findById(contributionId)
                .orElseThrow(() -> new RuntimeException("Contribution not found with id: " + contributionId));
    }



    // ========== RECORDS ==========

    public record ProjectContributionStats(
            List<MemberProjectContributionDTO> allContributions,
            List<MemberProjectContributionDTO> topContributors,
            BigDecimal totalAmount,
            int totalContributions,
            int uniqueContributors
    ) {}

    public record ContributionStatistics(
            BigDecimal totalAmount,
            int totalContributions,
            int uniqueContributors,
            List<MemberProjectContributionDTO> allContributions,
            List<MemberProjectContributionDTO> topContributors
    ) {}
}