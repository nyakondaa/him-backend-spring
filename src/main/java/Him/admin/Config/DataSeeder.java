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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

            System.out.println("🚀 Starting database seeding...");

            // ====================================================================
            // ===== 1️⃣ Handle Branches (Idempotency Check) =====
            // ====================================================================
            Branch headOffice;
            Branch harareBranch;
            List<Branch> existingBranches = branchRepository.findAll();

            if (existingBranches.isEmpty()) {
                System.out.println("✅ Seeding initial branches...");
                headOffice = Branch.builder()
                        .branchName("Head Office")
                        .branchAddress("123 Main St, Central")
                        .branchPhone("0771234567")
                        .branchEmail("headoffice@example.com")
                        .branchCode("HO")
                        .build();

                harareBranch = Branch.builder()
                        .branchName("Harare Branch")
                        .branchAddress("456 High St, Harare CBD")
                        .branchPhone("0779876543")
                        .branchEmail("harare@example.com")
                        .branchCode("HB")
                        .build();

                branchRepository.saveAll(Set.of(headOffice, harareBranch));
            } else {
                System.out.println("⚠️ Branches already exist. Fetching them for relationship linking...");
                headOffice = existingBranches.stream()
                        .filter(b -> b.getBranchCode().equals("HO"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Head Office (HO) not found!"));
                harareBranch = existingBranches.stream()
                        .filter(b -> b.getBranchCode().equals("HB"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Harare Branch (HB) not found!"));
            }

            // ====================================================================
            // ===== 2️⃣ Create and Persist Permissions (Required for Roles) =====
            // ====================================================================
            if (permissionRepository.count() == 0) {
                System.out.println("✅ Seeding Permissions...");
                Map<String, Permission> transientPermissions = Map.of(
                        "users:read", Permission.builder().module("users").action("read").build(),
                        "users:create", Permission.builder().module("users").action("create").build(),
                        "users:update", Permission.builder().module("users").action("update").build(),
                        "users:delete", Permission.builder().module("users").action("delete").build(),
                        "transactions:read", Permission.builder().module("transactions").action("read").build(),
                        "transactions:update", Permission.builder().module("transactions").action("update").build(),
                        "transactions:create", Permission.builder().module("transactions").action("create").build(),
                        "transactions:delete", Permission.builder().module("transactions").action("delete").build()
                );

                // CRITICAL FIX: Save all permissions and collect the returned, PERSISTED entities.
                Set<Permission> persistedPermissions = Set.copyOf(
                        permissionRepository.saveAll(transientPermissions.values())
                );

                // Convert the Set back to a Map for easy lookup by the business key (module:action)
                Map<String, Permission> pMap = persistedPermissions.stream()
                        .collect(Collectors.toMap(p -> p.getModule() + ":" + p.getAction(), p -> p));


                // ====================================================================
                // ===== 3️⃣ Create Roles (Using PERSISTED Permissions) =====
                // ====================================================================
                if (roleRepository.count() == 0) {
                    System.out.println("✅ Seeding Roles and Populating roles_permission table...");

                    Set<Permission> adminPerms = Set.of(
                            pMap.get("users:read"), pMap.get("users:create"), pMap.get("users:update"), pMap.get("users:delete"),
                            pMap.get("transactions:read"), pMap.get("transactions:create")
                    );

                    Role adminRole = Role.builder()
                            .name("ADMIN")
                            .description("Full system access and configuration rights.")
                            .permissions(adminPerms)
                            .build();

                    Set<Permission> supervisorPerms = Set.of(
                            pMap.get("users:read"),
                            pMap.get("transactions:read"), pMap.get("transactions:create")
                    );

                    Role supervisorRole = Role.builder()
                            .name("SUPERVISOR")
                            .description("Manages daily operations and approvals.")
                            .permissions(supervisorPerms)
                            .build();

                    // CRITICAL FIX: This save operation now correctly populates roles_permission
                    roleRepository.saveAll(Set.of(adminRole, supervisorRole));
                }
            }

            // ====================================================================
            // ===== 4️⃣ Remaining Seeding Logic (Reduced for brevity, but should be checked for idempotency) =====
            // ====================================================================

            // Fetch the newly created/existing roles to link to users
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found after seeding."));
            Role supervisorRole = roleRepository.findByName("SUPERVISOR")
                    .orElseThrow(() -> new IllegalStateException("SUPERVISOR role not found after seeding."));


            if (userRepository.count() == 0) {
                System.out.println("✅ Seeding Users...");
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
            }


            // ... Rest of your seeding logic (RevenueHeads, ExpenditureHeads, Payments, Expenditures)
            // ... should also have their own idempotency checks (e.g., if (revenueHeadRepository.count() == 0))
            // ... to ensure they run only if needed.

            System.out.println("✅ Database seeding complete. Check roles_permission table.");
        };
    }
}