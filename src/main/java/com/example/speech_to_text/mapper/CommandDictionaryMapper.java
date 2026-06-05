package com.example.speech_to_text.mapper;

import com.example.speech_to_text.dto.request.CommandDictionaryRequest;
import com.example.speech_to_text.dto.response.CommandDictionaryResponse;
import com.example.speech_to_text.entities.CommandDictionary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandDictionaryMapper {
    
    public CommandDictionary toEntity(CommandDictionaryRequest dto) {
        if (dto == null) return null;

        CommandDictionary commandDictionary = new CommandDictionary();
        commandDictionary.setKeyword(dto.getKeyword());
        commandDictionary.setAction(dto.getAction());
        commandDictionary.setDirection(dto.getDirection());
        commandDictionary.setHas_value(dto.getHasValue());

        return commandDictionary;
    }

    public CommandDictionaryResponse toResponseDTO(CommandDictionary entity) {
        if (entity == null) return null;

        CommandDictionaryResponse dto = new CommandDictionaryResponse();
        dto.setId(entity.getId());
        dto.setKeyword(entity.getKeyword());
        dto.setAction(entity.getAction());
        dto.setDirection(entity.getDirection());
        dto.setHasValue(entity.getHas_value());

        return dto;
    }
}