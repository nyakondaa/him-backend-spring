package Him.admin.Config;

import Him.admin.Models.*;
import Him.admin.Repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            BranchRepository branchRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            RevenueHeadRepository revenueHeadRepository,
            ExpenditureHeadsRepository expenditureHeadsRepository,
            PaymentMethodRepository paymentMethodRepository,


            ExpenditureRepository expenditureRepository
    ) {
        return args -> {

            // ===== 1️⃣ Create Branches =====
            Branch branch1 = Branch.builder()
                    .branchName("Head Office")
                    .branchAddress("123 Main St")
                    .branchPhone("0771234567")
                    .branchEmail("headoffice@example.com")
                    .branchCode("HO")
                    .build();

            Branch branch2 = Branch.builder()
                    .branchName("Harare Branch")
                    .branchAddress("456 High St")
                    .branchPhone("0779876543")
                    .branchEmail("harare@example.com")
                    .branchCode("HB")
                    .build();

            branchRepository.save(branch1);
            branchRepository.save(branch2);

            // ===== 2️⃣ Create Permissions =====
            Permission readUsers = Permission.builder().module("users").action("read").build();
            Permission createUsers = Permission.builder().module("users").action("create").build();
            Permission readTransactions = Permission.builder().module("transactions").action("read").build();
            permissionRepository.save(readUsers);
            permissionRepository.save(createUsers);
            permissionRepository.save(readTransactions);

            // ===== 3️⃣ Create Roles =====
            Set<Permission> adminPerms = new HashSet<>();
            adminPerms.add(readUsers);
            adminPerms.add(createUsers);
            adminPerms.add(readTransactions);

            Role adminRole = Role.builder()
                    .name("admin")
                    .description("Full system access")
                    .permissions(adminPerms)
                    .build();

            Role supervisorRole = Role.builder()
                    .name("supervisor")
                    .description("Limited access")
                    .permissions(Set.of(readUsers, readTransactions))
                    .build();

            roleRepository.save(adminRole);
            roleRepository.save(supervisorRole);

            // ===== 4️⃣ Create Users =====
            User adminUser = User.builder()
                    .username("admin_user")
                    .password("password123")
                    .email("user@example.gmail.com")
                    .branch(branch1)
                    .roles(Set.of(adminRole))
                    .build();

            User supervisorUser = User.builder()
                    .username("supervisor_user")
                    .password("password123")
                    .email("supervisor@gmail.com")
                    .branch(branch2)
                    .roles(Set.of(supervisorRole))
                    .build();

            userRepository.save(adminUser);
            userRepository.save(supervisorUser);

            // ===== 5️⃣ Revenue Heads =====
            RevenueHeads tithes = RevenueHeads.builder().name("Tithes").code("HO001").description("Tithes collection").build();
            RevenueHeads offerings = RevenueHeads.builder().name("Offerings").code("HO002").description("General offerings").build();
            revenueHeadRepository.saveAll(Set.of(tithes, offerings));

            // ===== 6️⃣ Expenditure Heads =====
            ExpenditureHead salaries = ExpenditureHead.builder().name("Salaries").code("HO101").description("Staff salaries").branch(branch1).build();
            ExpenditureHead utilities = ExpenditureHead.builder().name("Utilities").code("HO102").description("Electricity, water, internet").branch(branch2).build();
            expenditureHeadsRepository.saveAll(Set.of(salaries, utilities));

            // ===== 7️⃣ Payment Methods =====
            PaymentMethod cash = PaymentMethod.builder().name("Cash").build();
            PaymentMethod ecocash = PaymentMethod.builder().name("Ecocash").build();
            paymentMethodRepository.saveAll(Set.of(cash, ecocash));






            // ===== 🔟 Expenditures =====
            Expenditure e1 = Expenditure.builder()
                    .amount(new BigDecimal("300"))
                    .expenditureDate(LocalDateTime.now())
                    .description("Electricity bill")
                    .branch(branch1)
                    .expenditureHead(utilities)
                    .approvedBy(adminUser)
                    .build();

            Expenditure e2 = Expenditure.builder()
                    .amount(new BigDecimal("2000"))
                    .expenditureDate(LocalDateTime.now())
                    .description("Staff salaries for October")
                    .branch(branch1)
                    .expenditureHead(salaries)
                    .approvedBy(adminUser)
                    .build();

            expenditureRepository.saveAll(Set.of(e1, e2));

            System.out.println("✅ Database seeded: Branches, Users, Roles, Permissions, RevenueHeads, ExpenditureHeads, PaymentMethods, Budgets, Transactions, Expenditures");
        };
    }
}
