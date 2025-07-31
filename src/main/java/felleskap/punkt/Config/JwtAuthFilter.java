package felleskap.punkt.Config;

import felleskap.punkt.security.jwt.JwtService;
import felleskap.punkt.security.user.UserDetailsServiceImpl;
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

        // Fortsett filterkjeden
        filterChain.doFilter(request, response);
    }
}
