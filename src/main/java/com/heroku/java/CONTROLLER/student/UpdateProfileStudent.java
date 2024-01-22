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
import com.heroku.java.MODEL.teacher.TeacherBean;

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
    public String updateprofilestudent(@ModelAttribute("edit_profile") StudentBean s, HttpSession session,
            Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        System.out.println("IC Number : " + studentIC);

        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE public.student SET studentname=?, studentemail=?,studentphone=?,studentdob=?,studentgender=?,studentaddress=? WHERE studentic=?";
            final var statement = connection.prepareStatement(sql);

            String studentName = s.getStudentName();
            String studentEmail = s.getStudentEmail();
            String studentPhone = s.getStudentPhone();
            String studentDOB = s.getStudentDOB();
            String studentGender = s.getStudentGender();
            String studentAddress = s.getStudentAddress();

            statement.setString(1, studentIC);
            statement.setString(2, studentName);
            statement.setString(3, studentEmail);
            statement.setString(4, studentPhone);
            statement.setString(5, studentDOB);
            statement.setString(6, studentGender);
            statement.setString(7, studentAddress);

            System.out.println("Name: " + studentName);
            System.out.println("Email: " + studentEmail);
            System.out.println("IC: " + studentIC);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/edit_profile?success=true";
    }

    @PostMapping("/edit_password")
    public String updatepasswordstudent(StudentBean s, HttpSession session, Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        System.out.println("IC Number : " + studentIC);

        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE public.student SET studentpassword=? WHERE studentic=?";
            final var statement = connection.prepareStatement(sql);

            String studentPassword = s.getStudentPassword();

            statement.setString(1, studentIC);
            statement.setString(2, studentPassword);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/edit_profile";
    }

}
