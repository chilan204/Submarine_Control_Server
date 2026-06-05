package com.example.submarine_control_server.controllers;

import com.example.submarine_control_server.dto.common.response.ResponseBaseList;
import com.example.submarine_control_server.dto.response.CommandDictionaryResponse;
import com.example.submarine_control_server.services.CommandDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/command-admin")
public class CommandAdminController {
    private final CommandDictionaryService commandDictionaryService;

    @Autowired
    public CommandAdminController(CommandDictionaryService commandDictionaryService) {
        this.commandDictionaryService = commandDictionaryService;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<CommandDictionaryResponse>> getAllCommandDictionary() {

        List<CommandDictionaryResponse> data = commandDictionaryService.getAllCommandDictionary();

        return ResponseEntity.ok(
                ResponseBaseList.<CommandDictionaryResponse>builder()
                        .data(data)
                        .message("OK")
                        .build()
        );
    }
}