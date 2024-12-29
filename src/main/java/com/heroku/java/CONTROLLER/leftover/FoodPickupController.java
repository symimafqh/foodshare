package com.heroku.java.CONTROLLER.leftover;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import com.heroku.java.MODEL.leftover.LeftoverBean;
import com.heroku.java.MODEL.pickup.PickupBean;
import com.heroku.java.MODEL.student.StudentBean;

import jakarta.servlet.http.HttpSession;


@Controller
public class FoodPickupController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/pickupForm")
    public String getPickupForm(HttpSession session, Model model) {
        // Retrieve the studentNumber from the session
        String studentNumber = (String) session.getAttribute("studentNumber");
        System.out.println("masuk picup form"+studentNumber);

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
            // Example: Only show accepted requests

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

        // Redirect to a "not found" page if no data is found
        return "redirect:/notFound";
    }

    @PostMapping("/pickup_post")
    public String handlePickupForm(
            @RequestParam("studentName") String studentName,
            @RequestParam("studentNumber") String studentNumber,
            @RequestParam("pickuptime") String pickupTime,
            @RequestParam("evidenceimage") MultipartFile evidenceImage,
            @RequestParam("foodname") String foodName,
            @RequestParam("foodquantity") int foodQuantity,
            @RequestParam("confirmation") String confirmation,
            HttpSession session, Model model) {
    
        // Debug log for inputs
        System.out.println("Student Name: " + studentName);
        System.out.println("Student Number: " + studentNumber);
        System.out.println("Pickup Time: " + pickupTime);
        System.out.println("Food Name: " + foodName);
        System.out.println("Food Quantity: " + foodQuantity);
        System.out.println("Confirmation: " + confirmation);
    
        String imagePath = "";
        try (Connection connection = dataSource.getConnection()) {
            // Cloudinary Configuration
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dp0ybfl6r",
                "api_key", "225921615428341",
                "api_secret", "yG49PPviB8bWBE0YaQNd-i9B5SU"
            ));
    
            // Upload the image to Cloudinary
            if (!evidenceImage.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(evidenceImage.getBytes(), ObjectUtils.emptyMap());
                imagePath = uploadResult.get("secure_url").toString(); // Cloudinary URL
            }
    
            // Fetch foodid directly based on studentNumber, status, and created_at
            String fetchFoodIdSQL = "SELECT f.foodid FROM public.request r " +
                                    "JOIN public.leftover f ON r.foodid = f.foodid " +
                                    "WHERE r.\"studentNumber\" = ? AND r.status = 'Accepted' " +
                                    "AND f.foodname = ? AND f.created_at::date = CURRENT_DATE";
    
            int foodId = 0;
            try (PreparedStatement fetchFoodIdStmt = connection.prepareStatement(fetchFoodIdSQL)) {
                fetchFoodIdStmt.setString(1, studentNumber); // Filter by studentNumber
                fetchFoodIdStmt.setString(2, foodName); // Filter by foodName
                try (ResultSet resultSet = fetchFoodIdStmt.executeQuery()) {
                    if (resultSet.next()) {
                        foodId = resultSet.getInt("foodid");
                    } else {
                        throw new SQLException("No matching food found for studentNumber: " + studentNumber);
                    }
                }
            }
    
            // SQL query to insert the pickup details into the "pickup" table
            String sql = "INSERT INTO public.pickup (timepickup, imagepath, studentnumber, foodid) " +
                         "VALUES (?, ?, ?, ?)";
            final var statement = connection.prepareStatement(sql);
    
            // Set the parameters for the prepared statement
            statement.setTimestamp(1, Timestamp.valueOf(pickupTime)); // Convert string to Timestamp
            statement.setString(2, imagePath); // Image path from Cloudinary
            statement.setString(3, studentNumber); // Student number from the form
            statement.setInt(4, foodId); // Food ID fetched from SQL query
    
            // Debug log for SQL execution
            System.out.println("Pickup Time: " + pickupTime);
            System.out.println("Image Path: " + imagePath);
            System.out.println("Student Number: " + studentNumber);
            System.out.println("Food ID: " + foodId);
    
            // Execute the SQL insert
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            return "redirect:/pickupForm?error=true"; // Redirect with error flag
        }
    
        // Redirect to success page or confirmation
        return "redirect:/pickupForm?success=true";
    }
    


}
