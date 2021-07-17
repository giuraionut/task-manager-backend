package com.example.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/","index","/css/*","/js/*").permitAll()
//                .antMatchers("/api/**").hasRole(UserRole.MEMBER.name())
//               .antMatchers(HttpMethod.POST,"/task/api/**").hasAuthority(UserPermission.TASK_ASSIGN.getPermission())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails anna = User.builder()
                .username("anna")
                .password(passwordEncoder.encode("password"))
//                .roles(UserRole.MEMBER.name())
                .authorities(UserRole.MEMBER.getGrantedAuthorities())
                .build();

        UserDetails linda = User.builder()
                .username("linda")
                .password(passwordEncoder.encode("password"))
//                .roles(UserRole.LEADER.name())
                .authorities(UserRole.LEADER.getGrantedAuthorities())
                .build();

        UserDetails mark = User.builder()
                .username("mark")
                .password(passwordEncoder.encode("password"))
//                .roles(UserRole.LEADER.name())
                .authorities(UserRole.TEMPLEADER.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(
                anna,
                linda,
                mark);
    }


}
