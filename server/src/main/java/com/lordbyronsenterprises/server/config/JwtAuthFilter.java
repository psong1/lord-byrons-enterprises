package com.lordbyronsenterprises.server.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lordbyronsenterprises.server.user.User;
import com.lordbyronsenterprises.server.user.UserDetailsServiceImplementation;
import com.lordbyronsenterprises.server.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImplementation userDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
         final String authHeader = request.getHeader("Authorization");
         final String jwt;
         final String username;

         // Skip JWT processing for login and registration endpoints
         String path = request.getRequestURI();
         String method = request.getMethod();
         if (path.equals("/auth/login") || (path.equals("/user") && method.equals("POST"))) {
             filterChain.doFilter(request, response);
             return;
         }

         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
             filterChain.doFilter(request, response);
             return;
         }

         try {
             jwt = authHeader.substring(7);
             username = jwtService.extractUsername(jwt);

             if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                 UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                 if (jwtService.isTokenValid(jwt, userDetails)) {
                     // Get the actual User entity to set as principal
                     User user = userRepository.findUserByUsername(username)
                             .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                     
                     UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                             user,  // Use User entity instead of UserDetails
                             null,
                             userDetails.getAuthorities()
                     );
                     authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                     SecurityContextHolder.getContext().setAuthentication(authToken);
                 }
             }
         } catch (Exception e) {
             // If JWT processing fails, continue without authentication
             // This allows the request to proceed and be handled by Spring Security
         }
         
         filterChain.doFilter(request, response);
    }
}
