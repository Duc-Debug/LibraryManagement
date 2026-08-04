// package org.example.librarymanagement.infrastructure.security;

// import java.io.IOException;
// import java.util.Collections;

// import javax.crypto.SecretKey;

// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class JwtTokenFilter extends OncePerRequestFilter {

//     private final SecretKey signingKey;

//     public JwtTokenFilter(JwtProperties jwtProperties) {
//         byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
//         this.signingKey = Keys.hmacShaKeyFor(keyBytes);
//     }

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {

//         String header = request.getHeader("Authorization");

//         if (header != null && header.startsWith("Bearer ")) {
//             String token = header.substring(7);
//             try {
//                 Claims claims = Jwts.parser()
//                         .verifyWith(signingKey)
//                         .build()
//                         .parseSignedClaims(token)
//                         .getPayload();

//                 String username = claims.getSubject();

//                 if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                     UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                             username, null, Collections.emptyList()
//                     );
//                     authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                     SecurityContextHolder.getContext().setAuthentication(authentication);
//                 }
//             } catch (Exception e) {
//                 // Token không hợp lệ hoặc hết hạn
//                 SecurityContextHolder.clearContext();
//             }
//         }

//         filterChain.doFilter(request, response);
//     }
// }