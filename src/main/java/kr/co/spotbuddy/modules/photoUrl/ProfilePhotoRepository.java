package kr.co.spotbuddy.modules.photoUrl;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {
    boolean existsByMember(Member member);
    ProfilePhoto findByMember(Member member);
}
