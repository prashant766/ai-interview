package com.prashant.AI_interview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .defaultHeader("Content-Type", "application/json")
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String askAI(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(

                        Map.of(
                                "role", "system",
                                "content", """
You are a technical interviewer.

Rules:
1. Start with very basic beginner questions.
2. Increase difficulty gradually.
3. Ask only ONE question at a time.
4. When the candidate answers, give feedback and a score out of 10.
5. Then ask the next slightly harder question.
"""
                        ),

                        Map.of(
                                "role", "user",
                                "content", prompt
                        )

                ),
                "temperature", 0.7
        );

        String response;

        try {

            response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {

            throw new RuntimeException("Groq API call failed: " + e.getMessage());

        }

        try {

            JsonNode root = objectMapper.readTree(response);

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {

            return "AI response parsing failed: " + e.getMessage();

        }
    }
}