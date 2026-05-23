package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.response.VoiceCommandDetail;
import com.example.speech_to_text.entities.Role;
import com.example.speech_to_text.services.CommandAuthorizationService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class CommandAuthorizationServiceImpl implements CommandAuthorizationService {

    private static final Map<String, Set<String>> ROLE_PERMISSIONS =
            Map.of(

                    "ADMIN",
                    Set.of(
                            "TAKEOFF",
                            "LAND",
                            "MOVE",
                            "RTL",
                            "ARM",
                            "DISARM",
                            "CHANGE_ROUTE"
                    ),

                    "OFFICER_5",
                    Set.of(
                            "TAKEOFF",
                            "LAND",
                            "MOVE",
                            "RTL",
                            "ARM",
                            "DISARM"
                    ),

                    "OFFICER_4",
                    Set.of(
                            "TAKEOFF",
                            "LAND",
                            "MOVE",
                            "RTL"
                    ),

                    "OFFICER_3",
                    Set.of(
                            "MOVE",
                            "RTL"
                    ),

                    "OFFICER_2",
                    Set.of(
                            "MOVE"
                    ),

                    "OFFICER_1",
                    Set.of(
                            "MOVE"
                    )
            );

    @Override
    public boolean isAllowed(
            Role role,
            VoiceCommandDetail command
    ) {

        if (role == null || command == null) {
            return false;
        }

        String roleCode = role.getCode();

        String action = command.getAction();

        if (roleCode == null || action == null) {
            return false;
        }

        Set<String> permissions =
                ROLE_PERMISSIONS.get(roleCode);

        if (permissions == null) {
            return false;
        }

        return permissions.contains(action);
    }
}