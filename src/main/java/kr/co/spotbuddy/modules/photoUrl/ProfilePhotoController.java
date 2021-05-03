package kr.co.spotbuddy.modules.photoUrl;

import kr.co.spotbuddy.modules.member.CurrentUser;
import kr.co.spotbuddy.modules.photoUrl.dto.PhotoRequest;
import kr.co.spotbuddy.modules.photoUrl.dto.ProfileImageFile;
import kr.co.spotbuddy.modules.response.PhotoUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfilePhotoController {
    private final ProfilePhotoService profilePhotoService;

    @PostMapping("/upload/photo")
    public PhotoUrlResponse uploadProfilePhoto(@CurrentUser Object object, @ModelAttribute ProfileImageFile profileImageFile) throws IOException {
        return profilePhotoService.uploadProfileImage(object, profileImageFile);
    }

    @GetMapping("/get/my-photo")
    public PhotoUrlResponse getMyProfilePhoto(@CurrentUser Object object) {
        return profilePhotoService.getMyPhoto(object);
    }

    @PostMapping("/get/photo")
    public PhotoUrlResponse getOtherProfilePhoto(@RequestBody PhotoRequest photoRequest) {
        return profilePhotoService.getOtherPhoto(photoRequest);
    }
}
