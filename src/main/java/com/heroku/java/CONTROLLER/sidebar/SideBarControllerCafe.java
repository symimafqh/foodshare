package com.heroku.java.CONTROLLER.sidebar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.MODEL.cafe.CafeBean;
import com.heroku.java.MODEL.leftover.LeftoverBean;
import com.heroku.java.MODEL.student.StudentBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class SideBarControllerCafe {

    private final DataSource dataSource;

    @Autowired
    public SideBarControllerCafe(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     @GetMapping("/cafeRegisterr")
    public String showSignupPage(Model model) {
        // Create an empty StudentBean to bind form data to it
        StudentBean newStudent = new StudentBean();
        model.addAttribute("student", newStudent);

        return "cafeteria_owner/sign-in/ownerRegister"; // Redirects to the signup page template
    }

    
    

    @GetMapping("/dashboardCafe")
    public String dashboardCafe(@RequestParam(name = "success", required = false) Boolean success, Model model,
            HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");

        System.out.println("Cafe Number" + cafeNumber);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.cafeteria_owner where \"cafeNumber\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, cafeNumber);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String cafeName = resultSet.getString("cafeName");
                String cafeEmail = resultSet.getString("cafeEmail");
                String cafePassword = resultSet.getString("cafePassword");

                CafeBean t = new CafeBean();

                t.setCafeNumber(cafeNumber);
                t.setCafeName(cafeName);
                t.setCafeEmail(cafeEmail);
                t.setCafePassword(cafePassword);

                model.addAttribute("t", t);
                session.setAttribute("cafeName", cafeName);
                connection.close();
                System.out.println("Cafe Number" + cafeName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/dashboardCafe";
    }

    @GetMapping("/profileCafe_edit")
    public String editProfileCafe(@RequestParam(name = "success", required = false) Boolean success, Model model,
            HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.cafeteria_owner where \"cafeNumber\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, cafeNumber);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String cafeName = resultSet.getString("cafeName");
                String cafeEmail = resultSet.getString("cafeEmail");
                String cafePassword = resultSet.getString("cafePassword");

                CafeBean t = new CafeBean();

                t.setCafeNumber(cafeNumber);
                t.setCafeName(cafeName);
                t.setCafeEmail(cafeEmail);
                t.setCafePassword(cafePassword);

                model.addAttribute("t", t);
                session.setAttribute("cafeName", cafeName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/profileCO/profileCafe_edit";
    }

    // Get Mapping for Add Leftover Page
    @GetMapping("/add_Leftover")
    public String leftoverAdd(@RequestParam(name = "success", required = false) Boolean success, Model model,
            HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        
        try {
            if (cafeNumber != null) {
                // Create an empty Leftover bean for the form
                LeftoverBean leftover = new LeftoverBean();
                
                // Adding the Leftover bean to the model
                model.addAttribute("leftover", leftover);
                
                // Adding cafeNumber to the session to ensure we have it when the leftover is added
                model.addAttribute("cafeNumber", cafeNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/leftover/add_leftover";
    }

    // @GetMapping("/update_Leftover")
    // public String updateLeftover(@RequestParam("foodid") String id, Model model, HttpSession session) {

    //     String foodid = (String) session.getAttribute("cafeNumber");
    //     try {
    //         Connection connection = dataSource.getConnection();
    //         String sql = "SELECT * FROM public.leftover WHERE \"foodid\"=?";
    //         final var statement = connection.prepareStatement(sql);
    //         statement.setString(1, foodid);
    //         final var resultSet = statement.executeQuery();
            
    //         if (resultSet.next()) {
    //             LeftoverBean leftover = new LeftoverBean();
    //             leftover.setFoodid(resultSet.getInt("foodid"));
    //             leftover.setFoodname(resultSet.getString("foodname"));
    //             leftover.setFoodquantity(resultSet.getInt("foodquantity"));
    //             leftover.setFooddescription(resultSet.getString("fooddescription"));
    //             leftover.setImagePath(resultSet.getString("image_path"));

    //             model.addAttribute("leftover", leftover);
    //         }
    //         connection.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return "cafeteria_owner/leftover/update_leftover"; // Update to your actual view path
    // }
}
