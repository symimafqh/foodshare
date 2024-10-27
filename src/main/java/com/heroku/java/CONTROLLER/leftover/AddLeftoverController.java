package com.heroku.java.CONTROLLER.cafeteria_owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.MODEL.leftover.Leftover;

import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AddLeftoverController {
    private final DataSource dataSource;

    @Autowired
    public AddLeftoverController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Process adding a new leftover
    @PostMapping("addLeftover")
    public String addLeftover(
            @RequestParam("foodname") String foodName,
            @RequestParam("foodquantity") int foodQuantity,
            @RequestParam("fooddescription") String foodDescription,
            @RequestParam("image") MultipartFile imageFile,
            HttpSession session, Model model) {

        String cafeNumber = (String) session.getAttribute("cafeNumber");
        System.out.println("Cafe Number: " + cafeNumber);

        String imagePath = ""; // Placeholder for the image path

        try {
            // Save the image to the filesystem if not empty
            if (!imageFile.isEmpty()) {
                // Generate unique file name and save the file
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("src/main/resources/public/stylesheets/assets/leftover", fileName);

                // Ensure directory exists
                Files.createDirectories(uploadPath.getParent());
                Files.write(uploadPath, imageFile.getBytes());

                // Set the imagePath as relative path to be stored in the database
                imagePath = "/stylesheets/assets/leftover/" + fileName;
            }

            // Insert leftover data into the database
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO public.leftover (foodname, foodquantity, fooddescription, image_path, cafenumber) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, foodName);
            statement.setInt(2, foodQuantity);
            statement.setString(3, foodDescription);
            statement.setString(4, imagePath);
            statement.setString(5, cafeNumber);

            System.out.println("Food Name: " + foodName);
            System.out.println("Food Quantity: " + foodQuantity);
            System.out.println("Food Description: " + foodDescription);
            System.out.println("Image Path: " + imagePath);

            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/dashboardCafe?success=true";
    }
}
