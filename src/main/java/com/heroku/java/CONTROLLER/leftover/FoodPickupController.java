package com.heroku.java.CONTROLLER.leftover;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.heroku.java.MODEL.leftover.FoodRequestDetail;
import com.heroku.java.MODEL.leftover.LeftoverBean;
import com.heroku.java.MODEL.pickup.PickupBean;
import com.heroku.java.MODEL.student.StudentBean;
import com.heroku.java.SERVICE.WhatsappService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FoodPickupController {

    @Autowired
    private DataSource dataSource;

    private final WhatsappService whatsAppService;

    @Autowired
    public FoodPickupController(DataSource dataSource, WhatsappService whatsAppService) {
        this.dataSource = dataSource;
        this.whatsAppService = whatsAppService;
    }

    @GetMapping("/pickupForm")
    public String getPickupForm(HttpSession session, Model model) {
        // Retrieve the studentNumber from the session
        String studentNumber = (String) session.getAttribute("studentNumber");
        System.out.println("masuk picup form" + studentNumber);
    
        if (studentNumber == null) {
            // Redirect to login or error page if studentNumber is not in session
            return "redirect:/login";
        }
    
        try (Connection connection = dataSource.getConnection()) {
            // SQL query to join requests, student, and leftover tables
            String sql = "SELECT r.\"studentNumber\", s.\"studentName\", f.foodname, f.foodquantity " +
                    "FROM public.request r " +
                    "JOIN public.student s ON r.\"studentNumber\" = s.\"studentNumber\" " +
                    "JOIN public.leftover f ON r.foodid = f.foodid " +
                    "WHERE r.\"studentNumber\" = ? AND r.status = 'Accepted' " +
                    "AND f.created_at::date = CURRENT_DATE";
    
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentNumber); // Set the studentNumber parameter
    
                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("dah masuk untuk display");
                    // Check if records are found
                    if (resultSet.next()) {
                        // Create beans to hold the fetched data
                        StudentBean student = new StudentBean();
                        student.setStudentNumber(resultSet.getString("studentnumber"));
                        student.setStudentName(resultSet.getString("studentname"));
    
                        LeftoverBean leftover = new LeftoverBean();
                        leftover.setFoodname(resultSet.getString("foodname"));
                        leftover.setFoodquantity(resultSet.getInt("foodquantity"));
    
                        // Add the data to the model
                        model.addAttribute("s", student);
                        model.addAttribute("leftover", leftover);
    
                        // Return the view name for the pickup form
                        return "student/pickup/pickup_status"; // Match this with your Thymeleaf template name
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return "redirect:/error"; // Redirect to an error page if an exception occurs
        }
    
        // No requests found; add an attribute to trigger the popup
        model.addAttribute("noRequestMessage", "You have not making any request");
        System.out.println("tak de request so pergi dashboard");
        return "student/dashboardStudent";
    }
    // Method to handle the food pickup and notify the cafeteria
    @PostMapping("/pickup_post")
    public String handlePickupForm(
            @RequestParam("studentName") String studentName,
            @RequestParam("studentNumber") String studentNumber,
            @RequestParam("pickuptime") String pickupTime,
            @RequestParam("evidenceimage") MultipartFile evidenceImage,
            @RequestParam("foodname") String foodName,
            @RequestParam("foodquantity") int foodQuantity,
            HttpSession session, Model model) {

        String imagePath = "";

        try (Connection connection = dataSource.getConnection()) {
            // Cloudinary Configuration
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "dp0ybfl6r",
                    "api_key", "225921615428341",
                    "api_secret", "yG49PPviB8bWBE0YaQNd-i9B5SU"));

            // Upload the image to Cloudinary
            if (!evidenceImage.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(evidenceImage.getBytes(), ObjectUtils.emptyMap());
                imagePath = uploadResult.get("secure_url").toString(); // Cloudinary URL
            }

            // Convert pickupTime to Timestamp
            String formattedTime = pickupTime.replace("T", " ") + ":00";
            Timestamp timestamp = Timestamp.valueOf(formattedTime);

            // Fetch foodid based on studentNumber and foodName
            String fetchFoodIdSQL = "SELECT f.foodid FROM public.request r " +
                    "JOIN public.leftover f ON r.foodid = f.foodid " +
                    "WHERE r.\"studentNumber\" = ? AND r.status = 'Accepted' " +
                    "AND f.foodname = ? AND f.created_at::date = CURRENT_DATE";

            int foodId = 0;
            try (PreparedStatement fetchFoodIdStmt = connection.prepareStatement(fetchFoodIdSQL)) {
                fetchFoodIdStmt.setString(1, studentNumber);
                fetchFoodIdStmt.setString(2, foodName);
                try (ResultSet resultSet = fetchFoodIdStmt.executeQuery()) {
                    if (resultSet.next()) {
                        foodId = resultSet.getInt("foodid");
                    } else {
                        throw new SQLException("No matching food found for studentNumber: " + studentNumber);
                    }
                }
            }

            // Insert into the pickup table
            String sql = "INSERT INTO public.pickup (\"timepickup\", \"imagepath\", \"studentnumber\", \"foodid\") " +
                    "VALUES (?, ?, ?, ?)";
            final var statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, timestamp);
            statement.setString(2, imagePath);
            statement.setString(3, studentNumber);
            statement.setInt(4, foodId);
            statement.executeUpdate();

            // Step 1: Get the cafeteria number based on the foodId
            String cafeNumber = getCafeNumberByFoodId(foodId);

            if (cafeNumber != null) {
                // Step 2: Notify the cafeteria
                notifyCafe(foodId, studentNumber, cafeNumber, session);
            } else {
                System.out.println("No cafeteria found for this food item.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            return "redirect:/pickupForm?error=true";
        }

        return "redirect:/pickupForm?success=true";
    }

    // Retrieve cafeteria number by foodId
    private String getCafeNumberByFoodId(int foodId) {
        String cafeNumber = null;
    
        // SQL query to get the cafeNumber based on foodId
        String querySql = "SELECT r.\"cafeNumber\" " +
                "FROM public.request r " +
                "JOIN public.leftover f ON r.foodid = f.foodid " +
                "WHERE f.foodid = ?";
    
        try (Connection connection = dataSource.getConnection()) {
            // Execute the query
            try (PreparedStatement statement = connection.prepareStatement(querySql)) {
                statement.setInt(1, foodId); // Set the foodId parameter
    
                try (ResultSet resultSet = statement.executeQuery()) {
                    // If result is found, extract cafeNumber
                    if (resultSet.next()) {
                        cafeNumber = resultSet.getString("cafeNumber");
    
                        // Check if the cafe number doesn't start with '6'
                        if (cafeNumber != null && !cafeNumber.startsWith("6")) {
                            // Prepend '6' if it doesn't exist
                            cafeNumber = "6" + cafeNumber;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log SQL errors
        }
    
        return cafeNumber; // Return the modified cafeNumber (could be null if not found)
    }
    

    // Notify the cafeteria using WhatsApp
    private void notifyCafe(int foodId, String studentNumber, String cafeNumber, HttpSession session) {
        // Step 1: Retrieve student details
        StudentBean student = getStudentDetails(studentNumber);

        if (student == null) {
            System.out.println("Student not found.");
            return; // Exit if student details are not available
        }

        // Step 2: Get food name by foodId
        String foodName = getFoodNameByFoodId(foodId);

        // Step 3: Create the notification message
        String messageBody = "A student has picked up food!\n" +
                "Student Name: " + student.getStudentName() + "\n" +
                "Student Number: " + student.getStudentNumber() + "\n" +
                "Food Name: " + foodName + "\n" +
                "Food has been picked up. Please confirm.";

        // Step 4: Send notification using WhatsApp service
        try {
            String chatId = cafeNumber + "@c.us";
            whatsAppService.sendMessage(chatId, messageBody);
            System.out.println("Message sent to: " + cafeNumber);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send message to: " + cafeNumber);
        }
    }

    // Retrieve student details by student number
    private StudentBean getStudentDetails(String studentNumber) {
        StudentBean student = new StudentBean();
        String querySql = "SELECT \"studentName\", \"studentEmail\" FROM public.student WHERE \"studentNumber\" = ?";

        System.out.println("masuk get student details");

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(querySql)) {
                statement.setString(1, studentNumber);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        student = new StudentBean();
                        student.setStudentNumber(studentNumber);
                        student.setStudentName(resultSet.getString("studentName"));
                        student.setStudentEmail(resultSet.getString("studentEmail"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log SQL errors
        }

        return student;
    }

    // Retrieve the food name based on foodId
    // Retrieve the food name based on foodId
    private String getFoodNameByFoodId(int foodId) {
        String foodName = null;
        String querySql = "SELECT foodname FROM public.leftover WHERE foodid = ?";

        System.out.println("masuk get food Name ");

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(querySql)) {
                statement.setInt(1, foodId); // Set the foodId parameter

                try (ResultSet resultSet = statement.executeQuery()) {
                    // If result is found, extract food name
                    if (resultSet.next()) {
                        foodName = resultSet.getString("foodname");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log SQL errors
        }

        return foodName; // Return the food name (could be null if not found)
    }
}
