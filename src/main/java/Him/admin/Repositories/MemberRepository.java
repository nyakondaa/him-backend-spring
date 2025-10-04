package Him.admin.Repositories;

import Him.admin.Models.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhone(String phone);
}
