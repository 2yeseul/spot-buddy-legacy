package kr.co.spotbuddy.infra.config;

import kr.co.spotbuddy.infra.config.auth.CustomOAuth2UserService;
import kr.co.spotbuddy.infra.config.handler.AuthFailureHandler;
import kr.co.spotbuddy.infra.config.handler.AuthSuccessHandler;
import kr.co.spotbuddy.infra.config.handler.HttpLogoutSuccessHandler;
import kr.co.spotbuddy.infra.config.handler.RememberMeCookieFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import kr.co.spotbuddy.modules.member.MemberService;
import kr.co.spotbuddy.infra.security.CustomAuthenticationFilter;
import kr.co.spotbuddy.infra.security.RestAuthenticationEntryPoint;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // OAUTH2
    private final CustomOAuth2UserService customOAuth2UserService;

    private final MemberService memberService;

    // REMEMBER ME
    private final DataSource dataSource;

    // LOGIN
    private final AuthFailureHandler authFailureHandler;
    private final AuthSuccessHandler authSuccessHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    // LOGOUT
    private final HttpLogoutSuccessHandler logoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.authorizeRequests()
                .mvcMatchers("/","/tour", "/login", "/check-email-token", "/test", "/tour-search",
                        "/tour-popular", "/docs", "/your-profile", "/send-email", "/tests", "/login-test",
                        "/test-new-date", "/docs-new", "/token/save", "/community/search", "/profile/get/photo",
                        "/check-email/end", "/resend-email", "/email-login", "/check-email-login", "/device",
                        "/community/content-check","/home/tour-search", "/login-link", "/sign-up", "/tour-search/home",
                        "/sign-up-oauth", "/tour-review/detail", "/tour-search/theme").permitAll()
                .antMatchers("/valid-nickname/**", "/valid-email/**").permitAll()
                .antMatchers("/tour-list/**").permitAll()
                .antMatchers("/version/**").permitAll()
                .antMatchers("/setting/password/**").permitAll()
                .antMatchers("/community/detail/**").permitAll()
                .antMatchers("/comment/list/**").permitAll()
                .antMatchers("/comment/list/**").permitAll()
                .antMatchers("/community/team/**").permitAll()
                .antMatchers("/community/popular/**").permitAll()
                .antMatchers("/tour-detail/**").permitAll()
                .anyRequest().authenticated();
        http.oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);
        http.exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint); // 인증 실패시 401

        http.logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true);

        http.formLogin().disable();

        http.rememberMe()
                .key("remember-me")
                .rememberMeServices(rememberMeServices());

        http.addFilterBefore(new RememberMeCookieFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(rememberMeAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable();
        http.cors();
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        try {
            filter.setFilterProcessesUrl("/login");
            filter.setAuthenticationManager(authenticationManager());
            filter.setUsernameParameter("email");
            filter.setPasswordParameter("password");
            filter.setRememberMeServices(rememberMeServices());
            filter.setAuthenticationSuccessHandler(authSuccessHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filter;
    }


    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception {
        return new RememberMeAuthenticationFilter(authenticationManager(), rememberMeServices());
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomPersistentTokenBasedRememberMeServices tokenBasedRememberMeServices =
                new CustomPersistentTokenBasedRememberMeServices("remember-me", memberService, tokenRepository());

        // 1분 , 1시간, 24시간, 7일
        final int WEEK = 60 * 60 * 24 * 7;
        tokenBasedRememberMeServices.setTokenValiditySeconds(WEEK * 4);
        tokenBasedRememberMeServices.setCookieName("rememberMe");
        tokenBasedRememberMeServices.setParameter("rememberMe");

        return tokenBasedRememberMeServices;
    }

}
