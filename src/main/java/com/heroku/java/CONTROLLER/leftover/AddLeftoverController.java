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
package com.heroku.java.CONTROLLER.leftover;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heroku.java.MODEL.leftover.LeftoverBean;
import com.heroku.java.SERVICE.WhatsappService;

import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

//---------------------------------------------whatsapp
@Controller
public class AddLeftoverController {

    private final DataSource dataSource;
    private final WhatsappService whatsAppService; // Import your WhatsApp service

    @Autowired
    public AddLeftoverController(DataSource dataSource, WhatsappService whatsAppService) {
        this.dataSource = dataSource;
        this.whatsAppService = whatsAppService; // Inject WhatsApp service
    }

    

    @PostMapping("/addLeftover")
    public String addLeftover(
            @ModelAttribute("addLeftover") LeftoverBean leftover,
            @RequestParam("image") MultipartFile imageFile,
            HttpSession session, Model model) {
    
        String imagePath = "";
        try {
            // Cloudinary Configuration (inline setup)
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dp0ybfl6r",
                "api_key", "225921615428341",
                "api_secret", "yG49PPviB8bWBE0YaQNd-i9B5SU"
            ));
    
            // Upload the image to Cloudinary
            if (!imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                imagePath = uploadResult.get("secure_url").toString(); // Cloudinary URL
                leftover.setImagePath(imagePath); // Save the image path in the bean
            }
    
            // Set the current timestamp for created_at
            LocalDateTime createdAt = LocalDateTime.now();
    
            // Insert data into the database
            try (Connection connection = dataSource.getConnection()) {
                String sql = "INSERT INTO public.leftover (\"foodname\", \"foodquantity\", \"image_path\", \"cafeNumber\", \"created_at\", \"place_to_pickup\", \"pickup_time\", \"initial_quantity\") VALUES (?, ?, ?, ?, ?, ?,?,?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, leftover.getFoodname());
                    statement.setInt(2, leftover.getFoodquantity());
                    statement.setString(3, leftover.getImagePath());
                    String cafeNumber = (String) session.getAttribute("cafeNumber");
                    statement.setString(4, cafeNumber);
                    statement.setObject(5, createdAt);
                    statement.setObject(6, leftover.getPickupPlace());
                    statement.setObject(7, leftover.getPickupTime());
                    statement.setInt(8, leftover.getFoodquantity());
                    statement.executeUpdate();
                }
            }
    
            System.out.println("Leftover added with Cloudinary image path: " + imagePath);

            notifyStudents(leftover);
    
            return "redirect:/dashboardCafe?success=true";
    
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/addLeftover?error=true";
        }
    }
    

    private void notifyStudents(LeftoverBean leftover) {
        // Step 1: Get list of student phone numbers
        List<String> studentNumbers = getStudentPhoneNumbers();

        // Step 2: Create the message to be sent
        String messageBody = "New leftover food available!\n" +
                "Food Name: " + leftover.getFoodname() + "\n" +
                "Quantity: " + leftover.getFoodquantity() + "\n" +
                "Pickup Place: " + leftover.getPickupPlace() + "\n" +
                "Pickup Time: " + leftover.getPickupTime() + "\n" +
                "Hurry up and reserve it before it's gone!";

        // Step 3: Send the message to each student
        for (String studentNumber : studentNumbers) {
            try {
                // Assuming you have a WhatsAppService that handles sending messages
                String chatId = studentNumber + "@c.us"; // Construct the chat ID
                String response = whatsAppService.sendMessage(chatId, messageBody);
                System.out.println("Message sent to: " + studentNumber);
                System.out.println("WhatsApp Response: " + response);
            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                System.out.println("Failed to send message to: " + studentNumber);
            }
        }
    }

    private List<String> getStudentPhoneNumbers() {
        List<String> numbers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) { // Use try-with-resources
            String sql = "SELECT studentPhoneNumber FROM public.student WHERE studentPhoneNumber IS NOT NULL";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String phoneNumber = resultSet.getString("studentPhoneNumber");
                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        numbers.add(phoneNumber);
                        System.out.println(phoneNumber);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
        }
        return numbers;
    }
}

