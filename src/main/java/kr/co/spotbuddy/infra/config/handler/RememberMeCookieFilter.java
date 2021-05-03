package kr.co.spotbuddy.infra.config.handler;


import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RememberMeCookieFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(response instanceof HttpServletResponse) {
            HttpServletResponse newResponse = new RememberMeCookieResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, newResponse);
        }
    }
}
