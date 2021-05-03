package kr.co.spotbuddy.modules.photoUrl;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.ProfilePhoto;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.member.MemberType;
import kr.co.spotbuddy.modules.photoUrl.dto.PhotoRequest;
import kr.co.spotbuddy.modules.photoUrl.dto.ProfileImageFile;
import kr.co.spotbuddy.modules.posts.Uploader;
import kr.co.spotbuddy.modules.response.PhotoUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfilePhotoService {
    private final ProfilePhotoRepository profilePhotoRepository;
    private final MemberType memberType;
    private final Uploader uploader;
    private final MemberRepository memberRepository;

    public PhotoUrlResponse getMyPhoto(Object object) {
        Member member = memberType.getMemberType(object);

        if(profilePhotoRepository.existsByMember(member))
        return PhotoUrlResponse.builder().photoUrl(profilePhotoRepository.findByMember(member).getFileUrl()).build();

        else return null;
    }

    public PhotoUrlResponse getOtherPhoto(PhotoRequest photoRequest) {
        Member member = memberRepository.findByNickname(photoRequest.getNickname());
        if(profilePhotoRepository.existsByMember(member)) {
            ProfilePhoto photo = profilePhotoRepository.findByMember(member);

            return PhotoUrlResponse.builder().photoUrl(photo.getFileUrl()).build();
        }
        else return null;
    }


    @Transactional
    public PhotoUrlResponse uploadProfileImage(Object object, ProfileImageFile profileImageFile) throws IOException {
        Member member = memberType.getMemberType(object);
        if(profilePhotoRepository.existsByMember(member)) {
            return modifyProfileImage(profileImageFile, member);
        }
        else {
            return uploadNewProfileImage(profileImageFile, member);
        }
    }

    private PhotoUrlResponse uploadNewProfileImage(ProfileImageFile profileImageFile, Member member) throws IOException {
        String photoUrl = uploader.upload(profileImageFile.getPhoto(), "static");
        ProfilePhoto profilePhoto = ProfilePhoto.builder()
                .member(member)
                .fileUrl(photoUrl)
                .build();

        profilePhotoRepository.save(profilePhoto);
        return PhotoUrlResponse.builder().photoUrl(photoUrl).build();
    }

    private PhotoUrlResponse modifyProfileImage(ProfileImageFile profileImageFile, Member member) throws IOException {
        String photoUrl = uploader.upload(profileImageFile.getPhoto(), "static");
        ProfilePhoto profilePhoto = profilePhotoRepository.findByMember(member);
        profilePhoto.modifyPhoto(photoUrl);
        profilePhotoRepository.save(profilePhoto);

        return PhotoUrlResponse.builder().photoUrl(photoUrl).build();
    }
}
