package kr.co.spotbuddy.infra.firebase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FcmMessage {
    private boolean validate_only;
    private appMessage message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class appMessage {
        private Notification notification;
        private FcmData data;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class FcmData {
        private String path;
    }
}
