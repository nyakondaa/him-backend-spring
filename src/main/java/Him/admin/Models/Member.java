package Him.admin.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "members")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Personal Details ---
    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    private LocalDate birthDate;

    @Column(length = 10)
    private String gender; // Male/Female/Other

    // --- Contact Details ---
    private String address;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 100, unique = true)
    private String email;

    // --- Membership Info ---
    private LocalDate joinedAt;



    // 1. Member belongs to a Branch
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // 2. Member has a MembershipType (Regular, Executive, Pastor, etc.)

    // 3. One Member can have many Transactions
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();



}
