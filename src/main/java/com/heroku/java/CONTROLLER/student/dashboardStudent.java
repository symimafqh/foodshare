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
public class dashboardStudent {

    private final DataSource dataSource;

    @Autowired
    public dashboardStudent(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/dashboardStudent")
    public String getStudentById(@RequestParam("studentIC") String studentIC, Model model, HttpSession session) {
		String studentic = (String) session.getAttribute("studentIC") ;
           System.out.println("Student IC : " + studentic);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.student where studentic = ?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, studentic);
            final var resultSet = statement.executeQuery();
        
            if (resultSet.next()) {
                String studentName = resultSet.getString("studentName");
                String studentEmail = resultSet.getString("studentEmail");
                String studentPhone = resultSet.getString("studentPhone");
                String studentDOB = resultSet.getString("studentDOB");
                String studentGender = resultSet.getString("studentGender");
               String studentClass = resultSet.getString("studentClass");
               String studentAddress = resultSet.getString("studentAddress");
               String studentPassword = resultSet.getString("studentPassword");
        
               StudentBean s = new StudentBean();
                s.setStudentIC(studentIC);
                s.setStudentName(studentName);
                s.setStudentEmail(studentEmail);
                s.setStudentPhone(studentPhone);
                s.setStudentDOB(studentDOB);
                s.setStudentGender(studentGender);
                s.setStudentClass(studentClass);  
                s.setStudentAddress(studentAddress);
                s.setStudentPassword(studentPassword); 
               model.addAttribute("s", s); 
  
              connection.close();
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        
          return "student/dashboardStudent";
        }
}
