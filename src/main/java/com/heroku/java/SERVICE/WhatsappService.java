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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsappService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiUrl = "https://whatsapp.kwlabs.xyz/api/sendText";
    private final String sessionApiUrl = "https://whatsapp.kwlabs.xyz/api/sessions";
    private final String sessionId = "default"; // Define the session ID as 'default' or another ID if required
    private final String apiKey = System.getenv("WHATSAPP_API_KEY");

    public WhatsappService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String sendMessage(String chatId, String text) {
        String failedResponse = "Failed to send message";

        // Ensure session is created and started
        if (!isSessionAvailable() && !createSession()) {
            return "Failed to create session";
        }
        
        if (!startSession()) {
            return "Failed to start session";
        }

        // Prepare headers
        HttpHeaders headers = createHeaders();

        // Prepare JSON payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("chatId", chatId + "@c.us");
        payload.put("reply_to", null);
        payload.put("text", text);
        payload.put("session", sessionId);

        try {
            // Convert payload to JSON and wrap in HttpEntity
            String payloadJson = objectMapper.writeValueAsString(payload);
            HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

            // Send POST request
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Message sent successfully!");
                return response.getBody();
            } else {
                System.err.println("Failed to send message: " + response.getStatusCode());
                return failedResponse;
            }
        } catch (Exception e) {
            System.err.println("Exception while sending message: " + e.getMessage());
            e.printStackTrace();
            return failedResponse;
        }
    }

    private boolean isSessionAvailable() {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(sessionApiUrl, HttpMethod.GET, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Failed to check session availability: " + e.getMessage());
            return false;
        }
    }

    private boolean createSession() {
        HttpHeaders headers = createHeaders();
        Map<String, String> sessionData = new HashMap<>();
        sessionData.put("session", sessionId); // Provide any additional session data if necessary

        try {
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(sessionData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(sessionApiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Session created successfully!");
                return true;
            } else {
                System.err.println("Failed to create session: " + response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception while creating session: " + e.getMessage());
            return false;
        }
    }

   private boolean startSession() {
    HttpHeaders headers = createHeaders();
    HttpEntity<String> entity = new HttpEntity<>(null, headers);
    String startSessionUrl = sessionApiUrl + "/" + sessionId + "/start";

    try {
        // Attempt to start the session
        ResponseEntity<String> response = restTemplate.exchange(startSessionUrl, HttpMethod.POST, entity, String.class);

        // Check if the session started successfully
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Session started successfully!");
            return true;
        } else {
            System.err.println("Failed to start session: " + response.getStatusCode());
            // Check if the session requires a QR code scan
            if (response.getStatusCodeValue() == 422) {
                System.out.println("Session is in SCAN_QR_CODE status, retrieving QR code...");
                return retrieveQrCode();
            }
            return false;
        }
    } catch (Exception e) {
        System.err.println("Exception while starting session: " + e.getMessage());
        return false;
    }
}

// Method to retrieve and display the QR code for authentication
private boolean retrieveQrCode() {
    String qrCodeUrl = sessionApiUrl + "/" + sessionId + "/auth/qr?format=image";
    HttpHeaders headers = createHeaders();
    HttpEntity<String> entity = new HttpEntity<>(null, headers);

    try {
        ResponseEntity<byte[]> response = restTemplate.exchange(qrCodeUrl, HttpMethod.GET, entity, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            byte[] qrCodeImage = response.getBody();

            // Save QR code to a temporary directory
            Path path = Paths.get(System.getProperty("java.io.tmpdir"), "qr_code.png");
            Files.write(path, qrCodeImage);

            System.out.println("QR code saved to temporary file: " + path.toAbsolutePath() + ". Please scan it with WhatsApp.");

            return true;
        } else {
            System.err.println("Failed to retrieve QR code: " + response.getStatusCode());
            return false;
        }
    } catch (Exception e) {
        System.err.println("Exception while retrieving QR code: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}






    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
