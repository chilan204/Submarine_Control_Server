package com.example.submarine_control_server.enums;

public enum CommandArbitrationStatus {
    EXECUTED,
    REJECTED_LOW_PRIORITY,
    REJECTED_SUPERSEDED,
    REJECTED_BUSY,
    CONFLICT_SAME_ROLE
}
