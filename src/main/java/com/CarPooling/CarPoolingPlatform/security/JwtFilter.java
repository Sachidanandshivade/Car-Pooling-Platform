package com.CarPooling.CarPoolingPlatform.security;


<<<<<<< HEAD
import com.CarPooling.CarPoolingPlatform.service.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
=======
import com.CarPooling.CarPoolingPlatform.service.CustomeruserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
>>>>>>> f23408174ba710020cb531aa74c34512142e947a
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> f23408174ba710020cb531aa74c34512142e947a

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
<<<<<<< HEAD
    private final CustomerUserDetailsService userDetailsService;
=======
    private final CustomeruserDetailsService userDetailsService;
>>>>>>> f23408174ba710020cb531aa74c34512142e947a

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

<<<<<<< HEAD
        System.out.println(">>> REQUEST PATH: " + request.getServletPath());

        if (request.getServletPath().startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        System.out.println(">>> AUTH HEADER: " + authHeader);
=======
        final String authHeader = request.getHeader("Authorization");
>>>>>>> f23408174ba710020cb531aa74c34512142e947a

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
<<<<<<< HEAD
        System.out.println(">>> EMAIL FROM TOKEN: " + email);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token)) {
                String role = jwtUtil.extractRole(token);
                System.out.println(">>> ROLE FROM TOKEN: " + role);
                System.out.println(">>> AUTHORITY BEING SET: ROLE_" + role);
=======

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token)) {
>>>>>>> f23408174ba710020cb531aa74c34512142e947a

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
<<<<<<< HEAD
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println(">>> AUTH SET IN CONTEXT: " + SecurityContextHolder.getContext().getAuthentication());
                System.out.println(">>> AUTHORITIES: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
=======
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 🔥 THIS LINE IS CRITICAL
                SecurityContextHolder.getContext().setAuthentication(authToken);
>>>>>>> f23408174ba710020cb531aa74c34512142e947a
            }
        }

        filterChain.doFilter(request, response);
<<<<<<< HEAD
    }

    }

=======

    }
}
>>>>>>> f23408174ba710020cb531aa74c34512142e947a
