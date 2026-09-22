package com.example.submarine_control_server.controllers;

import com.example.submarine_control_server.dto.common.response.ResponseBaseList;
import com.example.submarine_control_server.dto.response.CommandDictionaryResponse;
import com.example.submarine_control_server.services.CommandDictionaryService;
import com.example.submarine_control_server.security.InternalApiAccessValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/command-admin")
public class CommandAIController {
    private final CommandDictionaryService commandDictionaryService;
    private final InternalApiAccessValidator internalApiAccessValidator;

    @Autowired
    public CommandAIController(
            CommandDictionaryService commandDictionaryService,
            InternalApiAccessValidator internalApiAccessValidator) {
        this.commandDictionaryService = commandDictionaryService;
        this.internalApiAccessValidator = internalApiAccessValidator;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<CommandDictionaryResponse>> getActiveCommandDictionaries(
            HttpServletRequest request) {
        internalApiAccessValidator.validate(request);

        List<CommandDictionaryResponse> data = commandDictionaryService.getActiveCommandDictionaries();

        return ResponseEntity.ok(
                ResponseBaseList.<CommandDictionaryResponse>builder()
                        .data(data)
                        .message("OK")
                        .build()
        );
    }
}
