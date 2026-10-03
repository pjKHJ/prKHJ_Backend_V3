package dsm.prkhj.global.security;

import dsm.prkhj.domain.extension.exception.ExtensionErrorCode;
import dsm.prkhj.global.redis.ExtensionTokenStore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ExtensionTokenFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Extension-Token";
    public static final String ROLE = "EXTENSION";

    private static final RequestMatcher EXTENSION_REQUESTS = new OrRequestMatcher(
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/v1/extension/link"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.DELETE, "/api/v1/extension/link")
            // POST /extension/link X
    );

    private final ExtensionTokenStore extensionTokenStore;
    private final SecurityErrorHandler securityErrorHandler;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !EXTENSION_REQUESTS.matches(request);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = request.getHeader(HEADER);

        Long userId;
        if (token != null && !token.isBlank()) {
            userId = extensionTokenStore.findUserId(token);
        } else userId = null;

        if (userId == null) {
            securityErrorHandler.write(response, ExtensionErrorCode.INVALID_EXTENSION_TOKEN, request.getRequestURI());
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new ExtensionPrincipal(userId), null, List.of(new SimpleGrantedAuthority("ROLE_" + ROLE))));
        filterChain.doFilter(request, response);
    }
}
