package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.response.VoiceCommandDetail;
import com.example.submarine_control_server.services.DroneCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DroneCommandServiceImpl
        implements DroneCommandService {

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

            case "TAKEOFF":
                handleTakeoff(value);
                break;

            case "LAND":
                handleLand();
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

    // TAKEOFF

    private void handleTakeoff(Integer altitude) {

        int finalAltitude =
                altitude != null ? altitude : 10;

        log.info(
                "Drone TAKEOFF to altitude {} meters",
                finalAltitude
        );

        /*
            TODO:
            MAVSDK / MAVLink / QGC API

            Example:
            drone.takeoff(finalAltitude);
         */
    }

    // LAND

    private void handleLand() {

        log.info("Drone LAND");

        /*
            TODO:
            drone.land();
         */
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

        /*
            TODO:
            drone.returnToLaunch();
         */
    }

    // ARM

    private void handleArm() {

        log.info("Drone ARM");

        /*
            TODO:
            drone.arm();
         */
    }

    // DISARM

    private void handleDisarm() {

        log.info("Drone DISARM");

        /*
            TODO:
            drone.disarm();
         */
    }

    // MOVE HELPERS

    private void moveForward(int value) {

        log.info("Drone MOVE FORWARD {} meters", value);

        /*
            TODO:
            MAVLink velocity / position control
         */
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