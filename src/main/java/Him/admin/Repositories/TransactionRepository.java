package Him.admin.Repositories;

import Him.admin.DTO.Contributions.MemberProjectContributionDTO;
import Him.admin.Models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByRrn(String rrn);
    boolean existsByRrn(String rrn);

    // Revenue transaction queries
    List<Transaction> findByRevenueHeadId(Long revenueHeadId);
    List<Transaction> findByMemberId(Long memberId);
    List<Transaction> findByBranchId(Long branchId);
    List<Transaction> findByPaymentMethodId(Long paymentMethodId);
    List<Transaction> findByProcessedById(Long userId);
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Pagination support for revenue transactions
    Page<Transaction> findByRevenueHeadId(Long revenueHeadId, Pageable pageable);
    Page<Transaction> findByMemberId(Long memberId, Pageable pageable);
    Page<Transaction> findByBranchId(Long branchId, Pageable pageable);
    Page<Transaction> findByPaymentMethodId(Long paymentMethodId, Pageable pageable);

    // Analytics queries for revenue transactions
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.revenueHead.id = :revenueHeadId")
    BigDecimal sumAmountByRevenueHeadId(@Param("revenueHeadId") Long revenueHeadId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.branch.id = :branchId")
    BigDecimal sumAmountByBranchId(@Param("branchId") Long branchId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.member.id = :memberId")
    BigDecimal sumAmountByMemberId(@Param("memberId") Long memberId);

    Long countByRevenueHeadId(Long revenueHeadId);

    // Recent transactions (both revenue and project)
    @Query("SELECT t FROM Transaction t ORDER BY t.transactionDate DESC LIMIT :limit")
    List<Transaction> findTopNByOrderByTransactionDateDesc(@Param("limit") int limit);

    // Find transactions by type (revenue vs project)
    @Query("SELECT t FROM Transaction t WHERE t.project IS NULL ORDER BY t.transactionDate DESC")
    List<Transaction> findAllRevenueTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.project IS NOT NULL ORDER BY t.transactionDate DESC")
    List<Transaction> findAllProjectTransactions();

    // Project contribution queries (your existing methods)
    @Query("""
        SELECT new Him.admin.DTO.Contributions.MemberProjectContributionDTO(
            t.id, 
            p.id, 
            p.title, 
            m.id, 
            m.firstName, 
            m.lastName, 
            m.email,
            m.phone,
            t.amount, 
            t.transactionDate, 
            b.branchName, 
            COALESCE(pm.name, 'Unknown'),
            COALESCE(CONCAT(u.firstName, ' ', u.lastName), 'System')
        )
        FROM Transaction t
        JOIN t.project p
        JOIN t.member m
        JOIN t.branch b
        LEFT JOIN t.paymentMethod pm
        LEFT JOIN t.processedBy u
        WHERE p.id = :projectId
        ORDER BY t.transactionDate DESC
    """)
    List<MemberProjectContributionDTO> findContributionsByProjectId(@Param("projectId") Long projectId);

    @Query("""
        SELECT new Him.admin.DTO.Contributions.MemberProjectContributionDTO(
            t.id, 
            p.id, 
            p.title, 
            m.id, 
            m.firstName, 
            m.lastName, 
            m.email,
            m.phone,
            t.amount, 
            t.transactionDate, 
            b.branchName, 
            COALESCE(pm.name, 'Unknown'),
            COALESCE(CONCAT(u.firstName, ' ', u.lastName), 'System')
        )
        FROM Transaction t
        JOIN t.project p
        JOIN t.member m
        JOIN t.branch b
        LEFT JOIN t.paymentMethod pm
        LEFT JOIN t.processedBy u
        WHERE m.id = :memberId
        ORDER BY t.transactionDate DESC
    """)
    List<MemberProjectContributionDTO> findContributionsByMemberId(@Param("memberId") Long memberId);

    @Query("""
        SELECT new Him.admin.DTO.Contributions.MemberProjectContributionDTO(
            MAX(t.id), 
            p.id, 
            p.title, 
            m.id, 
            m.firstName, 
            m.lastName, 
            m.email,
            m.phone,
            SUM(t.amount), 
            MAX(t.transactionDate), 
            b.branchName, 
            'Various',
            'System'
        )
        FROM Transaction t
        JOIN t.project p
        JOIN t.member m
        JOIN t.branch b
        WHERE p.id = :projectId
        GROUP BY m.id, m.firstName, m.lastName, m.email, m.phone, p.id, p.title, b.branchName
        ORDER BY SUM(t.amount) DESC
    """)
    List<MemberProjectContributionDTO> findTopContributorsByProjectId(@Param("projectId") Long projectId);

    @Query("""
        SELECT new Him.admin.DTO.Contributions.MemberProjectContributionDTO(
            t.id, 
            p.id, 
            p.title, 
            m.id, 
            m.firstName, 
            m.lastName, 
            m.email,
            m.phone,
            t.amount, 
            t.transactionDate, 
            b.branchName, 
            COALESCE(pm.name, 'Unknown'),
            COALESCE(CONCAT(u.firstName, ' ', u.lastName), 'System')
        )
        FROM Transaction t
        JOIN t.project p
        JOIN t.member m
        JOIN t.branch b
        LEFT JOIN t.paymentMethod pm
        LEFT JOIN t.processedBy u
        WHERE t.transactionDate BETWEEN :startDate AND :endDate
        ORDER BY t.transactionDate DESC
    """)
    List<MemberProjectContributionDTO> findContributionsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.project.id = :projectId")
    BigDecimal getTotalContributionsByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.member.id = :memberId AND t.project.id = :projectId")
    BigDecimal getMemberContributionToProject(@Param("memberId") Long memberId, @Param("projectId") Long projectId);

    @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.member.id = :memberId AND t.project.id = :projectId")
    boolean hasMemberContributedToProject(@Param("memberId") Long memberId, @Param("projectId") Long projectId);

    @Query("""
        SELECT new Him.admin.DTO.Contributions.MemberProjectContributionDTO(
            t.id, 
            p.id, 
            p.title, 
            m.id, 
            m.firstName, 
            m.lastName, 
            m.email,
            m.phone,
            t.amount, 
            t.transactionDate, 
            b.branchName, 
            COALESCE(pm.name, 'Unknown'),
            COALESCE(CONCAT(u.firstName, ' ', u.lastName), 'System')
        )
        FROM Transaction t
        JOIN t.project p
        JOIN t.member m
        JOIN t.branch b
        LEFT JOIN t.paymentMethod pm
        LEFT JOIN t.processedBy u
        WHERE b.id = :branchId
        ORDER BY t.transactionDate DESC
    """)
    List<MemberProjectContributionDTO> findContributionsByBranchId(@Param("branchId") Long branchId);
}