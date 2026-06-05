package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.request.CommandDictionaryRequest;
import com.example.submarine_control_server.dto.response.CommandDictionaryResponse;
import com.example.submarine_control_server.entities.CommandDictionary;
import com.example.submarine_control_server.mapper.CommandDictionaryMapper;
import com.example.submarine_control_server.repositories.CommandDictionaryRepository;
import com.example.submarine_control_server.services.CommandDictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final CommandDictionaryRepository commandDictionaryRepository;
    private final CommandDictionaryMapper commandDictionaryMapper;

    public CommandDictionaryServiceImpl(CommandDictionaryRepository commandDictionaryRepository, CommandDictionaryMapper commandDictionaryMapper) {
        this.commandDictionaryRepository = commandDictionaryRepository;
        this.commandDictionaryMapper = commandDictionaryMapper;
    }

    @Override
    public List<CommandDictionaryResponse> getAllCommandDictionary() {
        return commandDictionaryRepository.findAll().stream()
                .map(commandDictionaryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommandDictionaryResponse getCommandDictionaryById(Long id) {
        CommandDictionary commandDictionary = commandDictionaryRepository.findById(id).orElse(null);
        return commandDictionary != null ? commandDictionaryMapper.toResponseDTO(commandDictionary) : null;
    }

    @Override
    public CommandDictionaryResponse createCommandDictionary(CommandDictionaryRequest commandDictionaryDTO) {
        CommandDictionary commandDictionary = commandDictionaryMapper.toEntity(commandDictionaryDTO);
        CommandDictionary savedCommandDictionary = commandDictionaryRepository.save(commandDictionary);
        return commandDictionaryMapper.toResponseDTO(savedCommandDictionary);
    }

    @Override
    public CommandDictionaryResponse updateCommandDictionary(Long id, CommandDictionaryRequest updatedCommandDictionaryDTO) {
        CommandDictionary commandDictionary = commandDictionaryRepository.findById(id).orElse(null);
        if (commandDictionary != null) {
            commandDictionary.setKeyword(updatedCommandDictionaryDTO.getKeyword());
            commandDictionary.setDirection(updatedCommandDictionaryDTO.getDirection());
            commandDictionary.setAction(updatedCommandDictionaryDTO.getAction());
            commandDictionary.setHas_value(updatedCommandDictionaryDTO.getHasValue());
            CommandDictionary updatedCommandDictionary = commandDictionaryRepository.save(commandDictionary);
            return commandDictionaryMapper.toResponseDTO(updatedCommandDictionary);
        }
        return null;
    }

    @Override
    public void deleteCommandDictionary(Long id) {
        commandDictionaryRepository.deleteById(id);
    }
}