package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.response.VoiceCommandDetail;
import com.example.submarine_control_server.services.AuvCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuvCommandServiceImpl implements AuvCommandService {

    @Override
    public void execute(VoiceCommandDetail command) {

        if (command == null) {
            throw new RuntimeException("Command is null");
        }

        String action = command.getAction();
        String direction = command.getDirection();
        Integer value = command.getValue();

        log.info(
                "Executing drone command: action={}, direction={}, value={}",
                action,
                direction,
                value
        );

        switch (action) {

            case "DIVE":
                handleDive(value);
                break;

            case "SURFACE":
                handleSurface();
                break;

            case "MOVE":
                handleMove(direction, value);
                break;

            case "RTL":
                handleRTL();
                break;

            case "ARM":
                handleArm();
                break;

            case "DISARM":
                handleDisarm();
                break;

            default:
                throw new RuntimeException(
                        "Unsupported action: " + action
                );
        }
    }

    // DIVE

    private void handleDive(Integer depth) {

        int finalDepth =
                depth != null ? depth : 10;

        log.info(
                "AUV DIVE to depth {} meters",
                finalDepth
        );
    }

    // SURFACE

    private void handleSurface() {

        log.info("Drone SURFACE");
    }

    // MOVE

    private void handleMove(
            String direction,
            Integer value
    ) {

        int distance =
                value != null ? value : 5;

        log.info(
                "Drone MOVE direction={} value={}",
                direction,
                distance
        );

        if (direction == null) {
            throw new RuntimeException(
                    "Direction is required for MOVE"
            );
        }

        switch (direction) {

            case "FORWARD":
                moveForward(distance);
                break;

            case "BACKWARD":
                moveBackward(distance);
                break;

            case "LEFT":
                moveLeft(distance);
                break;

            case "RIGHT":
                moveRight(distance);
                break;

            case "UP":
                moveUp(distance);
                break;

            case "DOWN":
                moveDown(distance);
                break;

            default:
                throw new RuntimeException(
                        "Unsupported direction: " + direction
                );
        }
    }

    // RTL

    private void handleRTL() {

        log.info("Drone RETURN TO LAUNCH");
    }

    // ARM

    private void handleArm() {

        log.info("Drone ARM");
    }

    // DISARM

    private void handleDisarm() {

        log.info("Drone DISARM");
    }

    // MOVE HELPERS

    private void moveForward(int value) {

        log.info("Drone MOVE FORWARD {} meters", value);
    }

    private void moveBackward(int value) {

        log.info("Drone MOVE BACKWARD {} meters", value);
    }

    private void moveLeft(int value) {

        log.info("Drone MOVE LEFT {} meters", value);
    }

    private void moveRight(int value) {

        log.info("Drone MOVE RIGHT {} meters", value);
    }

    private void moveUp(int value) {

        log.info("Drone MOVE UP {} meters", value);
    }

    private void moveDown(int value) {

        log.info("Drone MOVE DOWN {} meters", value);
    }
}