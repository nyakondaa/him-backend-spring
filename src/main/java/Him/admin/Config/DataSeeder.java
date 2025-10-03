package Him.admin.Config;

import Him.admin.Models.*;
import Him.admin.Repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    CommandLineRunner seedDatabase(
            BranchRepository branchRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            RevenueHeadRepository revenueHeadRepository,
            ExpenditureHeadsRepository expenditureHeadsRepository,
            PaymentMethodRepository paymentMethodRepository,
            PasswordEncoder passwordEncoder,
            ExpenditureRepository expenditureRepository
    ) {
        return args -> {

            // ====================================================================
            // 💡 CRITICAL IMPROVEMENT: CHECK IF DATA ALREADY EXISTS (Idempotency)
            // ====================================================================
            if (branchRepository.count() > 0) {
                System.out.println("⚠️ Database already contains branches. Seeding process skipped to prevent Duplicate Key errors.");
                return; // Exit the seeder if data is present
            }

            System.out.println("🚀 Starting database seeding...");

            // ====================================================================
            // ===== 1️⃣ Create Branches and Entities Dependent on Branch =====
            // ====================================================================
            Branch headOffice = Branch.builder()
                    .branchName("Head Office")
                    .branchAddress("123 Main St, Central")
                    .branchPhone("0771234567")
                    .branchEmail("headoffice@example.com")
                    .branchCode("HO") // This is the unique field causing the error
                    .build();

            Branch harareBranch = Branch.builder()
                    .branchName("Harare Branch")
                    .branchAddress("456 High St, Harare CBD")
                    .branchPhone("0779876543")
                    .branchEmail("harare@example.com")
                    .branchCode("HB")
                    .build();

            branchRepository.saveAll(Set.of(headOffice, harareBranch));

            // ====================================================================
            // ===== 2️⃣ Create Permissions (Standard CRUD for Core Modules) =====
            // ====================================================================
            Permission readUsers = Permission.builder().module("users").action("read").build();
            Permission createUsers = Permission.builder().module("users").action("create").build();
            Permission updateUsers = Permission.builder().module("users").action("update").build();
            Permission deleteUsers = Permission.builder().module("users").action("delete").build();
            Permission readTransactions = Permission.builder().module("transactions").action("read").build();
            Permission createTransactions = Permission.builder().module("transactions").action("create").build();

            permissionRepository.saveAll(Set.of(readUsers, createUsers, updateUsers, deleteUsers, readTransactions, createTransactions));

            // ====================================================================
            // ===== 3️⃣ Create Roles (Group Permissions) =====
            // ====================================================================
            Set<Permission> adminPerms = Set.of(readUsers, createUsers, updateUsers, deleteUsers, readTransactions, createTransactions);

            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .description("Full system access and configuration rights.")
                    .permissions(adminPerms)
                    .build();

            Role supervisorRole = Role.builder()
                    .name("SUPERVISOR")
                    .description("Manages daily operations and approvals.")
                    .permissions(Set.of(readUsers, readTransactions, createTransactions))
                    .build();

            roleRepository.saveAll(Set.of(adminRole, supervisorRole));

            // ====================================================================
            // ===== 4️⃣ Create Users (Associating with Branch and Hashed Password) =====
            // ====================================================================
            User adminUser = User.builder()
                    .username("sysadmin")
                    .password(passwordEncoder.encode("SecureP@ss123"))
                    .email("sysadmin@him.com")
                    .branch(headOffice)
                    .roles(Set.of(adminRole))
                    .build();

            User supervisorUser = User.builder()
                    .username("harare_supervisor")
                    .password(passwordEncoder.encode("SupervisorP@ss"))
                    .email("supervisor.harare@him.com")
                    .branch(harareBranch)
                    .roles(Set.of(supervisorRole))
                    .build();

            userRepository.saveAll(Set.of(adminUser, supervisorUser));

            // ====================================================================
            // ===== 5️⃣ Revenue Heads (Income Sources) =====
            // ====================================================================
            RevenueHeads tithes = RevenueHeads.builder().name("Tithes").code("REV001").description("Regular tithes collection from members.").build();
            RevenueHeads offerings = RevenueHeads.builder().name("Offerings").code("REV002").description("General weekly offerings.").build();
            RevenueHeads specialDonation = RevenueHeads.builder().name("Special Donation").code("REV003").description("One-time, non-regular donations.").build();

            revenueHeadRepository.saveAll(Set.of(tithes, offerings, specialDonation));

            // ====================================================================
            // ===== 6️⃣ Expenditure Heads (Expense Categories) =====
            // ====================================================================
            ExpenditureHead salaries = ExpenditureHead.builder().name("Staff Salaries").code("EXP101").description("Monthly compensation for staff.").branch(headOffice).build();
            ExpenditureHead utilities = ExpenditureHead.builder().name("Branch Utilities").code("EXP102").description("Electricity, water, and internet bills.").branch(harareBranch).build();
            ExpenditureHead maintenance = ExpenditureHead.builder().name("Building Maintenance").code("EXP103").description("General repairs and upkeep.").branch(headOffice).build();

            expenditureHeadsRepository.saveAll(Set.of(salaries, utilities, maintenance));

            // ====================================================================
            // ===== 7️⃣ Payment Methods (How money moves) =====
            // ====================================================================
            PaymentMethod cash = PaymentMethod.builder().name("Cash").build();
            PaymentMethod ecocash = PaymentMethod.builder().name("Ecocash").build();
            PaymentMethod bankTransfer = PaymentMethod.builder().name("Bank Transfer").build();

            paymentMethodRepository.saveAll(Set.of(cash, ecocash, bankTransfer));


            // ====================================================================
            // ===== 8️⃣ Expenditures (Sample Transactions) =====
            // ====================================================================
            Expenditure e1 = Expenditure.builder()
                    .amount(new BigDecimal("300.50"))
                    .expenditureDate(LocalDateTime.now().minusDays(5))
                    .description("Head Office Electricity Bill Q4")
                    .branch(headOffice)
                    .expenditureHead(utilities)
                    .approvedBy(adminUser)
                    .build();

            Expenditure e2 = Expenditure.builder()
                    .amount(new BigDecimal("2000.00"))
                    .expenditureDate(LocalDateTime.now())
                    .description("October Staff Salaries Payout")
                    .branch(headOffice)
                    .expenditureHead(salaries)
                    .approvedBy(adminUser)
                    .build();

            expenditureRepository.saveAll(Set.of(e1, e2));

            System.out.println("✅ Database seeded: Branches, Permissions, Roles, Users (Hashed Passwords!), RevenueHeads, ExpenditureHeads, PaymentMethods, Expenditures.");
        };
    }
}