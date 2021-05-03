package kr.co.spotbuddy.modules.appVersion.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AppVersionResponse {
    private String version;
}
