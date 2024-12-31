package com.heroku.java.CONTROLLER.history_view;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.heroku.java.MODEL.leftover.HistoryBean;

import jakarta.servlet.http.HttpSession;

@Controller
public class HistoryViewController {

    @Autowired
    private DataSource dataSource;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/requestHistory")
    public String getRequestHistory(HttpSession session, Model model) {
        // Retrieve the studentNumber from the session
        String studentNumber = (String) session.getAttribute("studentNumber");
        System.out.println("Fetching request history for student: " + studentNumber);

        if (studentNumber == null) {
            // Redirect to login or error page if studentNumber is not in session
            return "redirect:/login";
        }

        try (Connection connection = dataSource.getConnection()) {
            // SQL query to join request, leftover, and pickup tables
            String sql = """
                SELECT 
                    l.foodname,
                    r.request_time,
                    r.status,
                    CASE
                        WHEN p.studentnumber IS NOT NULL THEN 'Picked Up'
                        ELSE 'Pending Pickup'
                    END AS pickup_status
                FROM request r
                JOIN leftover l ON r.foodid = l.foodid
                LEFT JOIN pickup p ON r.foodid = p.foodid
                WHERE r."studentNumber" = ?
                ORDER BY r.request_time DESC
            """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentNumber); // Set the studentNumber parameter

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<HistoryBean> historyList = new ArrayList<>();

                    // Process each row of the result set
                    while (resultSet.next()) {
                        HistoryBean history = new HistoryBean();
                        history.setFoodName(resultSet.getString("foodname"));
                        LocalDateTime requestTime = resultSet.getTimestamp("request_time").toLocalDateTime();
                        history.setRequestTime(requestTime);
                        history.setStatus(resultSet.getString("status"));
                        history.setPickupStatus(resultSet.getString("pickup_status"));

                        // Convert LocalDateTime to formatted String and set it
                        if (requestTime != null) {
                            String formattedTime = requestTime.format(formatter);
                            history.setFormattedRequestTime(formattedTime); // This will now work
                        }
                        

                        historyList.add(history);
                    }

                    // Add the history list to the model
                    model.addAttribute("historyList", historyList);

                    // Return the view name for the request history page
                    return "student/pickup/viewHistoryRequest"; // Match this with your Thymeleaf template name
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return "redirect:/error"; // Redirect to an error page if an exception occurs
        }
    }
}
