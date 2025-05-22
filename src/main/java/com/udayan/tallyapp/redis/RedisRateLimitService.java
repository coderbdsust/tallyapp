package com.udayan.tallyapp.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisRateLimitService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Generic method to handle rate-limiting logic.
     *
     * @param key        The unique key for the rate limit (e.g., user ID or email).
     * @param limit      The maximum number of allowed requests in the time window.
     * @param minuteWindow The time window duration for the rate limit.
     * @param prefix     The prefix for the Redis key.
     * @return True if the action is allowed, otherwise False.
     */
    public boolean isActionAllowed(String key, int limit, Duration minuteWindow, RateLimit prefix) {
        String redisKey = prefix + ":" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count == 1) {
            redisTemplate.expire(redisKey, minuteWindow);
        }
        return count <= limit;
    }

    public boolean isVerifyUserAllowed(String key) {
        return isActionAllowed(key, 5, Duration.ofMinutes(1), RateLimit.VERIFY_USER);
    }

    public boolean isResendAccountVerificationOTPAllowed(String key) {
        return isActionAllowed(key, 3, Duration.ofMinutes(1), RateLimit.RESEND_ACCOUNT_VERIFICATION_OTP);
    }

    public boolean isForgotPasswordOTPGenerateAllowed(String key) {
        return isActionAllowed(key, 3, Duration.ofMinutes(1), RateLimit.FORGOT_PASSWORD_OTP_GENERATION);
    }

    public boolean isPasswordResetRequestAllowed(String key) {
        return isActionAllowed(key, 5, Duration.ofMinutes(1), RateLimit.PASSWORD_RESET_REQUEST);
    }

    public boolean isForgotPasswordOTPValidityAllowed(String key) {
        return isActionAllowed(key, 5, Duration.ofMinutes(1), RateLimit.FORGOT_PASSWORD_OTP_VALIDITY);
    }

    public boolean haveAccountLoginOTPVerificationLimit(String key) {
        return isActionAllowed(key, 5, Duration.ofMinutes(1), RateLimit.ACCOUNT_LOGIN_OTP);
    }
}
