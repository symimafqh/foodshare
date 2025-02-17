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

    // @GetMapping("/view_request")
    // public String listFoodRequest(Model model, HttpSession session) {
    //     // Retrieve the cafeNumber from the session
    //     String cafeNumber = (String) session.getAttribute("cafeNumber");
    
    //     List<FoodRequestDetail> foodRequestDetails = new ArrayList<>();
    
    //     // Check if cafeNumber is null or empty
    //     if (cafeNumber == null || cafeNumber.isEmpty()) {
    //         return "redirect:/error"; // Redirect if no cafeNumber is available
    //     }
    
    //     try (Connection connection = dataSource.getConnection()) {
    //         // Prepare the SQL statement to fetch food items along with student details for the specific cafe
    //         String sql = "SELECT l.\"foodid\", l.\"foodname\",l.\"place_to_pickup\",l.\"pickup_time\", r.\"quantityrequest\", s.\"studentName\", s.\"studentNumber\", r.\"status\" " +
    //         "FROM public.leftover l " +
    //         "JOIN public.request r ON l.\"foodid\" = r.\"foodid\" " +
    //         "JOIN public.student s ON r.\"studentNumber\" = s.\"studentNumber\" " +
    //         "WHERE l.\"cafeNumber\" = ?";

    
    //         try (PreparedStatement statement = connection.prepareStatement(sql)) {
    //             // Set the cafeNumber parameter in the query
    //             statement.setString(1, cafeNumber);
    
    //             ResultSet resultSet = statement.executeQuery(); // Execute the query
    //             while (resultSet.next()) {
    //                 // Create a new FoodRequestDetail object and populate it with data from the result set
    //                 FoodRequestDetail detail = new FoodRequestDetail();
    //                 System.out.println("TESTTTTTT");
                    
    //                 detail.setFoodid(resultSet.getInt("foodid"));
    //                 detail.setFoodname(resultSet.getString("foodname"));
    //                 detail.setPickupPlace(resultSet.getString("place_to_pickup"));
    //                 detail.setPickupTime(resultSet.getString("pickup_time"));
    //                 detail.setQuantity(resultSet.getInt("quantityrequest"));
    //                 detail.setStudentName(resultSet.getString("studentName"));
    //                 detail.setStudentNumber(resultSet.getString("studentNumber"));
    //                 detail.setStatus(resultSet.getString("status"));
    
    //                 foodRequestDetails.add(detail); // Add the combined data to the list
    //             }
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace(); // Log the exception for debugging
    //         return "redirect:/error"; // Redirect in case of an error
    //     }
    
    //     // Add the foodRequestDetails list to the model
    //     model.addAttribute("foodRequestDetails", foodRequestDetails);
    //     return "cafeteria_owner/leftover/accept_leftover"; // Return the view name to be rendered
    // }
