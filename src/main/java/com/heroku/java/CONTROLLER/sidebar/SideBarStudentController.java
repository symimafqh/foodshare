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

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class SideBarStudentController {
     private final DataSource dataSource;

      @Autowired
    public SideBarStudentController(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     @GetMapping("/dashboardStudent")
      public String index1(@RequestParam(name = "success", required = false) Boolean success, HttpSession session) {
        String studentIC = (String) session.getAttribute("studentIC");
        return "student/dashboardStudent";
    }

    @GetMapping("/edit_profile")
    public String editProfile(HttpSession session, Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        System.out.println("IC Number : " + studentIC);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT guesticnumber, guestname, guestphonenumber, guestgender, guestreligion, guestrace, guestaddress, guestemail, guestpassword FROM public.guest WHERE guesticnumber LIKE ?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, studentIC);
            final var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String password = resultSet.getString("studentPassword");
                String studentName = resultSet.getString("studentName");
                String studentEmail = resultSet.getString("studentEmail");
                String studentPhone = resultSet.getString("studentPhone");
                String studentDOB = resultSet.getString("studentDOB");
                String studentGender = resultSet.getString("studentGender");
               String studentClass = resultSet.getString("studentClass");
               String studentAddress = resultSet.getString("studentAddress");

                StudentBean s = new StudentBean();
                s.setStudentIC(studentIC);
                s.setStudentPassword(password);
                s.setStudentName(studentName);
                s.setStudentEmail(studentEmail);
                s.setStudentPhone(studentPhone);
                s.setStudentDOB(studentDOB);
                s.setStudentGender(studentGender);
                s.setStudentClass(studentClass);
                s.setStudentAddress(studentAddress);
                model.addAttribute("s", s);

                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "student/profile/edit_profile";
    }

    @GetMapping("/semasa")
    public String semasa() {
        return "semasa";    
    }

    @GetMapping("/pendaftaran")
    public String pendaftaran() {
        return "pendaftaran";
    }
    
    @GetMapping("/semakan")
    public String semakan() {
        return "semakan";
    }  
    
    @GetMapping("/info_unit")
    public String infoUnit() {
        return "info_unit";
    }

    @GetMapping("/info_kelab")
    public String infoKelab() {
        return "info_kelab";
    }
    
    @GetMapping("/info_sukan") 
    public String infoSukan() {
        return "info_sukan";
    }

}
