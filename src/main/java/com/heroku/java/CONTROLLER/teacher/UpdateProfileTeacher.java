package com.heroku.java.CONTROLLER.teacher;
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
public class UpdateProfileTeacher {
    private final DataSource dataSource;

    @Autowired
    public UpdateProfileTeacher(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //process update profile teacher
    @PostMapping("/profileTeacher_edit")
    public String updateProfileTeacher(@ModelAttribute("profileTeacher_edit") TeacherBean s, HttpSession session, Model model){
        String teacherUsername = (String) session.getAttribute("teacherUsername");
        System.out.println("IC Number : " + teacherUsername);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE public.teacher SET teachername=?, teacheremail=?,teacherphone=?,teacherdob=?,teachergender=?,teacheraddress=? WHERE teacherusername=?";
            final var statement = connection.prepareStatement(sql);

            String teacherName = s.getTeacherName();
            String teacherEmail = s.getTeacherEmail();
            String teacherPhone = s.getTeacherPhone();
            String teacherDOB = s.getTeacherDOB();
            String teacherGender = s.getTeacherGender();
            String teacherAddress = s.getTeacherAddress();

            
            statement.setString(1, teacherName);
            statement.setString(2, teacherEmail);
            statement.setString(3, teacherPhone);
            statement.setString(4, teacherDOB);
            statement.setString(5, teacherGender);
            statement.setString(6, teacherAddress);
            statement.setString(7, teacherUsername);

            System.out.println("Name: " + teacherUsername);
            System.out.println("Email: " + teacherEmail);
            System.out.println("IC: " + teacherUsername);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/profileTeacher_edit?success=true";
    }
}
