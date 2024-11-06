package com.heroku.java.SERVICE;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.ResponseEntity;
// import org.json.JSONObject;

// @Service
// public class WhatsappService {
//     private final String apiUrl = "https://whatsapp.kwlabs.xyz/api/sendText";

//     // Inject the API key from environment variables
//     @Value("${WHATSAPP_API_KEY}")
//     private String apiKey;

//     public String sendMessage(String chatId, String message) {
//         JSONObject payload = new JSONObject();
//         payload.put("chatId", chatId);
//         payload.put("reply_to", JSONObject.NULL);
//         payload.put("text", message);
//         payload.put("session", "default");

//         RestTemplate restTemplate = new RestTemplate();
//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Accept", "application/json");
//         headers.set("Content-Type", "application/json");
//         headers.set("Authorization", "Bearer " + apiKey); // Include the API key in the headers
//         HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

//         ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
//         return response.getBody();
//     }
// }

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsappService {

    private final RestTemplate restTemplate;
    private final String apiUrl = "https://whatsapp.kwlabs.xyz/api/sendText";
    private final String sessionStartUrl = "https://whatsapp.kwlabs.xyz/api/sessions/default/start";

    public WhatsappService() {
        this.restTemplate = new RestTemplate();
    }

    public String sendMessage(String chatId, String text) {
        String failedResponse = "Failed to send message";

        // Start the session if it is not active
        if (!startSessionIfNeeded()) {
            return "Failed to start session";
        }

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", System.getenv("WHATSAPP_API_KEY"));
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

        // Prepare the JSON payload
        JSONObject payload = new JSONObject();
        payload.put("chatId", chatId + "@c.us");
        payload.put("reply_to", JSONObject.NULL);
        payload.put("text", text);
        payload.put("session", "default");

        // Wrap headers and payload in HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

        try {
            // Send POST request with headers and payload
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            // Check the response status
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Message sent successfully!");
                return response.getBody(); // Return the response from the API
            } else {
                System.err.println("Failed to send message: " + response.getStatusCode());
                return failedResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return failedResponse;
        }
    }

    private boolean startSessionIfNeeded() {
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", System.getenv("WHATSAPP_API_KEY"));
            headers.set("Accept", "application/json");

            // Start the session with a POST request
            ResponseEntity<String> response = restTemplate.postForEntity(sessionStartUrl, new HttpEntity<>(headers), String.class);

            // Check if the session was started successfully
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Session 'default' started successfully!");
                return true;
            } else {
                System.err.println("Failed to start session: " + response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
