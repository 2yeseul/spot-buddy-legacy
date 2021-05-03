package kr.co.spotbuddy.modules.passwordFind;

import kr.co.spotbuddy.infra.domain.PasswordFind;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordFindRepository extends JpaRepository<PasswordFind, Long> {
    PasswordFind findByEmail(String email);

    PasswordFind findTopByEmailOrderByIdDesc(String email);
}
