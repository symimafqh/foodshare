package com.heroku.java.CONTROLLER.sidebar;

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
import com.heroku.java.MODEL.activity.ActivityBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;
@Controller
public class SideBarControllerCafe {

     private final DataSource dataSource;

    @Autowired
    public SideBarControllerCafe(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/dashboardCafe")
      public String dashboardCafe(@RequestParam(name = "success", required = false) Boolean success, Model model,HttpSession session) {
        String cafeNumber= (String) session.getAttribute("cafeNumber");
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.cafe where cafeusername=?";
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
                session.setAttribute("cafeName",cafeName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/dashboardOwner";
    }

    @GetMapping("/profileCafe_edit")
    public String editProfileCafe(@RequestParam(name = "success", required = false) Boolean success, Model model, HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.cafe where cafeNumber=?";
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
                session.setAttribute("cafeName",cafeName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafe/profileCafe/profileCafe_edit";
    }

}