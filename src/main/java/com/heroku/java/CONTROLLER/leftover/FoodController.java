package com.heroku.java.CONTROLLER.leftover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.heroku.java.MODEL.leftover.LeftoverBean;

import javax.sql.DataSource;
import java.sql.Connection;

@Controller
public class FoodController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/foodlist")
    public String viewFoodDetails(
            @RequestParam("foodid") String foodID,
            RedirectAttributes redirectAttributes) {

        System.out.println("Received foodID: " + foodID);

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM public.leftover WHERE \"foodid\"=?"; // Update with your actual food table name
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, foodID);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                LeftoverBean food = new LeftoverBean(); // Use your existing bean class
                food.setFoodid(resultSet.getString("foodid"));
                food.setFoodname(resultSet.getString("foodname"));
                food.setFoodquantity(resultSet.getInt("foodquantity"));
                food.setFooddescription(resultSet.getString("fooddescription"));
                food.setImagePath(resultSet.getString("image_path"));

                // Add food object to RedirectAttributes
                redirectAttributes.addFlashAttribute("food", food);
                return "redirect:/leftover/update_leftover"; // Redirect to the view page
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception and redirect appropriately
            return "redirect:/error"; // Redirect to an error page
        }

        // If food is not found, redirect to an appropriate page
        return "redirect:/notFound"; // Redirect to a not found page
    }
}
