package com.heroku.java.CONTROLLER.cafeteria_owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.student.StudentBean;
import com.heroku.java.MODEL.cafe.CafeBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class UpdateProfileCafe{
    private final DataSource dataSource;

    @Autowired
    public UpdateProfileCafe(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //process update profile cafe
    @PostMapping("/profileCafe_edit")
    public String updateProfileCafe(@ModelAttribute("profileCafe_edit") CafeBean s, HttpSession session, Model model){
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        System.out.println("CAFE Number : " + cafeNumber);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE public.cafeteria_owner SET \"cafeName\"=?, \"cafeEmail\"=? WHERE \"cafeNumber\"=?";
            final var statement = connection.prepareStatement(sql);

            String cafeName = s.getCafeName();
            String cafeEmail = s.getCafeEmail();
        
            

            
            statement.setString(1, cafeName);
            statement.setString(2, cafeEmail);
            statement.setString(3, cafeNumber);

            System.out.println("Name: " + cafeName);
            System.out.println("Email: " + cafeEmail);
            System.out.println("Cafe number: " + cafeNumber);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/profileCafe_edit?success=true";
    }

    @PostMapping("/password_cafe")
    public String updatepasswordcafe(CafeBean t, HttpSession session, Model model) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        System.out.println("CAFE : " + cafeNumber);

        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE public.cafeteria_owner SET \"cafePassword\"=? WHERE \"cafeNumber\"=?";
            final var statement = connection.prepareStatement(sql);

            String cafePassword = t.getCafePassword();

            statement.setString(1, cafePassword);
            statement.setString(2, cafeNumber);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/profileCafe_edit?success=true";
    }

}