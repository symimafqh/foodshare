package com.heroku.java.CONTROLLER.student;

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

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class LoginStudentController {
    private final DataSource dataSource;

    @Autowired
    public LoginStudentController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/signin")
    public String signin() {
        return "student/sign-in/signin";
    }

    @PostMapping("/signin")
    public String LoginStudent(HttpSession session,@RequestParam(name = "success", required = false) Boolean success, String studentNumber, String studentPassword, StudentBean s, Model model) {

        try {
            // String returnPage = null;
            Connection connection = dataSource.getConnection();

            String sql = "SELECT * FROM public.student WHERE\"studentNumber\"=? AND \"studentPassword\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, studentNumber);
            statement.setString(2, studentPassword);

            final var resultSet = statement.executeQuery();

            System.out.println("student number : " + studentNumber);
            System.out.println("student pass : " + studentPassword);

            if (resultSet.next()) {

                // String guestICNumber = resultSet.getString("guestICNumber");
                // String teacherName = resultSet.getString("guestname");
                String studentNo = resultSet.getString("studentNumber");
                String password = resultSet.getString("studentPassword");
                String studentName = resultSet.getString("studentName");
                String studentEmail = resultSet.getString("studentEmail");
                
                System.out.println(studentNo);
                // if they're admin
                // System.out.println("Email : " + guestEmail.equals(email) + " | " + email);
                // System.out.println("Password status : " + guestPassword.equals(password));

                if (studentNo.equals(studentNumber) && password.equals(studentPassword)) {

                    session.setAttribute("studentNumber", studentNumber);
                    session.setAttribute("studentPassword", studentPassword);
                    session.setAttribute("studentName", studentName);
                    session.setAttribute("studentEmail", studentEmail);


                    return "redirect:/dashboardStudent?success=true" ;
                }
            }

            connection.close();
            return "redirect:/signin?invalidUsername&Password";

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

    @GetMapping("/logoutstudent")
    public String logoutStudent(HttpSession session) {
        session.invalidate();
        return "index";
    }
    
}
