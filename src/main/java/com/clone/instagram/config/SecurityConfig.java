package com.clone.instagram.config;

import com.clone.instagram.config.auth.PrincipalDetailsService;
import com.clone.instagram.config.oauth.Oauth2DetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // 스프링 시큐리티 사용을 위한 어노테이션
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalDetailsService principalDetailsService;
    private final Oauth2DetailsService oauth2DetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/style/**")
                .antMatchers("/js/**")
                .antMatchers("/img/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
//                .antMatchers("/login", "/signup", "/style/**", "/js/**", "/img/**").permitAll()
                .antMatchers("/login", "/signup").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login") // 로그인 페이지로 사용할 페이지 지정
                .loginProcessingUrl("/loginForm") // 로그인을 요청할 url 지정 (로그인 form의 action과 이름이 같아야 함)
                .defaultSuccessUrl("/story") // 로그인 성공 후 반환할 페이지 지정
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true) // 세션 전체 삭제
                .and()
                .oauth2Login() // form 로그인도 하고, oauth2로그인도 함
                .loginPage("/login") // 로그인 페이지로 사용할 페이지 지정
                .userInfoEndpoint() // oauth2로그인시 최종 유저의 정보를 바로 받아옴
                .userService(oauth2DetailsService);


        http.sessionManagement() // 중복 로그인
                .maximumSessions(1) // 세션 최대 허용 수
                .maxSessionsPreventsLogin(false); // false 이면 중복 로그인시 이전 로그인이 풀림
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 비밀번호를 암호화 하기 위해서 userDetailsService에 passwordEncoder를 등록
        auth.userDetailsService(principalDetailsService).passwordEncoder(passwordEncoder());
    }

}
