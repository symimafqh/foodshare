package com.heroku.java.CONTROLLER.leftover;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.heroku.java.MODEL.pickup.PickupStatusBean;
import com.heroku.java.SERVICE.WhatsappService;

import jakarta.servlet.http.HttpSession;


@Controller
public class FoodPickupViewController {

    private final DataSource dataSource;
    private final WhatsappService whatsAppService;

    @Autowired
    public FoodPickupViewController(DataSource dataSource, WhatsappService whatsAppService) {
        this.dataSource = dataSource;
        this.whatsAppService = whatsAppService; // Inject WhatsApp service
    }

    @GetMapping("/pickupStatus")
public String getPickupStatus(HttpSession session, Model model) {
    // Retrieve the cafeteria number from the session
    String cafeNumber = (String) session.getAttribute("cafeNumber");

    if (cafeNumber == null) {
        return "redirect:/login"; // Redirect if no cafeteria number is set in the session
    }

    try (Connection connection = dataSource.getConnection()) {
        String sql = """
            SELECT r."requestID", r."studentNumber", r."cafeNumber", r.status AS requestStatus, 
                   r."quantityrequest", r.request_time, 
                   CASE 
                       WHEN p.pickupid IS NOT NULL THEN 'Already Pickup' 
                       ELSE 'Did Not Pickup' 
                   END AS pickupStatus
            FROM request r
            LEFT JOIN pickup p ON r.foodid = p.foodid AND r."studentNumber" = p."studentnumber"
            WHERE r."cafeNumber" = ? -- Filter by cafeteria number from the session
            ORDER BY r.request_time DESC
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cafeNumber); // Set the cafeteria number from the session

            try (ResultSet resultSet = statement.executeQuery()) {
                List<PickupStatusBean> pickupStatusList = new ArrayList<>();

                while (resultSet.next()) {
                    PickupStatusBean status = new PickupStatusBean();
                    status.setRequestID(resultSet.getString("requestID"));
                    status.setStudentNumber(resultSet.getString("studentNumber"));
                    status.setCafeNumber(resultSet.getString("cafeNumber"));
                    status.setRequestStatus(resultSet.getString("requestStatus"));
                    status.setQuantityRequest(resultSet.getInt("quantityrequest"));
                    status.setRequestTime(resultSet.getTimestamp("request_time").toLocalDateTime());
                    status.setPickupStatus(resultSet.getString("pickupStatus"));

                    pickupStatusList.add(status);
                }

                model.addAttribute("pickupStatusList", pickupStatusList);
                return "cafeteria_owner/leftover/pickupView";
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/error";
    }
}
    
}
