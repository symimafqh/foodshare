package com.heroku.java.CONTROLLER.leftover;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.MODEL.leftover.LeftoverBean;

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


@Controller
public class UpdateLeftoverController {

    private final DataSource dataSource;

    @Autowired
    public UpdateLeftoverController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

@PostMapping("/updateLeftover")
public String updateLeftover(
        @ModelAttribute("updateLeftover") LeftoverBean leftover,
        @RequestParam(value = "image", required = false) MultipartFile imageFile,
        HttpSession session, Model model) {

    System.out.println("Received POST request for updating leftover.");

    String imagePath = leftover.getImagePath(); // Retain existing image path if no new image is uploaded
    try {
        // Check if a new image is provided and save it to the filesystem
        if (!imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/public/stylesheets/assets/leftover", fileName);

            // Ensure the directory exists
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, imageFile.getBytes());

            // Update imagePath for database storage
            imagePath = "/stylesheets/assets/leftover/" + fileName;
            leftover.setImagePath(imagePath); // Update LeftoverBean with the new image path
        }

        // Step 1: Update leftover in the database
        Connection connection = dataSource.getConnection();
        String sql = "UPDATE public.leftover SET \"foodname\"=?, \"foodquantity\"=?, \"image_path\"=? , \"place_to_pickup\"=? , \"pickup_time\"=? WHERE \"foodid\"=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, leftover.getFoodname());
        statement.setInt(2, leftover.getFoodquantity());
        statement.setString(3, leftover.getImagePath());
        statement.setString(4, leftover.getPickupPlace());
        statement.setString(5, leftover.getPickupTime());
        statement.setInt(5, leftover.getFoodid()); // Assuming there's an ID field to identify the leftover

        statement.executeUpdate();
        connection.close();

        System.out.println("Leftover updated with image path: " + imagePath);

        return "redirect:/foodList?success=true";

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/updateLeftover?error=true";
    }
}
}