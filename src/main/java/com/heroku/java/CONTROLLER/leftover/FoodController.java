package com.heroku.java.CONTROLLER.leftover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.heroku.java.MODEL.leftover.LeftoverBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FoodController {

    @Autowired
    private DataSource dataSource;

    // Method to list all food items
    @GetMapping("/foodList")
    public String listFoodItems(Model model) {
        List<LeftoverBean> foodList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM public.leftover"; // Ensure this matches your DB
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                LeftoverBean food = new LeftoverBean();
                food.setFoodid(resultSet.getInt("foodid"));
                food.setFoodname(resultSet.getString("foodname"));
                food.setFoodquantity(resultSet.getInt("foodquantity"));
                food.setFooddescription(resultSet.getString("fooddescription"));
                food.setImagePath(resultSet.getString("image_path"));
                foodList.add(food);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error"; // Handle errors gracefully
        }
        model.addAttribute("foodList", foodList); // Add the food list to the model
        return "cafeteria_owner/leftover/foodList"; // Return the view name
    }

    @GetMapping("/foodDetails")
    public String viewFoodDetails(@RequestParam("foodid") int foodID, Model model) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM public.leftover WHERE foodid=?"; // Correct SQL query
            final var statement = connection.prepareStatement(sql);
            statement.setInt(1, foodID); // Set the foodID parameter
            final var resultSet = statement.executeQuery();
            
            // Check if a food item was found
            if (resultSet.next()) {
                LeftoverBean food = new LeftoverBean();
                food.setFoodid(resultSet.getInt("foodid"));
                food.setFoodname(resultSet.getString("foodname"));
                food.setFoodquantity(resultSet.getInt("foodquantity"));
                food.setFooddescription(resultSet.getString("fooddescription"));
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
public String deleteFood(@RequestParam("foodid") int foodID, RedirectAttributes redirectAttributes) {
    try (Connection connection = dataSource.getConnection()) {
        String sql = "DELETE FROM public.leftover WHERE foodid=?";
        final var statement = connection.prepareStatement(sql);
        statement.setInt(1, foodID);
        
        int affectedRows = statement.executeUpdate(); // Execute the delete operation
        
        if (affectedRows > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "Food item deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Food item not found.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("errorMessage", "Error occurred while deleting food item.");
    }
    
    return "redirect:/foodList"; // Redirect to the food list after deletion
}

}
