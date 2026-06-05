package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.command.ArbitrationCommand;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.enums.CommandArbitrationStatus;
import com.example.submarine_control_server.services.CommandArbitrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CommandArbitrationServiceImpl implements CommandArbitrationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY = "uav:arb:commands";
    private static final long WINDOW = 2000; // ms

    @Override
    public CommandArbitrationStatus processCommand(User user, String command) {

        long submittedAt = System.currentTimeMillis();

        int myPriority = user.getRole().getPriority();

        ArbitrationCommand arbitrationCommand =
                new ArbitrationCommand(
                        user.getId(),
                        myPriority,
                        command,
                        submittedAt
                );

        try {

            String json = objectMapper.writeValueAsString(arbitrationCommand);

            redisTemplate.opsForZSet().add(KEY, json, submittedAt);

            redisTemplate.expire(KEY, Duration.ofMillis(WINDOW * 3));

            // Chờ thêm WINDOW để thu thập command gần nhau
            Thread.sleep(WINDOW);

            long from = submittedAt - WINDOW;
            long to = submittedAt + WINDOW;

            Set<String> participants =
                    redisTemplate.opsForZSet().rangeByScore(KEY, from, to);

            if (participants == null || participants.isEmpty()) {
                return CommandArbitrationStatus.REJECTED_LOW_PRIORITY;
            }

            int maxPriority = Integer.MIN_VALUE;
            int maxPriorityCount = 0;

            for (String participant : participants) {

                ArbitrationCommand participantCommand =
                        objectMapper.readValue(
                                participant,
                                ArbitrationCommand.class
                        );

                int priority = participantCommand.getPriority();

                if (priority > maxPriority) {
                    maxPriority = priority;
                    maxPriorityCount = 1;
                } else if (priority == maxPriority) {
                    maxPriorityCount++;
                }
            }

            if (myPriority < maxPriority) {
                return CommandArbitrationStatus.REJECTED_LOW_PRIORITY;
            }

            if (maxPriorityCount > 1) {
                return CommandArbitrationStatus.CONFLICT_SAME_ROLE;
            }

            // cleanup dữ liệu cũ
            redisTemplate.opsForZSet()
                    .removeRangeByScore(
                            KEY,
                            0,
                            submittedAt - (WINDOW * 3)
                    );

            return CommandArbitrationStatus.EXECUTED;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Command arbitration interrupted",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to process arbitration command",
                    e
            );
        }
    }
}