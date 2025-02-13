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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import com.heroku.java.SERVICE.WhatsappService;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

@Controller
public class RequestController {

    private final DataSource dataSource;
    private final WhatsappService whatsAppService;

    @Autowired
    public RequestController(DataSource dataSource, WhatsappService whatsAppService) {
        this.dataSource = dataSource;
        this.whatsAppService = whatsAppService; // Inject WhatsApp service
    }

    // Method to list all food items
    @GetMapping("/view_leftover")
    public String listFoodItems(Model model, HttpSession session) {
        // Retrieve the studentNumber from the session
        String studentNumber = (String) session.getAttribute("studentNumber");
        List<LeftoverBean> foodList = new ArrayList<>();

        // Check if studentNumber is null or empty
        if (studentNumber == null || studentNumber.isEmpty()) {
            return "redirect:/error"; // Redirect if no studentNumber is available
        }

        try (Connection connection = dataSource.getConnection()) {
            // SQL query to fetch food items for today (CURRENT_DATE)
            String sql = "SELECT * FROM public.leftover WHERE DATE(\"created_at\") = CURRENT_DATE";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery(); // Execute the query
                while (resultSet.next()) {
                    // Create a new LeftoverBean and populate it with data from the result set
                    LeftoverBean food = new LeftoverBean();
                    food.setFoodid(resultSet.getInt("foodid"));
                    food.setFoodname(resultSet.getString("foodname"));
                    food.setInitialQuantity(resultSet.getInt("initial_quantity"));
                    food.setFoodquantity(resultSet.getInt("foodquantity"));
                    food.setPickupPlace(resultSet.getString("place_to_pickup"));
                    food.setPickupTime(resultSet.getString("pickup_time"));

                    // Set the Cloudinary image path from the database
                    String imagePath = resultSet.getString("image_path");
                    if (imagePath != null && !imagePath.isEmpty()) {
                        food.setImagePath(imagePath); // Cloudinary URL
                    } else {
                        // Default image if no image path is provided
                        food.setImagePath("/default/image/path.jpg");
                    }

                    food.setCafeNumber(resultSet.getString("cafeNumber"));
                    System.out.println("Cafe Number: " + resultSet.getString("cafeNumber"));

                    foodList.add(food); // Add the food item to the list
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return "redirect:/error"; // Redirect in case of an error
        }

        // Add the food list to the model for rendering in the view
        model.addAttribute("foodList", foodList);
        return "cafeteria_owner/leftover/request_leftover"; // Return the view name
    }

    @PostMapping("/submit_leftover")
public String submitLeftover(
        Model model,
        HttpSession session,
        @RequestParam("foodid") int foodId,
        @RequestParam("cafenumber") String cafeNumber,
        @RequestParam("quantity") int requestedQuantity) {
    String studentNumber = (String) session.getAttribute("studentNumber");
    String status = "Pending";

    // Set the current timestamp for request_time
    LocalDateTime requestTime = LocalDateTime.now();

    try (Connection connection = dataSource.getConnection()) {
        int availableQuantity = getAvailableFoodQuantity(connection, foodId);
        if (availableQuantity < requestedQuantity) {
            // Redirect with an error message if requested quantity exceeds available quantity
            return "redirect:/dashboardStudent?error=InsufficientQuantity";
        }

        // Step 1: Add request to the database
        String sql = "INSERT INTO public.request (\"studentNumber\", \"foodid\", \"cafeNumber\", \"quantityrequest\", \"status\", \"request_time\") VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentNumber);
            statement.setInt(2, foodId);
            statement.setString(3, cafeNumber);
            statement.setInt(4, requestedQuantity);
            statement.setString(5, status);
            statement.setObject(6, requestTime); // Set LocalDateTime
            statement.executeUpdate();
        }

        // Step 2: Deduct the requested quantity from the leftover table
        updateFoodQuantity(connection, foodId, requestedQuantity);

        // Step 3: Populate the FoodRequestDetail bean
        FoodRequestDetail leftover = new FoodRequestDetail();
        leftover.setFoodid(foodId); // Set the food ID
        leftover.setCafeNumber(cafeNumber); // Set the cafe number
        leftover.setQuantity(requestedQuantity); // Set the requested quantity
        //leftover.setRequestTime(requestTime); // Save the request time to the bean

        // Step 4: Notify the cafeteria
        notifyCafe(leftover, session, cafeNumber);

        return "redirect:/view_leftover?success=true";
    } catch (SQLException e) {
        e.printStackTrace();
        return "redirect:/view_leftover?error=true";
    }
}

    
    
