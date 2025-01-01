package com.mazid.electronic.store.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private Jwt jwt;

    @Autowired
    private UserDetailsService userDetailsService;

    private final Logger logger= LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Skip filtering for /google-login endpoint
//        if (request.getRequestURI().equals("/google-login")) {
//
//            filterChain.doFilter(request, response);  // Skip the filter for this route
//            return;
//        }


        // Get token from request
        String requestHeader = request.getHeader("Authorization");
        logger.info("Header: {}", requestHeader);

        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7);
            try {
                username= jwt.getUsernameFromToken(token);
                logger.info("Username: {}", username);
            } catch (IllegalArgumentException e) {
                logger.error("Illegal argument while getting username from token");
            }catch (ExpiredJwtException e){
                logger.error("Token is expired");
            }catch (MalformedJwtException e){
                logger.error("Token is invalid");
            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // load user associated with token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate token
            if (username.equals(userDetails.getUsername()) && !jwt.isTokenExpired(token)) {

                // if everything is good then set authentication in security context
                UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
               authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);


            }
        }

        filterChain.doFilter(request, response);







    }
}
