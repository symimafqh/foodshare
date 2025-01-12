package com.heroku.java.CONTROLLER.leftover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.heroku.java.MODEL.leftover.LeftoverBean;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FoodController {

    @Autowired
    private DataSource dataSource;

    // Method to list all food items
    // @GetMapping("/foodList")
    // public String listFoodItems(Model model, HttpSession session) {
    // // Retrieve the cafeNumber from the session
    // String cafeNumber = (String) session.getAttribute("cafeNumber");
    // List<LeftoverBean> foodList = new ArrayList<>();

    // // Check if cafeNumber is null or empty
    // if (cafeNumber == null || cafeNumber.isEmpty()) {
    // return "redirect:/error"; // Redirect if no cafeNumber is available
    // }

    // try (Connection connection = dataSource.getConnection()) {
    // // Prepare the SQL statement to fetch food items for the specific cafe
    // String sql = "SELECT * FROM public.leftover WHERE \"cafeNumber\" = ?";
    // try (PreparedStatement statement = connection.prepareStatement(sql)) {
    // statement.setString(1, cafeNumber); // Set the cafeNumber parameter

    // ResultSet resultSet = statement.executeQuery(); // Execute the query
    // while (resultSet.next()) {
    // // Create a new LeftoverBean and populate it with data from the result set
    // LeftoverBean food = new LeftoverBean();
    // food.setFoodid(resultSet.getInt("foodid"));
    // food.setFoodname(resultSet.getString("foodname"));
    // food.setFoodquantity(resultSet.getInt("foodquantity"));
    // food.setFooddescription(resultSet.getString("fooddescription"));
    // food.setImagePath(resultSet.getString("image_path"));
    // foodList.add(food); // Add the food item to the list
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace(); // Log the exception for debugging
    // return "redirect:/error"; // Redirect in case of an error
    // }

    // // Add the food list to the model for rendering in the view
    // model.addAttribute("foodList", foodList);
    // return "cafeteria_owner/leftover/foodList"; // Return the view name
    // }

    @GetMapping("/foodList")
    public String listFoodItems(Model model, HttpSession session) {
        // Retrieve the cafeNumber from the session
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        List<LeftoverBean> foodList = new ArrayList<>();
    
        // Check if cafeNumber is null or empty
        if (cafeNumber == null || cafeNumber.isEmpty()) {
            return "redirect:/error"; // Redirect if no cafeNumber is available
        }
    
        try (Connection connection = dataSource.getConnection()) {
            // SQL query to fetch food items for the specific cafe and today’s date
            String sql = "SELECT * FROM public.leftover WHERE \"cafeNumber\" = ? AND DATE(\"created_at\") = CURRENT_DATE";
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, cafeNumber); // Set the cafeNumber parameter
    
                ResultSet resultSet = statement.executeQuery(); // Execute the query
                while (resultSet.next()) {
                    // Create a new LeftoverBean and populate it with data from the result set
                    LeftoverBean food = new LeftoverBean();
                    food.setFoodid(resultSet.getInt("foodid"));
                    food.setFoodname(resultSet.getString("foodname"));
                    food.setFoodquantity(resultSet.getInt("foodquantity"));
                    food.setPickupPlace(resultSet.getString("place_to_pickup"));
                    food.setPickupTime(resultSet.getString("pickup_time"));
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
        return "cafeteria_owner/leftover/foodList"; // Return the view name
    }

    @GetMapping("/foodDetails")
    public String viewFoodDetails(@RequestParam("foodid") int foodID, Model model, HttpSession session) {

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM public.leftover WHERE foodid = ?"; // Correct SQL query
            final var statement = connection.prepareStatement(sql);
            statement.setInt(1, foodID);
            // Set the foodID parameter
            final var resultSet = statement.executeQuery();

            // Check if a food item was found
            if (resultSet.next()) {
                LeftoverBean food = new LeftoverBean();
                food.setFoodid(resultSet.getInt("foodid"));
                food.setFoodname(resultSet.getString("foodname"));
                food.setFoodquantity(resultSet.getInt("foodquantity"));
                food.setPickupPlace(resultSet.getString("place_to_pickup"));
                food.setPickupTime(resultSet.getString("pickup_time"));
                food.setImagePath(resultSet.getString("image_path"));

                model.addAttribute("food", food); // Add the food details to the model
                return "cafeteria_owner/leftover/update_leftover"; // Return the correct view path
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return "redirect:/error"; // Redirect on exception
        }

        return "redirect:/notFound"; // Redirect if food item was not found
    }

    @PostMapping("/deleteFood")
    public String deleteFood(@RequestParam("foodid") int foodID) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM public.leftover WHERE foodid=?";
            final var statement = connection.prepareStatement(sql);
            statement.setInt(1, foodID);

            // Log SQL execution for debugging
            System.out.println("Executing query: " + sql + " with foodid: " + foodID);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception (optional)
        }
        return "redirect:/foodList"; // Redirect back to food list
    }

}
