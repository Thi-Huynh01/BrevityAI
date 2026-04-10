package com.brevityai.backend.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor
@Data
@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public boolean isDuplicate(String sentence) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember("generated:sentences", normalize(sentence))
        );
    }

    public void storeSentence(String sentence) {
        String key = "generated:sentences";
        redisTemplate.opsForSet().add(key, normalize(sentence));

        // expire after 24 hours to prevent memory issues
        redisTemplate.expire(key, Duration.ofHours(24));
    }

    private String normalize(String sentence) {
        return sentence.trim().toLowerCase();
    }

    public void addMessage(String userID, String sessionID, String messageJson) {
        String key = "chat:" + userID + ":" + sessionID;
        redisTemplate.opsForList().leftPush(key, messageJson);

        // keep only last 20 messages
        redisTemplate.opsForList().trim(key, 0, 19);

        //expire after 1 hour
        redisTemplate.expire(key, Duration.ofHours(1));
    }

    public List<String> getRecentMessages(String userID, String sessionID) {
        String key = "chat:" + userID + ":" + sessionID;
        return redisTemplate.opsForList().range(key, 0, 19);
    }
}
