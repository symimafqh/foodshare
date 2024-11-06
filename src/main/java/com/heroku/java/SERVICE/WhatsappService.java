package com.heroku.java.SERVICE;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

@Service
public class WhatsappService {
    private final String apiUrl = "https://whatsapp.kwlabs.xyz/api/sendText";

    // Inject the API key from environment variables
    @Value("${WHATSAPP_API_KEY}")
    private String apiKey;

    public String sendMessage(String chatId, String message) {
        JSONObject payload = new JSONObject();
        payload.put("chatId", chatId);
        payload.put("reply_to", JSONObject.NULL);
        payload.put("text", message);
        payload.put("session", "default");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey); // Include the API key in the headers
        HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        return response.getBody();
    }
}