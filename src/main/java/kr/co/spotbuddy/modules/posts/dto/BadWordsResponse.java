package kr.co.spotbuddy.modules.posts.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BadWordsResponse {
    private boolean badWordsIn;
}
