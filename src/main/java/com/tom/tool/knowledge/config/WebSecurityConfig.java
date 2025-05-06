package com.tom.tool.knowledge.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.tom.tool.knowledge.common.ServiceLogger;
import com.tom.tool.knowledge.common.WhitelistLoader;
import com.tom.tool.knowledge.exception.AuthEntryPointJwt;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
	
	private final WhitelistLoader whitelist;
	private final AuthEntryPointJwt unauthorizedHandler;
	
	@Value("${application.security.user}")
	private String user;
	
	@Value("${application.security.password}")
	private String password;
	
	private String[] whiteListUrls;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    loadData();
	    http
	        .exceptionHandling(exception -> 
	            exception.authenticationEntryPoint(unauthorizedHandler))
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(whiteListUrls).permitAll()
	            .anyRequest().authenticated()
	        )
	        .httpBasic(Customizer.withDefaults()) 
	        .sessionManagement(sess -> 
	            sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .csrf(csrf -> csrf.disable());

	    return http.build();
	}
	
    @Bean
    UserDetailsService userDetailsService() {
        UserDetails username = User.builder()
            .username(user)
            .password(passwordEncoder().encode(password))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(username);
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(16);
    }
    
    private void loadData() {
    	try {
    	    whiteListUrls = whitelist.loadWhitelist();
    	} catch (IOException e) {
    		ServiceLogger.warn("Loaded whitelist URLs: " + Arrays.toString(whiteListUrls));
    	    throw new RuntimeException("Failed to load whitelist", e);
    	}
    }
}
