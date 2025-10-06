package Him.admin.Repositories;

import Him.admin.DTO.Contributions.MemberProjectContributionDTO;
import Him.admin.Models.Transaction;
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

    // Find all contributions for a specific project - MATCHING YOUR DTO
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

    // Find all projects a member has contributed to - MATCHING YOUR DTO
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

    // Find top contributors for a project - MATCHING YOUR DTO
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

    // Find contributions within date range - MATCHING YOUR DTO
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

    // Additional useful queries
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