package com.udayan.tallyapp.redis;

import com.udayan.tallyapp.user.token.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveToken(String username, TokenType tokenType, String token, long expirationInSeconds) {
        String key = tokenType+":"+username;
        redisTemplate.opsForValue().set( key, token, Duration.ofSeconds(expirationInSeconds));
    }

    public void deleteToken(String username, TokenType tokenType) {
        String key = tokenType+":"+username;
        redisTemplate.delete(key);
    }

    public boolean isTokenValid(String username, TokenType tokenType, String tokenData) {
        String key = tokenType+":"+username;
        String token = (String) redisTemplate.opsForValue().get(key);
        return tokenData.equals(token);
    }

}
