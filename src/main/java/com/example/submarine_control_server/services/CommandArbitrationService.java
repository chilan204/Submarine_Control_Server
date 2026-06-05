package com.example.submarine_control_server.services;

import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.enums.CommandArbitrationStatus;

public interface CommandArbitrationService {

    CommandArbitrationStatus processCommand(User user, String command);
}