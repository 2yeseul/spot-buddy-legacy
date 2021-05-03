package kr.co.spotbuddy.infra.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

public class CustomPersistentTokenBasedRememberMeServices extends PersistentTokenBasedRememberMeServices {
    private PersistentTokenRepository tokenRepository = new InMemoryTokenRepositoryImpl();

    public CustomPersistentTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie cookie = new Cookie("rememberMe", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        this.tokenRepository.removeUserTokens(authentication.getName());
    }


    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 + " tokens, but contained '"
                    + Arrays.asList(cookieTokens) + "'");
        }
        String presentedSeries = cookieTokens[0];
        String presentedToken = cookieTokens[1];

        if(this.tokenRepository.getTokenForSeries(presentedSeries)==null) {
            Cookie cookie = new Cookie("rememberMe", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            throw new RememberMeAuthenticationException("logout status");
        }

        PersistentRememberMeToken token = this.tokenRepository.getTokenForSeries(presentedSeries);

        String nickname = token.getUsername();

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        if (token.getDate().getTime() + getTokenValiditySeconds() * 1000L < System.currentTimeMillis()) {
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }

        logger.info(">>>> persistent login username : " + nickname);

        PersistentRememberMeToken newToken = new PersistentRememberMeToken(token.getUsername(), token.getSeries(),
                generateTokenData(), new Date());
        try {
            this.tokenRepository.updateToken(newToken.getSeries(), newToken.getTokenValue(), newToken.getDate());
            addCookie(newToken, request, response);
        }
        catch (Exception ex) {
            this.logger.error("Failed to update token: ", ex);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
        }

        return getUserDetailsService().loadUserByUsername(nickname);
    }

    private void addCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[] { token.getSeries(), token.getTokenValue() }, getTokenValiditySeconds(), request,
                response);
    }
}
