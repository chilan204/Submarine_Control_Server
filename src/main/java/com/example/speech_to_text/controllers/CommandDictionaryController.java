package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.common.response.ResponseBaseList;
import com.example.speech_to_text.dto.request.CommandDictionaryRequest;
import com.example.speech_to_text.dto.response.CommandDictionaryResponse;
import com.example.speech_to_text.services.CommandDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/command-dictionaries")
public class CommandDictionaryController {
    private final CommandDictionaryService commandDictionaryService;

    @Autowired
    public CommandDictionaryController(CommandDictionaryService commandDictionaryService) {
        this.commandDictionaryService = commandDictionaryService;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<CommandDictionaryResponse>> getAllCommandDictionary() {

        List<CommandDictionaryResponse> list = commandDictionaryService.getAllCommandDictionary();

        return ResponseEntity.ok(
                ResponseBaseList.<CommandDictionaryResponse>builder()
                        .data(list)
                        .message("Get CommandDictionary list successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<CommandDictionaryResponse>> getCommandDictionaryById(
            @PathVariable Long id
    ) {
        CommandDictionaryResponse dto = commandDictionaryService.getCommandDictionaryById(id);

        return ResponseEntity.ok(
                ResponseBase.<CommandDictionaryResponse>builder()
                        .data(dto)
                        .message("Get CommandDictionary successfully")
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseBase<CommandDictionaryResponse>> createCommandDictionary(
            @RequestBody CommandDictionaryRequest commandDictionary
    ) {
        CommandDictionaryResponse dto = commandDictionaryService.createCommandDictionary(commandDictionary);

        notifyAiReloadCommandCache();

        return ResponseEntity.ok(
                ResponseBase.<CommandDictionaryResponse>builder()
                        .data(dto)
                        .message("Create CommandDictionary successfully")
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseBase<CommandDictionaryResponse>> updateCommandDictionary(
            @PathVariable Long id,
            @RequestBody CommandDictionaryRequest updateCommandDictionary
    ) {
        CommandDictionaryResponse dto = commandDictionaryService.updateCommandDictionary(id, updateCommandDictionary);

        notifyAiReloadCommandCache();

        return ResponseEntity.ok(
                ResponseBase.<CommandDictionaryResponse>builder()
                        .data(dto)
                        .message("Update CommandDictionary successfully")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBase<Void>> deleteCommandDictionary(
            @PathVariable Long id
    ) {
        commandDictionaryService.deleteCommandDictionary(id);

        notifyAiReloadCommandCache();

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Delete CommandDictionary successfully")
                        .build()
        );
    }

    private void notifyAiReloadCommandCache() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.postForEntity(
                    "http://localhost:5000/reload-command-cache",
                    null,
                    String.class
            );

        } catch (Exception e) {
            System.out.println("Cannot notify AI service: " + e.getMessage());
        }
    }
}