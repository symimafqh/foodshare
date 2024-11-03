// package com.heroku.java.CONTROLLER.leftover;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.multipart.MultipartFile;

// import com.heroku.java.MODEL.leftover.LeftoverBean;

// import jakarta.servlet.http.HttpSession;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import javax.sql.DataSource;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// @Controller
// public class AddLeftoverController {
//     private final DataSource dataSource;

//     @Autowired
//     public AddLeftoverController(DataSource dataSource) {
//         this.dataSource = dataSource;
//     }

//     // Process adding a new leftover
//     @PostMapping("addLeftover")
//     public String addLeftover(
//             @RequestParam("foodname") String foodName,
//             @RequestParam("foodquantity") int foodQuantity,
//             @RequestParam("fooddescription") String foodDescription,
//             @RequestParam("image") MultipartFile imageFile,
//             HttpSession session, Model model) {

//         String cafeNumber = (String) session.getAttribute("cafeNumber");
//         System.out.println("Cafe Number: " + cafeNumber);

//         String imagePath = ""; // Placeholder for the image path

//         try {
//             // Save the image to the filesystem if not empty
//             if (!imageFile.isEmpty()) {
//                 // Generate unique file name and save the file
//                 String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
//                 Path uploadPath = Paths.get("src/main/resources/public/stylesheets/assets/leftover", fileName);

//                 // Ensure directory exists
//                 Files.createDirectories(uploadPath.getParent());
//                 Files.write(uploadPath, imageFile.getBytes());

//                 // Set the imagePath as relative path to be stored in the database
//                 imagePath = "/stylesheets/assets/leftover/" + fileName;
//             }

//             // Insert leftover data into the database
//             Connection connection = dataSource.getConnection();
//             String sql = "INSERT INTO public.leftover (\"foodname\", \"foodquantity\", \"fooddescription\", \"image_path\", \"cafeNumber\") VALUES (?, ?, ?, ?, ?)";
//             PreparedStatement statement = connection.prepareStatement(sql);

//             statement.setString(1, foodName);
//             statement.setInt(2, foodQuantity);
//             statement.setString(3, foodDescription);
//             statement.setString(4, imagePath);
//             statement.setString(5, cafeNumber);

//             System.out.println("Food Name: " + foodName);
//             System.out.println("Food Quantity: " + foodQuantity);
//             System.out.println("Food Description: " + foodDescription);
//             System.out.println("Image Path: " + imagePath);

//             statement.executeUpdate();

//             connection.close();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         return "redirect:/dashboardCafe?success=true";
//     }
// }
//---------------------------------------------------------twilio-----------------------------------------
// package com.heroku.java.CONTROLLER.leftover;

// import com.twilio.Twilio;
// import com.twilio.rest.api.v2010.account.Message;
// import com.twilio.type.PhoneNumber;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.multipart.MultipartFile;

// import com.heroku.java.MODEL.leftover.LeftoverBean;

// import jakarta.servlet.http.HttpSession;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import javax.sql.DataSource;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.ArrayList;
// import java.util.List;

// import com.twilio.Twilio;
// import com.twilio.rest.api.v2010.account.Message;
// import com.twilio.type.PhoneNumber;

// @Controller
// public class AddLeftoverController {

//     private final DataSource dataSource;

//     @Autowired
//     public AddLeftoverController(DataSource dataSource) {
//         this.dataSource = dataSource;
//     }

//     @PostMapping("/addLeftover")
//     public String addLeftover(@ModelAttribute("addLeftover") LeftoverBean leftover, HttpSession session, Model model) {

