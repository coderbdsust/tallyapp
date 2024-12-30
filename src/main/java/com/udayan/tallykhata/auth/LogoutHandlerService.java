package com.udayan.tallykhata.auth;

import com.udayan.tallykhata.auth.exp.InvalidTokenException;
import com.udayan.tallykhata.security.jwt.JwtService;
import com.udayan.tallykhata.user.User;
import com.udayan.tallykhata.user.UserRepository;
import com.udayan.tallykhata.user.token.TokenService;
import com.udayan.tallykhata.user.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutHandlerService implements LogoutHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        log.info("logout request");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        var accessToken = authHeader.substring(7);

        User user = null;

        try {
            String username = jwtService.extractUsername(accessToken);
            user = userRepository.findByUsername(username).orElseThrow(
                    () -> new InvalidTokenException("User token not valid")
            );
        } catch (Exception e) {
            log.error("", e);
        }

        if (user != null) {
            tokenService.revokeUserAllTokens(user, TokenType.ACCESS_TOKEN);
            tokenService.revokeUserAllTokens(user, TokenType.REFRESH_TOKEN);
            SecurityContextHolder.clearContext();
        }
    }
}
