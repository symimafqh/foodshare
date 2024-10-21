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
import com.heroku.java.MODEL.teacher.CafeBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class UpdateProfileStudent {

    private final DataSource dataSource;

    @Autowired
    public UpdateProfileStudent(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/edit_profile")
    public String updateprofilestudent(@ModelAttribute("edit_profile") StudentBean s, HttpSession session, Model model) {
        String studentNumber = (String) session.getAttribute("studentNumber");
        System.out.println("Student Number : " + studentNumber);

        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE public.student SET studentname=?, studentemail=? WHERE studentNumber=?";
            final var statement = connection.prepareStatement(sql);

            String studentName = s.getStudentName();
            String studentEmail = s.getStudentEmail();
        

            
            statement.setString(1, studentName);
            statement.setString(2, studentEmail);
            statement.setString(7, studentNumber);

            System.out.println("Name: " + studentName);
            System.out.println("Email: " + studentEmail);
            System.out.println("Student Number: " + studentNumber);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/edit_profile?success=true";
    }

    @PostMapping("/edit_password")
    public String updatepasswordstudent(StudentBean s, HttpSession session, Model model) {
        String studentNumber = (String) session.getAttribute("studentNumber");
        System.out.println("IC Number : " + studentNumber);

        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE public.student SET studentpassword=? WHERE studentNumber=?";
            final var statement = connection.prepareStatement(sql);

            String studentPassword = s.getStudentPassword();

            statement.setString(1, studentPassword);
            statement.setString(2, studentNumber);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/edit_profile?success=true";
    }

}
