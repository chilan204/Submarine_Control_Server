package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.request.CommandDictionaryRequest;
import com.example.speech_to_text.dto.response.CommandDictionaryResponse;

import java.util.List;

public interface CommandDictionaryService {
    List<CommandDictionaryResponse> getAllCommandDictionary();

    CommandDictionaryResponse getCommandDictionaryById(Long id);

    CommandDictionaryResponse createCommandDictionary(CommandDictionaryRequest CommandDictionary);

    CommandDictionaryResponse updateCommandDictionary(Long id, CommandDictionaryRequest updatedCommandDictionary);

    void deleteCommandDictionary(Long id);
}
