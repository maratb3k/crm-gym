package com.example.crm_gym.security;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

@Service
public class BruteForceProtectionService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 5;
    private Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private Map<String, LocalDateTime> lockoutTimeCache = new HashMap<>();

    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            lockoutTimeCache.put(username, LocalDateTime.now());
        }
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockoutTimeCache.remove(username);
    }

    public boolean isBlocked(String username) {
        if (!lockoutTimeCache.containsKey(username)) {
            return false;
        }

        LocalDateTime lockoutTime = lockoutTimeCache.get(username);
        if (lockoutTime.plusMinutes(LOCK_TIME_DURATION).isBefore(LocalDateTime.now())) {
            lockoutTimeCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        return true;
    }
}