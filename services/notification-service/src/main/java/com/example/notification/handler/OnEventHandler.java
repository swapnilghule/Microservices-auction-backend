package com.example.notification.handler;


import com.fasterxml.jackson.databind.JsonNode;

public interface OnEventHandler {
    void handle(JsonNode payload);
}
