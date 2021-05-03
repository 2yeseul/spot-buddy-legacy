package kr.co.spotbuddy.modules.posts.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostSave {
    private String title;
    private String content;
    private String isAnonymous;
    private List<MultipartFile> multipartFile;
    private int category;
    private int teamIndex;
}
