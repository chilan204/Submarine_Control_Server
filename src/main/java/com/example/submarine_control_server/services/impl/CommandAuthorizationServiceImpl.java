package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.response.VoiceCommandDetail;
import com.example.submarine_control_server.entities.Role;
import com.example.submarine_control_server.services.CommandAuthorizationService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class CommandAuthorizationServiceImpl implements CommandAuthorizationService {

    private static final Map<String, Set<String>> ROLE_PERMISSIONS =
            Map.of(

                    "ADMIN",
                    Set.of(
                            "DIVE",
                            "SURFACE",
                            "MOVE",
                            "RTL",
                            "ARM",
                            "DISARM",
                            "CHANGE_ROUTE",
                            "HOLD_POSITION"
                    ),

                    "OFFICER_5",
                    Set.of(
                            "DIVE",
                            "SURFACE",
                            "MOVE",
                            "RTL",
                            "ARM",
                            "DISARM",
                            "HOLD_POSITION"
                    ),

                    "OFFICER_4",
                    Set.of(
                            "DIVE",
                            "SURFACE",
                            "MOVE",
                            "RTL",
                            "HOLD_POSITION"
                    ),

                    "OFFICER_3",
                    Set.of(
                            "MOVE",
                            "RTL",
                            "HOLD_POSITION"
                    ),

                    "OFFICER_2",
                    Set.of(
                            "MOVE",
                            "RTL"
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