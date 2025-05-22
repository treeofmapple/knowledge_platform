package com.tom.service.knowledges.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Bean
    Bucket createNewBucket() {
    	Bandwidth limit = Bandwidth.builder()
    			.capacity(10)
    			.refillIntervally(10, Duration.ofMinutes(1))
    			.build();
    	
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
    								@NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
        	response.setContentType("application/json"); 
        	response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); 
            response.getWriter().write("Too many requests - try again later.");
        }
    }
    
}