    // Method to get the available quantity of food
    private int getAvailableFoodQuantity(Connection connection, int foodId) throws SQLException {
        String query = "SELECT \"foodquantity\" FROM public.leftover WHERE foodid = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, foodId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("foodquantity");
                }
            }
        }
        return 0; // Default to 0 if food ID not found
    }
    
    // Update the food quantity based on the requested amount
    private void updateFoodQuantity(Connection connection, int foodId, int requestedQuantity) throws SQLException {
        String updateSql = "UPDATE public.leftover SET \"foodquantity\" = \"foodquantity\" - ? WHERE foodid = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setInt(1, requestedQuantity);
            statement.setInt(2, foodId);
            statement.executeUpdate();
        }
    }
    

    // private void updateFoodQuantity(Connection connection, @RequestParam("foodid") int foodId) {
    //     String updateSql = "UPDATE public.leftover SET \"foodquantity\" = \"foodquantity\" - 1 WHERE foodid = ?";

    //     try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
    //         statement.setInt(1, foodId); // Set the foodId for which we want to update the quantity
    //         statement.executeUpdate(); // Execute the update to reduce the food quantity by 1
    //         System.out.println("done tolak dekat quantity food");
    //     } catch (SQLException e) {
    //         e.printStackTrace(); // Log any error that occurs during the update
    //     }
    // }

    private String getFoodName(Connection connection, int foodId) {
        String foodName = null;
        String querySql = "SELECT \"foodname\" FROM public.leftover WHERE foodid = ?";

        try (PreparedStatement statement = connection.prepareStatement(querySql)) {
            statement.setInt(1, foodId); // Set the foodId parameter

            try (ResultSet resultSet = statement.executeQuery()) { // Use executeQuery for SELECT
                if (resultSet.next()) {
                    foodName = resultSet.getString("foodname"); // Retrieve the food name
                }
            }

            System.out.println("Food name retrieved: " + foodName);
        } catch (SQLException e) {
            e.printStackTrace(); // Log any SQL error that occurs
        }

        return foodName; // Return the retrieved food name
    }

    private StudentBean getStudentDetails(String studentNumber) {
        StudentBean student = null;

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT \"studentName\", \"studentEmail\" FROM public.student WHERE \"studentNumber\" = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentNumber); // Set the studentNumber from session

                // Execute the query and get results
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    // Populate the StudentBean with the data fetched from the database
                    student = new StudentBean();
                    student.setStudentNumber(studentNumber);
                    student.setStudentName(rs.getString("studentName"));
                    student.setStudentEmail(rs.getString("studentEmail"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return student;
    }

    private void notifyCafe(FoodRequestDetail leftover, HttpSession session, String cafeNumber) {
        // Print cafe number for debugging
        System.out.println(cafeNumber + "cafenumber dekat method notify");
        leftover.setCafeNumber(cafeNumber);

        // Step 1: Get list of cafe phone numbers
        List<String> cafeNumbers = getCafePhoneNumbers(cafeNumber); // Now it will get the correct cafeNumber

        // Step 2: Retrieve student details using the studentNumber from the session
        String studentNumber = (String) session.getAttribute("studentNumber"); // Get studentNumber from session
        StudentBean student = getStudentDetails(studentNumber); // Fetch student details

        // Check if student details were successfully retrieved
        if (student == null) {
            System.out.println("Student not found.");
            return; // Exit the method if student details are not found
        }

        // Step 3: Get the food name from the database
        String foodName = null;
        try (Connection connection = dataSource.getConnection()) {
            foodName = getFoodName(connection, leftover.getFoodid()); // Call the getFoodName method
        } catch (SQLException e) {
            e.printStackTrace(); // Log any SQL error
            System.out.println("Failed to fetch the food name.");
            return; // Exit the method if the food name cannot be fetched
        }

        // Step 4: Create the message to be sent
        String messageBody = "New request by the student!\n" +
                "Student Name: " + student.getStudentName() + "\n" + // Use studentName from StudentBean
                "Student Number: " + student.getStudentNumber() + "\n" + // Use studentNumber from StudentBean
                "Food Name: " + (foodName != null ? foodName : "Unknown") + "\n" + // Use the fetched food name
                "Hurry up and accept the request";

        // Step 5: Send the message to each cafe
        for (String cafeNumberr : cafeNumbers) {
            try {
                // Assuming you have a WhatsAppService that handles sending messages
                String chatId = cafeNumberr + "@c.us"; // Construct the chat ID for WhatsApp
                String response = whatsAppService.sendMessage(chatId, messageBody);
                System.out.println("Message sent to: " + cafeNumberr);
                System.out.println("WhatsApp Response: " + response);
            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                System.out.println("Failed to send message to: " + cafeNumberr);
            }
        }
    }

    private List<String> getCafePhoneNumbers(String cafeNumber) {
        List<String> numbers = new ArrayList<>();

        System.out.println("Current cafeNumber: " + cafeNumber);

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT  DISTINCT r.\"cafeNumber\", c.\"phoneNumber\" "
                    + "FROM public.request r "
                    + "JOIN public.cafeteria_owner c ON r.\"cafeNumber\" = c.\"cafeNumber\" "
                    + "WHERE r.\"cafeNumber\" = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, cafeNumber);

                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String phoneNumber = rs.getString("phoneNumber");

                    // Add '6' prefix if the phone number does not start with '6'
                    if (!phoneNumber.startsWith("6")) {
                        phoneNumber = "6" + phoneNumber;
                    }

                    numbers.add(phoneNumber); // Add phone number to the list
                    System.out.println("Cafe Number: " + rs.getString("cafeNumber") + ", Phone Number: " + phoneNumber);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
        }

        return numbers;
    }

    // private List<String> getCafePhoneNumbers(String cafeNumber) {
    // List<String> numbers = new ArrayList<>();

    // System.out.println(" current cafeNumber untuk getCafePhoneNumbers"+
    // cafeNumber);

    // try (Connection connection = dataSource.getConnection()) {
    // String sql = "SELECT r.\"cafeNumber\", c.\"phoneNumber\" "
    // + "FROM public.request r "
    // + "JOIN public.cafeteria_owner c ON r.\"cafeNumber\" = c.\"cafeNumber\" "
    // + "WHERE r.\"cafeNumber\" = ?"; // Use '?' for parameter placeholder

    // try (PreparedStatement statement = connection.prepareStatement(sql)) {
    // // Set the cafeNumber dynamically
    // statement.setString(1, cafeNumber); // Assuming leftover.getCafeNumber() is
    // the value you
    // // want

    // // Execute the query
    // ResultSet rs = statement.executeQuery();

    // while (rs.next()) {
    // String cafeNumberr = rs.getString("cafeNumber");
    // String phoneNumber = rs.getString("phoneNumber");
    // // Handle the result (e.g., print, process)
    // System.out.println("Cafe Number: " + cafeNumberr + ", Phone Number: " +
    // phoneNumber);
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace(); // Log the error
    // }
    // return numbers;
    // }

}