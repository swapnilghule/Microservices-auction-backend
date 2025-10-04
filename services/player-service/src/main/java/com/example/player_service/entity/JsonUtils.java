package com.example.player_service.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {
    private final ObjectMapper mapper;


    public JsonUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String normalize(String raw) {
        if (raw == null) return "{}";
        try {
            JsonNode node = mapper.readTree(raw);
            if (node.isTextual()) return normalize(node.asText());
            return node.toString();
        } catch (Exception e) {
            return raw;
        }
    }

    public JsonNode parse(String raw) throws Exception {
        String normalized = normalize(raw);
        return mapper.readTree(normalized);
    }
}
