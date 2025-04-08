package com.udayan.tallyapp.auth;

import com.udayan.tallyapp.customexp.InvalidTokenException;
import com.udayan.tallyapp.redis.RedisTokenService;
import com.udayan.tallyapp.security.jwt.JwtService;
import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserRepository;
import com.udayan.tallyapp.user.token.TokenService;
import com.udayan.tallyapp.user.token.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutHandlerService implements LogoutHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RedisTokenService redisTokenService;

    @Value("${application.security.web.origin}")
    private String applicationSecurityWebOrigin;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        log.info("Logout request");

        String accessToken = null;
        boolean isBrowser = isBrowserRequest(request);

        if (isBrowser) {
            // Get token from cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("X-Tally-Access-Token".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }
        } else {
            // Get token from Authorization header
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }
        }

        if (accessToken == null) {
            log.warn("No access token provided during logout");
            return;
        }

        User user = null;
        try {
            String username = jwtService.extractUsername(accessToken);
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new InvalidTokenException("User token not valid"));
        } catch (Exception e) {
            log.error("Error extracting username from token", e);
        }

        if (user != null) {
            // Revoke tokens
            tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
            tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
            redisTokenService.deleteToken(user.getUsername(), TokenType.ACCESS_TOKEN);
        }

        // Clear security context
        SecurityContextHolder.clearContext();

        // Expire cookies if it's a browser logout
        if (isBrowser) {
            log.info("Expiring cookies for browser logout");
            expireCookie("X-Tally-Access-Token", "/", response);
            expireCookie("X-Tally-Refresh-Token", "/", response);
        }
    }

    public void expireCookie(String name, String path, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path(path)
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private boolean isBrowserRequest(HttpServletRequest request, List<String> allowedOrigins) {
        String userAgent = request.getHeader("User-Agent");
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        log.debug("userAgent: {}",userAgent);
        log.debug("origin: {}",origin);
        log.debug("referer: {}",referer);

        boolean fromUserAgent = userAgent != null && userAgent.toLowerCase().contains("mozilla");
        boolean fromAllowedOrigin = origin != null && allowedOrigins.stream().anyMatch(origin::contains);
        boolean fromAllowedReferer = referer != null && allowedOrigins.stream().anyMatch(referer::contains);

        return fromUserAgent || fromAllowedOrigin || fromAllowedReferer;
    }

    private boolean isBrowserRequest(HttpServletRequest request){
        return this.isBrowserRequest(request, Arrays.asList(applicationSecurityWebOrigin.split(",")));
    }

}
