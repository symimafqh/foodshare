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
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO public.student(studentic, studentname, studentemail, studentphone, studentgender, studentclass, studentaddress, studentpassword) VALUES(?,?,?,?,?,?,?,?)";
            final var statement = connection.prepareStatement(sql);

            String studIC= s.getStudentIC();
            String name= s.getStudentName();
            String email=s.getStudentEmail();
            String phone=s.getStudentPhone();
            // String dob=stud.getDob();
            String gender=s.getStudentGender();
            String kelas=s.getStudentClass();
            String address=s.getStudentAddress();
            String password =s.getStudentPassword();
    
            s.setString(1,studIC);
            s.setString(2,name);
            s.setString(3,email);
            s.setString(4,phone);
            // s.setString(5,dob);
            s.setString(5,gender);
            s.setString(6,kelas);
            s.setString(7,address);
            s.setString(8,password);
            
            statement.executeUpdate();
            
            // System.out.println("type : "+protype);
            // System.out.println("product price : RM"+proprice);
            // System.out.println("proimg: "+proimgs.getBytes());
            
            connection.close();
                
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/signup.html";
                }
            return "redirect:/sign.html";
    }
}
