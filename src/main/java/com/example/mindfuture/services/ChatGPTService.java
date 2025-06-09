package com.example.mindfuture.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatGPTService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public String getResponse(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        String jsonBody = """
            {
              "model": "gpt-3.5-turbo",
              "messages": [
                { "role": "system", "content": "Eres un experto en salud mental que ayuda a los pacientes con consejos amables y útiles." },
                { "role": "user", "content": "%s" }
              ]
            }
            """.formatted(userMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);  // Usa la variable inyectada

        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(ENDPOINT, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            return json.get("choices").get(0).get("message").get("content").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al comunicarse con ChatGPT.";
        }
    }
}
