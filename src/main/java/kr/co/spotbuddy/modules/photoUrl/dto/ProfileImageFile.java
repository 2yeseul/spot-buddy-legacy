package kr.co.spotbuddy.modules.photoUrl.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileImageFile {
    private MultipartFile photo;
}
