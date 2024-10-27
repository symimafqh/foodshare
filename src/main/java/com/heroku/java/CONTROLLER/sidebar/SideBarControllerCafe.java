package com.heroku.java.CONTROLLER.sidebar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.MODEL.cafe.CafeBean;
import com.heroku.java.MODEL.leftover.Leftover;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class SideBarControllerCafe {

    private final DataSource dataSource;

    @Autowired
    public SideBarControllerCafe(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/dashboardCafe")
    public String dashboardCafe(@RequestParam(name = "success", required = false) Boolean success, Model model,
            HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");

        System.out.println("Cafe Number" + cafeNumber);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.cafeteria_owner where \"cafeNumber\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, cafeNumber);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String cafeName = resultSet.getString("cafeName");
                String cafeEmail = resultSet.getString("cafeEmail");
                String cafePassword = resultSet.getString("cafePassword");

                CafeBean t = new CafeBean();

                t.setCafeNumber(cafeNumber);
                t.setCafeName(cafeName);
                t.setCafeEmail(cafeEmail);
                t.setCafePassword(cafePassword);

                model.addAttribute("t", t);
                session.setAttribute("cafeName", cafeName);
                connection.close();
                System.out.println("Cafe Number" + cafeName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/dashboardCafe";
    }

    @GetMapping("/profileCafe_edit")
    public String editProfileCafe(@RequestParam(name = "success", required = false) Boolean success, Model model,
            HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.cafeteria_owner where \"cafeNumber\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, cafeNumber);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String cafeName = resultSet.getString("cafeName");
                String cafeEmail = resultSet.getString("cafeEmail");
                String cafePassword = resultSet.getString("cafePassword");

                CafeBean t = new CafeBean();

                t.setCafeNumber(cafeNumber);
                t.setCafeName(cafeName);
                t.setCafeEmail(cafeEmail);
                t.setCafePassword(cafePassword);

                model.addAttribute("t", t);
                session.setAttribute("cafeName", cafeName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/profileCO/profileCafe_edit";
    }

    // Add Leftover Endpoint
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
            // Save the image to the filesystem if it is not empty
            if (!imageFile.isEmpty()) {
                // Generate unique file name and save the file
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("src/main/resources/public/stylesheets/assets/leftover", fileName);

                // Ensure the directory exists
                Files.createDirectories(uploadPath.getParent());
                Files.write(uploadPath, imageFile.getBytes());

                // Set the imagePath as a relative path to be stored in the database
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