//--------------------------------------------------------approved-------------------------------------
    @PostMapping("/accept")
    private String acceptFood(@RequestParam("foodid") int foodId, FoodRequestDetail fr) {
        String updateSql = "UPDATE public.request SET \"status\" = 'Accepted' WHERE \"foodid\" = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateSql)) {
            
            statement.setInt(1, foodId); // Set the foodId for which we want to update the status
            statement.executeUpdate(); // Execute the update
            
            System.out.println("Status updated to 'Accepted' for food ID: " + foodId);

            fr = getFoodRequestDetailById(foodId); // Ensure this method populates all fields
            if (fr != null && fr.getFoodname() != null && fr.getFoodDescription() != null) {
                notifyStudents(foodId, fr);
            } else {
                System.out.println("Failed to fetch required food details.");
            }
            
            return "cafeteria_owner/leftover/accept_leftover";
        } catch (SQLException e) {
            e.printStackTrace(); // Log any error that occurs during the update
            return "redirect:/cafeteria_owner/leftover/accept_leftover?error=update_failed";
        }
    }

    private void notifyStudents(@RequestParam("foodid") int foodId, FoodRequestDetail fr) {
        // Step 1: Get list of student phone numbers
        List<String> studentNumbers = getStudentPhoneNumbers(foodId);
        if (studentNumbers.isEmpty()) {
            System.out.println("No student phone numbers found for food ID: " + foodId);
            return;
        }
    
        // Step 2: Create the message to be sent
        String messageBody = "Your leftover has been accepted!\n" +
                             "Food Name: " + fr.getFoodname() + "\n" +
                             "Pickup Place: " + fr.getPickupPlace() + "\n" +
                             "Pickup Time: " + fr.getPickupTime() + "\n" +
                             "You can pick it up as per the description.";
    
        // Step 3: Send the message to each student
        for (String studentNumber : studentNumbers) {
            try {
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

    public FoodRequestDetail getFoodRequestDetailById(int foodId) {
        FoodRequestDetail fr = new FoodRequestDetail();
        String sql = "SELECT l.\"foodname\", l.\"place_to_pickup\", l.\"pickup_time\", l.\"cafeNumber\" " +
                     "FROM public.leftover l " +
                     "WHERE l.\"foodid\" = ?";
    
        System.out.println("Fetching food details for food ID: " + foodId);
    
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
    
            statement.setInt(1, foodId);
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String foodName = resultSet.getString("foodname");
                String pickupPlace = resultSet.getString("place_to_pickup");
                String pickupTime = resultSet.getString("pickup_time");
                String cafeNumber = resultSet.getString("cafeNumber");

                // Log the values retrieved
                System.out.println("Food Name: " + foodName);
                System.out.println("Pickup Place: " + pickupPlace);
                System.out.println("Pickup Time: " + pickupTime);
                System.out.println("Cafe Number: " + cafeNumber);

                // Check if any values are null
                if (foodName != null && pickupPlace != null && pickupTime != null && cafeNumber != null) {
                    fr.setFoodid(foodId);
                    fr.setFoodname(foodName);
                    fr.setPickupPlace(pickupPlace);
                    fr.setPickupTime(pickupTime);
                    fr.setCafeNumber(cafeNumber);
                    System.out.println("Food details retrieved for food ID: " + foodId);
                } else {
                    System.out.println("Some fields are null for food ID: " + foodId);
                }
            } else {
                System.out.println("No details found for food ID: " + foodId);
            }
        }
    } catch (SQLException e) {
        System.out.println("Error executing SQL: " + e.getMessage());
        e.printStackTrace();
    }
    return fr;
}
    

    private List<String> getStudentPhoneNumbers(int food) {
        List<String> numbers = new ArrayList<>();
        
        String sql = "SELECT DISTINCT s.\"studentphonenumber\" " +
                 "FROM public.leftover l " +
                 "JOIN public.request r ON l.\"foodid\" = r.\"foodid\" " +
                 "JOIN public.student s ON r.\"studentNumber\" = s.\"studentNumber\" " +
                 "WHERE l.\"foodid\" = ?";  // Filter by foodId

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
    
            while (resultSet.next()) {
                String phoneNumber = resultSet.getString("studentphonenumber");
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    numbers.add(phoneNumber);
                    System.out.println(phoneNumber); // Log the phone number
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
        }
        return numbers;
    }

    //------------------------------------------------rejected----------------------------
    @PostMapping("/reject")
    private String rejectFood(@RequestParam("foodid") int foodId, FoodRequestDetail fr) {
    String updateSql = "UPDATE public.request SET \"status\" = 'Rejected' WHERE \"foodid\" = ?";

    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(updateSql)) {
        
        statement.setInt(1, foodId); // Set the foodId for which we want to update the status
        statement.executeUpdate(); // Execute the update
        
        System.out.println("Status updated to 'Rejected' for food ID: " + foodId);

        fr = getFoodRequestDetailById(foodId); // Ensure this method populates all fields
        if (fr != null && fr.getFoodname() != null && fr.getPickupPlace() != null  && fr.getPickupTime() != null) {
            notifyRejection(foodId, fr);
        } else {
            System.out.println("Failed to fetch required food details.");
        }
        
        return "cafeteria_owner/leftover/accept_leftover";
    } catch (SQLException e) {
        e.printStackTrace(); // Log any error that occurs during the update
        return "redirect:/cafeteria_owner/leftover/accept_leftover?error=update_failed";
    }
}

private void notifyRejection(int foodId, FoodRequestDetail fr) {
    // Step 1: Get list of student phone numbers
    List<String> studentNumbers = getStudentPhoneNumbers(foodId);

    // Step 2: Create the rejection message to be sent
    String messageBody = "Unfortunately, your leftover request has been rejected.\n" +
            "Food Name: " + fr.getFoodname() + "\n" +
            "Pickup Place: " + fr.getPickupPlace() + "\n" +
            "Pickup Time: " + fr.getPickupTime() + "\n" +
            "Thank you for understanding.";

    // Step 3: Send the message to each student
    for (String studentNumber : studentNumbers) {
        try {
            // Assuming you have a WhatsAppService that handles sending messages
            String chatId = studentNumber + "@c.us"; // Construct the chat ID
            String response = whatsAppService.sendMessage(chatId, messageBody);
            System.out.println("Rejection message sent to: " + studentNumber);
            System.out.println("WhatsApp Response: " + response);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            System.out.println("Failed to send rejection message to: " + studentNumber);
        }
    }
}

    
}