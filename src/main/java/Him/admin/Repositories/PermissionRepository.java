package Him.admin.Repositories;


import Him.admin.Models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByModuleAndAction(String module, String action);
}
