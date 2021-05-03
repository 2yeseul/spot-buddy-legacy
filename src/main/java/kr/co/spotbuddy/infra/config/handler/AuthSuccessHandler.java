package kr.co.spotbuddy.infra.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.spotbuddy.infra.config.handler.dto.TokenValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);

        Cookie jSession = new Cookie("JSESSIONID", request.getSession().getId());
        ObjectMapper objectMapper = new ObjectMapper();

        TokenValue tokenValue = TokenValue.builder().JSESSIONID(jSession.getValue()).build();

        response.getWriter().write(objectMapper.writeValueAsString(tokenValue));

        Cookie rememberMe = new Cookie("rememberMe", request.getSession().getId());


    }


}