//         System.out.println("Received POST request for adding leftover.");
//         try {
//             // Step 1: Add leftover to the database (similar to before)
//             Connection connection = dataSource.getConnection();
//             String sql = "INSERT INTO public.leftover (\"foodname\", \"foodquantity\", \"fooddescription\", \"image_path\", \"cafeNumber\") VALUES (?, ?, ?, ?, ?)";
//             final var statement = connection.prepareStatement(sql);
//             statement.setString(1, leftover.getFoodname());
//             statement.setInt(2, leftover.getFoodquantity());
//             statement.setString(3, leftover.getFooddescription());
//             statement.setString(4, leftover.getImagePath());
//             String cafeNumber = (String) session.getAttribute("cafeNumber");
//             statement.setString(5, cafeNumber);
//             statement.executeUpdate();
//             connection.close();
//             System.out.println("Received request to add leftover");

//             // Step 2: Send WhatsApp notifications to students
//             notifyStudents(leftover);

//             return "redirect:/dashboardCafe?success=true";

//         } catch (Exception e) {
//             e.printStackTrace();
//             return "redirect:/addLeftover?error=true";
//         }
//     }

//     private void notifyStudents(LeftoverBean leftover) {
//         // Getting credentials from environment variables securely
//         String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
//         String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
//         String FROM_WHATSAPP_NUMBER = System.getenv("TWILIO_WHATSAPP_NUMBER");
    
//         // Initialize Twilio SDK
//         Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    
//         // Step 1: Get list of student phone numbers
//         List<String> studentNumbers = getStudentPhoneNumbers();
    
//         // Step 2: Create the message to be sent
//         String messageBody = "New leftover food available!\n" +
//                 "Food Name: " + leftover.getFoodname() + "\n" +
//                 "Quantity: " + leftover.getFoodquantity() + "\n" +
//                 "Description: " + leftover.getFooddescription() + "\n" +
//                 "Hurry up and reserve it before it's gone!";
    
//         // Step 3: Send the message to each student
//         for (String studentNumber : studentNumbers) {
//             try {
//                 Message message = Message.creator(
//                         new PhoneNumber("whatsapp:" + studentNumber),  // Ensure the 'To' number is formatted for WhatsApp
//                         new PhoneNumber(FROM_WHATSAPP_NUMBER),         // The 'From' number also should be in WhatsApp format
//                         messageBody
//                 ).create();
//                 System.out.println("Message sent to: " + studentNumber);
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 System.out.println("Failed to send message to: " + studentNumber);
//             }
//         }
//     }
    

//     private List<String> getStudentPhoneNumbers() {
//         List<String> numbers = new ArrayList<>();
//         try {
//             // Query the student database to get all student phone numbers
//             Connection connection = dataSource.getConnection();
//             String sql = "SELECT studentphonenumber FROM public.student WHERE studentphonenumber IS NOT NULL";
//             PreparedStatement statement = connection.prepareStatement(sql);
//             ResultSet resultSet = statement.executeQuery();
            

//             while (resultSet.next()) {
//                 String phoneNumber = resultSet.getString("studentphonenumber");
//                 if (phoneNumber != null && !phoneNumber.isEmpty()) {
//                     numbers.add(phoneNumber);
//                     System.out.println(phoneNumber);
//                 }
//             }
//             connection.close();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return numbers;
//     }
// }
package com.heroku.java.CONTROLLER.leftover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heroku.java.MODEL.leftover.LeftoverBean;

