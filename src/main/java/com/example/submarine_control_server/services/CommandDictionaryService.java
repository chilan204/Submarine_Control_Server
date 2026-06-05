package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.request.CommandDictionaryRequest;
import com.example.submarine_control_server.dto.response.CommandDictionaryResponse;

import java.util.List;

public interface CommandDictionaryService {
    List<CommandDictionaryResponse> getActiveCommandDictionaries();

    List<CommandDictionaryResponse> getAllCommandDictionary();

    CommandDictionaryResponse getCommandDictionaryById(Long id);

    CommandDictionaryResponse createCommandDictionary(CommandDictionaryRequest CommandDictionary);

    CommandDictionaryResponse updateCommandDictionary(Long id, CommandDictionaryRequest updatedCommandDictionary);

    void deleteCommandDictionary(Long id);
}
