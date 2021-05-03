package kr.co.spotbuddy.modules.deleteAccount;

import kr.co.spotbuddy.infra.domain.DeleteAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteAccountRepository extends JpaRepository<DeleteAccount, Long> {
    boolean existsByEmail(String email);
    DeleteAccount findByEmail(String email);
}
