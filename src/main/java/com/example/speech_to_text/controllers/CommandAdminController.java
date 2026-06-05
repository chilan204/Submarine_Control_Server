package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBaseList;
import com.example.speech_to_text.dto.response.CommandDictionaryResponse;
import com.example.speech_to_text.services.CommandDictionaryService;
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