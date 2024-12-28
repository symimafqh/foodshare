package com.heroku.java.CONTROLLER.leftover;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.leftover.LeftoverBean;
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

}
