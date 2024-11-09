package com.heroku.java.CONTROLLER.leftover;

import org.checkerframework.checker.index.qual.PolySameLen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        // Retrieve the cafeNumber from the session
        String studentNumber = (String) session.getAttribute("studentNumber");
        
        List<LeftoverBean> foodList = new ArrayList<>();

        // Check if cafeNumber is null or empty
        if (studentNumber == null || studentNumber.isEmpty()) {
            return "redirect:/error"; // Redirect if no cafeNumber is available
        }

        try (Connection connection = dataSource.getConnection()) {
            // Prepare the SQL statement to fetch food items for the specific cafe
            String sql = "SELECT * FROM public.leftover";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                ResultSet resultSet = statement.executeQuery(); // Execute the query
                while (resultSet.next()) {
                    // Create a new LeftoverBean and populate it with data from the result set
                    LeftoverBean food = new LeftoverBean();
                    food.setFoodid(resultSet.getInt("foodid"));
                    food.setFoodname(resultSet.getString("foodname"));
                    food.setFoodquantity(resultSet.getInt("foodquantity"));
                    food.setFooddescription(resultSet.getString("fooddescription"));
                    food.setImagePath(resultSet.getString("image_path"));
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

    @PostMapping("/request_leftover")
    public String requestLeftover(Model model, HttpSession session, LeftoverBean leftover, @RequestParam("foodid") int foodId) {
        String studentNumber = (String) session.getAttribute("studentNumber");
        String status = "Pending";
    
        System.out.print("ni food id untuk insert" + foodId);

        // Step 1: Fetch student details using the studentNumber
        StudentBean student = getStudentDetails(studentNumber);
    
        // Check if student details were fetched successfully
        if (student == null) {
            // If not found, handle the error and return a failure page or message
            return "redirect:/error?studentNotFound=true";
        }
    
        // Step 2: Add request to the database
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO public.request (\"studentNumber\", \"foodid\", \"cafeNumber\", \"status\") VALUES (?, ?, ?, ?)";
    
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentNumber); // Use studentNumber from session
                statement.setInt(2,foodId );
                statement.setString(3, leftover.getCafeNumber());
                statement.setString(4, status);
                statement.executeUpdate();
            }
    
            // Step 3: Deduct 1 from the food quantity in the leftover table
            updateFoodQuantity(connection, leftover.getFoodid());
    
            // Step 4: Notify cafe using WhatsApp API
            notifyCafe(leftover, session);
    
            return "redirect:/dashboardCafe?success=true";
        } catch (SQLException e) {
            // Log specific SQL errors
            System.err.println("SQL Exception: " + e.getMessage());
            return "redirect:/request_leftover?error=true";
        } catch (Exception e) {
            // Log general exceptions
            System.err.println("General Exception: " + e.getMessage());
            return "redirect:/request_leftover?error=true";
        }
    }
    
    private void updateFoodQuantity(Connection connection, @RequestParam("foodid") int foodId) {
        String updateSql = "UPDATE public.leftover SET \"foodquantity\" = \"foodquantity\" - 1 WHERE foodid = ?";
    
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setInt(1, foodId); // Set the foodId for which we want to update the quantity
            statement.executeUpdate(); // Execute the update to reduce the food quantity by 1
            System.out.print("done tolak");
        } catch (SQLException e) {
            e.printStackTrace(); // Log any error that occurs during the update
        }
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

    private void notifyCafe(LeftoverBean leftover, HttpSession session) {
        // Step 1: Get list of cafe phone numbers
        List<String> cafeNumbers = getCafePhoneNumbers(leftover);
    
        // Step 2: Retrieve student details using the studentNumber from the session
        String studentNumber = (String) session.getAttribute("studentNumber"); // Get studentNumber from session
        StudentBean student = getStudentDetails(studentNumber); // Fetch student details
    
        // Check if student details were successfully retrieved
        if (student == null) {
            System.out.println("Student not found.");
            return;  // Exit the method if student details are not found
        }
    
        // Step 3: Create the message to be sent
        String messageBody = "New request by the student!\n" +
                "Student Name: " + student.getStudentName() + "\n" +  // Use studentName from StudentBean
                "Student Number: " + student.getStudentNumber() + "\n" +  // Use studentNumber from StudentBean
                "Food Name: " + leftover.getFoodname() + "\n" +
                "Hurry up and accept the request";
    
        // Step 4: Send the message to each cafe
        for (String cafeNumber : cafeNumbers) {
            try {
                // Assuming you have a WhatsAppService that handles sending messages
                String chatId = cafeNumber + "@c.us"; // Construct the chat ID for WhatsApp
                String response = whatsAppService.sendMessage(chatId, messageBody);
                System.out.println("Message sent to: " + cafeNumber);
                System.out.println("WhatsApp Response: " + response);
            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                System.out.println("Failed to send message to: " + cafeNumber);
            }
        }
    }
    
    private List<String> getCafePhoneNumbers(LeftoverBean leftover) {
        List<String> numbers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT r.\"cafeNumber\", c.phoneNumber "
                    + "FROM public.request r "
                    + "JOIN public.cafe c ON r.\"cafeNumber\" = c.\"cafeNumber\" "
                    + "WHERE r.\"cafeNumber\" = ?"; // Use '?' for parameter placeholder

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set the cafeNumber dynamically
                statement.setString(1, leftover.getCafeNumber()); // Assuming leftover.getCafeNumber() is the value you
                                                                  // want

                // Execute the query
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    String cafeNumber = rs.getString("cafeNumber");
                    String phoneNumber = rs.getString("phoneNumber");
                    // Handle the result (e.g., print, process)
                    System.out.println("Cafe Number: " + cafeNumber + ", Phone Number: " + phoneNumber);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
        }
        return numbers;
    }

}