package com.example.submarine_control_server.mapper;

import com.example.submarine_control_server.dto.request.CommandDictionaryRequest;
import com.example.submarine_control_server.dto.response.CommandDictionaryResponse;
import com.example.submarine_control_server.entities.CommandDictionary;
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
        commandDictionary.setActive(dto.getActive());

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
        dto.setActive(entity.getActive());

        return dto;
    }
}