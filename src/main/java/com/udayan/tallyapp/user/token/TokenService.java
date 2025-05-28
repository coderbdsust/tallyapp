package com.udayan.tallyapp.user.token;

import com.udayan.tallyapp.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public void saveUserToken(User user, String userToken, TokenType tokenType) {
        var token = Token.builder()
                .user(user)
                .token(userToken)
                .expired(false)
                .revoked(false)
                .tokenType(tokenType.getValue())
                .build();
        tokenRepository.save(token);
    }

    public void revokeUserAllTokens(User user, TokenType tokenType) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId(), tokenType.getValue());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
           // token.setUpdatedDate(LocalDateTime.now());
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public boolean isTokenValid(String token){
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }
}
