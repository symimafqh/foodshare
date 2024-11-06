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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class WhatsappService {

    private RestTemplate restTemplate;

    public WhatsappService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendMessage(String message) {
        String url = "https://whatsapp.kwlabs.xyz/api/sendText"; // Replace with actual URL

        // Prepare the authorization header (replace YOUR_ACCESS_TOKEN with your actual token)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + System.getenv("WHATSAPP_API_KEY")); // or use the actual token

        // Prepare the payload for the request (if necessary)
        String payload = "{\"message\": \"" + message + "\"}"; // Example payload, adapt as necessary

        // Wrap the payload and headers into an HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        try {
            // Send POST request with headers and payload
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Handle the response (optional)
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Message sent successfully!");
            } else {
                System.err.println("Failed to send message: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions here
        }
    }

    public String sendMessage(String chatId, String messageBody) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
    }
}
