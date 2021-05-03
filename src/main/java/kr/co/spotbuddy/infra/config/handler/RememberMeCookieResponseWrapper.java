package kr.co.spotbuddy.infra.config.handler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class RememberMeCookieResponseWrapper extends HttpServletResponseWrapper {
    public RememberMeCookieResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void addCookie(Cookie cookie) {
        if(cookie.getName().equals("rememberMe")) {
            cookie.setPath("/");
        }
        super.addCookie(cookie);
    }
}
