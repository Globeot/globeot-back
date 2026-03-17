package com.globeot.globeotback.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // JwtProvider에서 안전하게 userId 가져오기
                Long userId = jwtProvider.getUserId(token);

                JwtAuthentication authentication = new JwtAuthentication(userId);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
                // JWT 검증 실패 시 SecurityContext 초기화
                SecurityContextHolder.clearContext();
                // 로그 남기기 (선택)
                logger.warn("Invalid JWT token: {}");
            }
        }

        filterChain.doFilter(request, response);
    }
}