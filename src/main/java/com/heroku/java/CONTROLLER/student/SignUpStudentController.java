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
public class SignUpStudentController {
    private final DataSource dataSource;

    @Autowired
    public SignUpStudentController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/signup")
    public String registerStudent(@ModelAttribute("signup")StudentBean s){
        try( Connection connection = dataSource.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM public.student WHERE studentic = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, s.getStudentIC());
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // StudentIC already exists
                    return "redirect:/signup?NomborICpernah didaftarkan";
                }
            }
        }
            String sql = "INSERT INTO public.student(studentic, studentname, studentemail, studentphone, studentdob, studentgender, studentclass, studentaddress, studentpassword) VALUES(?,?,?,?,?,?,?,?,?)";
            try (final var statement = connection.prepareStatement(sql)){;

            String studIC= s.getStudentIC();
            String name= s.getStudentName();
            String email=s.getStudentEmail();
            String phone=s.getStudentPhone();
            String dob=s.getStudentDOB();
            String gender=s.getStudentGender();
            String kelas=s.getStudentClass();
            String address=s.getStudentAddress();
            String password =s.getStudentPassword();
    
            statement.setString(1,studIC);
            statement.setString(2,name);
            statement.setString(3,email);
            statement.setString(4,phone);
            statement.setString(5,dob);
            statement.setString(6,gender);
            statement.setString(7,kelas);
            statement.setString(8,address);
            statement.setString(9,password);
            
            statement.executeUpdate();
            
            }
            connection.close();
                
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/signup?";
                }
            return "redirect:/signin";
    }
}