import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AddLeftoverController {
    private final DataSource dataSource;

    @Autowired
    public AddLeftoverController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/addLeftover")
    public String addLeftover(@ModelAttribute("addLeftover") LeftoverBean leftover, HttpSession session, Model model) {
        System.out.println("Received POST request for adding leftover.");
        try {
            // Step 1: Add leftover to the database
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO public.leftover (\"foodname\", \"foodquantity\", \"fooddescription\", \"image_path\", \"cafeNumber\") VALUES (?, ?, ?, ?, ?)";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, leftover.getFoodname());
            statement.setInt(2, leftover.getFoodquantity());
            statement.setString(3, leftover.getFooddescription());
            statement.setString(4, leftover.getImagePath());
            String cafeNumber = (String) session.getAttribute("cafeNumber");
            statement.setString(5, cafeNumber);
            statement.executeUpdate();
            connection.close();
            System.out.println("Leftover added to the database.");

            // Step 2: Notify students via WhatsApp using Vonage
            notifyStudents(leftover);

            return "redirect:/dashboardCafe?success=true";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/addLeftover?error=true";
        }
    }

  private void notifyStudents(LeftoverBean leftover) {
    List<String> studentNumbers = getStudentPhoneNumbers();

    String fromNumber = System.getenv("VONAGE_WHATSAPP_NUMBER");
    String apiKey = System.getenv("VONAGE_API_KEY");
    String apiSecret = System.getenv("VONAGE_API_SECRET");

    for (String studentNumber : studentNumbers) {
        try {
            System.out.println("Attempting to send WhatsApp message to: " + studentNumber);
            sendWhatsAppMessage(apiKey, apiSecret, fromNumber, studentNumber, leftover.getFoodname(), leftover.getFoodquantity(), leftover.getFooddescription());
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp message to: " + studentNumber);
            e.printStackTrace();
        }
    }
}

private void sendWhatsAppMessage(String apiKey, String apiSecret, String from, String to, String foodName, int quantity, String description) throws Exception {
    String url = "https://api.nexmo.com/v1/messages";

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(apiKey, apiSecret);
    headers.add("Content-Type", "application/json");

    // Construct JSON payload with the correct structure
    String payload = createWhatsAppPayload(from, to, foodName, quantity, description);

    // Log the headers and payload separately for better clarity
    System.out.println("Headers: " + headers);
    System.out.println("Payload: " + payload);

    HttpEntity<String> entity = new HttpEntity<>(payload, headers);
    try {
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println("WhatsApp message sent to " + to + ": " + response.getBody());
    } catch (HttpClientErrorException e) {
        // Log full error response
        System.err.println("Error Response Body: " + e.getResponseBodyAsString());
        throw e;
    }
}

private String createWhatsAppPayload(String from, String to, String foodName, int quantity, String description) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    
    // Create a detailed message with separate fields
    String messageText = "New leftover food available!\n" +
                         "Food Name: " + foodName + "\n" +
                         "Quantity: " + quantity + "\n" +
                         "Description: " + description + "\n" +
                         "Hurry up and reserve it before it's gone!";
    
    // Log each individual parameter before constructing JSON
    System.out.println("Creating WhatsApp Payload with:");
    System.out.println(" - from: " + from);
    System.out.println(" - to: " + to);
    System.out.println(" - message_text: " + messageText);

    // Construct the JSON payload with content type "text"
    return mapper.writeValueAsString(new WhatsAppMessagePayload(from, to, messageText));
}

private static class WhatsAppMessagePayload {
    public String from;
    public String to;
    public String channel = "whatsapp";
    public String message_type = "text";
    public String text; // Place the message text directly here

    public WhatsAppMessagePayload(String from, String to, String text) {
        this.from = "whatsapp:" + from;  // Prefix with "whatsapp:"
        this.to = "whatsapp:" + to;      // Prefix with "whatsapp:"
        this.text = text;
    }
}


    private List<String> getStudentPhoneNumbers() {
        List<String> numbers = new ArrayList<>();
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT studentPhoneNumber FROM public.student WHERE studentPhoneNumber IS NOT NULL";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String phoneNumber = resultSet.getString("studentPhoneNumber");
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    // Adjust the phone number format
                    phoneNumber = formatPhoneNumber(phoneNumber);
                    numbers.add(phoneNumber);
                    System.out.println("Student phone number: " + phoneNumber);
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numbers;
    }
    
    private String formatPhoneNumber(String phoneNumber) {
        // Check if the phone number starts with "0" and replace it with "+60"
        if (phoneNumber.startsWith("0")) {
            return "+60" + phoneNumber.substring(1);
        }
        // If the phone number is already in the correct format, return it as is
        return phoneNumber;
    }
}
