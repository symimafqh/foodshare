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
import com.heroku.java.MODEL.teacher.CafeBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class LoginCafeteriaOwnerController {

    private final DataSource dataSource;

    @Autowired
    public LoginCafeteriaOwnerController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/teachersignin")
    public String cafeteriaSignin() {
        return "teacher/sign-in/teachersignin";
    }

    @PostMapping("/teachersignin")
    public String LoginOwner(HttpSession session,@RequestParam(name = "success", required = false) Boolean success, String cafeNumber, String cafePassword, CafeBean t, Model model) {

        try {
            // String returnPage = null;
            Connection connection = dataSource.getConnection();

            String sql = "SELECT * FROM public.cafeteria_owner WHERE \"cafeNumber\"=? AND \"cafePassword\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, cafeNumber);
            statement.setString(2, cafePassword);

            final var resultSet = statement.executeQuery();

            System.out.println("teacher ic : " + cafeNumber);
            System.out.println("teacher pass : " + cafePassword);

            if (resultSet.next()) {

                // String guestICNumber = resultSet.getString("guestICNumber");
                String username = resultSet.getString("cafeNumber");
                String password = resultSet.getString("cafePassword");
                String cafeName = resultSet.getString("cafeName");
                String cafeEmail = resultSet.getString("cafeEmail");
        

                System.out.println(username);
                // if they're admin
                // System.out.println("Email : " + guestEmail.equals(email) + " | " + email);
                // System.out.println("Password status : " + guestPassword.equals(password));

                if (username.equals(cafeNumber) && password.equals(cafePassword)) {

                    session.setAttribute("cafeNumber", cafeNumber);
                    session.setAttribute("cafePassword", cafePassword);
                    session.setAttribute("cafeName", cafeName);
                    session.setAttribute("cafeEmail", cafeEmail);
                


                    return "redirect:/dashboardTeacher?success=true" ;
                }
            }

            connection.close();
            return "redirect:/teachersignin?invalidUsername&Password";

        } catch (SQLException sqe) {
            System.out.println("Error Code = " + sqe.getErrorCode());
            System.out.println("SQL state = " + sqe.getSQLState());
            System.out.println("Message = " + sqe.getMessage());
            System.out.println("printTrace /n");
            sqe.printStackTrace();

            return "redirect:/signin?error";

        } catch (Exception e) {
            System.out.println("E message : " + e.getMessage());
            return "redirect:/signin?error";
        }
    }

    @GetMapping("/logoutteacher")
    public String logoutTeacher(HttpSession session) {
        session.invalidate();
        return "index";
    }
}
