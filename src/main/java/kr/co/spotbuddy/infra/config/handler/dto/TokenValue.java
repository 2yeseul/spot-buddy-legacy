package kr.co.spotbuddy.infra.config.handler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValue {

    private String JSESSIONID;

}
