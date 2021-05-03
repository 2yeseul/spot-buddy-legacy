package kr.co.spotbuddy.modules.appVersion;

import kr.co.spotbuddy.infra.domain.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    boolean existsByToken(String token);

    AppVersion findByToken(String token);

}
