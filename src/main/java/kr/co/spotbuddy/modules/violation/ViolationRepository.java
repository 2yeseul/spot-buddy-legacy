package kr.co.spotbuddy.modules.violation;

import kr.co.spotbuddy.infra.domain.Violation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViolationRepository extends JpaRepository<Violation, Long> {
}
