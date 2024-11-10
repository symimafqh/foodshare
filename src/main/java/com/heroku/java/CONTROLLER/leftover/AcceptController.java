package com.heroku.java.CONTROLLER.leftover;

import org.checkerframework.checker.index.qual.PolySameLen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.heroku.java.MODEL.leftover.FoodRequestDetail;
import com.heroku.java.MODEL.leftover.LeftoverBean;
import com.heroku.java.MODEL.student.StudentBean;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import com.heroku.java.SERVICE.WhatsappService;

@Controller
public class AcceptController {

    private final DataSource dataSource;
    private final WhatsappService whatsAppService;

    @Autowired
    public AcceptController(DataSource dataSource, WhatsappService whatsAppService) {
        this.dataSource = dataSource;
        this.whatsAppService = whatsAppService; // Inject WhatsApp service
    }

    @GetMapping("/view_request")
    public String listFoodRequest(Model model, HttpSession session) {
        // Retrieve the cafeNumber from the session
        String cafeNumber = (String) session.getAttribute("cafeNumber");
    
        List<FoodRequestDetail> foodRequestDetails = new ArrayList<>();
    
        // Check if cafeNumber is null or empty
        if (cafeNumber == null || cafeNumber.isEmpty()) {
            return "redirect:/error"; // Redirect if no cafeNumber is available
        }
    
        try (Connection connection = dataSource.getConnection()) {
            // Prepare the SQL statement to fetch food items along with student details for the specific cafe
            String sql = "SELECT l.\"foodid\", l.\"foodname\", l.\"cafeNumber\", s.\"studentName\", s.\"studentNumber\", r.\"status\" " +
            "FROM public.leftover l " +
            "JOIN public.request r ON l.\"foodid\" = r.\"foodid\" " +
            "JOIN public.student s ON r.\"studentNumber\" = s.\"studentNumber\" " +
            "WHERE l.\"cafeNumber\" = ?";

    
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set the cafeNumber parameter in the query
                statement.setString(1, cafeNumber);
    
                ResultSet resultSet = statement.executeQuery(); // Execute the query
                while (resultSet.next()) {
                    // Create a new FoodRequestDetail object and populate it with data from the result set
                    FoodRequestDetail detail = new FoodRequestDetail();
                    detail.setFoodid(resultSet.getInt("foodid"));
                    detail.setFoodname(resultSet.getString("foodname"));
                    detail.setCafeNumber(resultSet.getString("cafeNumber"));
                    detail.setStudentName(resultSet.getString("studentName"));
                    detail.setStudentNumber(resultSet.getString("studentNumber"));
                    detail.setStatus(resultSet.getString("status"));
    
                    foodRequestDetails.add(detail); // Add the combined data to the list
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return "redirect:/error"; // Redirect in case of an error
        }
    
        // Add the foodRequestDetails list to the model
        model.addAttribute("foodRequestDetails", foodRequestDetails);
        return "cafeteria_owner/leftover/view_request"; // Return the view name to be rendered
    }
}