package felleskap.punkt.Config;

import felleskap.punkt.security.jwt.JwtService;
import felleskap.punkt.security.user.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    // Eksplisitt konstruktør for dependency injection
    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Hvis header ikke finnes eller ikke starter med "Bearer ", hopp over filteret
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Hent JWT-token uten "Bearer " prefix
            final String jwt = authHeader.substring(7);

            // Hent brukernavn fra token (kan være null hvis token ikke gyldig eller utløpt)
            final String username = jwtService.extractUsername(jwt);

            // Sjekk at brukernavn finnes og at ingen allerede er autentisert i konteksten
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Last inn brukerdetaljer fra database eller annen lagring
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Sjekk at token er gyldig for denne brukeren
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Sett opp autentisering i Spring Security kontekst
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            // Token is expired - log and continue without authentication
            // This allows the request to reach public endpoints like /api/auth/login
            System.out.println("JWT token expired: " + e.getMessage());
        } catch (Exception e) {
            // Any other JWT parsing error - log and continue
            System.out.println("JWT parsing error: " + e.getMessage());
        }

        // Fortsett filterkjeden
        filterChain.doFilter(request, response);
    }
}
