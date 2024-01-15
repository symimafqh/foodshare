package com.heroku.java.CONTROLLER.account;
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

import com.heroku.java.MODEL.teacher.TeacherBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class ListTeacherAccountController {
    private final DataSource dataSource;

    @Autowired
    public ListTeacherAccountController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/ListAccountStudent")
    public String studentAccountList(Model model) {

        List<TeacherBean> teacher = new ArrayList<TeacherBean>();

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM teacher order by teacherid";
            final var statement = connection.prepareStatement(sql);
            //statement.setString(1, "baker"); (syahir punya nih)
            final var resultSet = statement.executeQuery();
           

            while (resultSet.next()) {
                String teacherid = resultSet.getString("teacherID");
                String teacherusername = resultSet.getString("teacherUsername");
                String teachername = resultSet.getString("teacherName");
                String teacheremail = resultSet.getString("teacherEmail");
                String teacherphone = resultSet.getString("teacherPhone");
                String teacherdob = resultSet.getString("teacherDOB");
                String teachergender = resultSet.getString("teacherGender");
               String teacherrole = resultSet.getString("teacherRole");
               String teacheraddress = resultSet.getString("teacherAddress");
               String teacherpassword = resultSet.getString("teacherPassword");
                
                TeacherBean t = new TeacherBean();
                t.setTeacherID(teacherid);
                t.setTeacherUsername(teacherusername);
                t.setTeacherName(teachername);
                t.setTeacherEmail(teacheremail);
                t.setTeacherPhone(teacherphone);
                t.setTeacherDOB(teacherdob);
                t.setTeacherGender(teachergender);  
                t.setTeacherRole(teacherrole);
                t.setTeacherAddress(teacheraddress);
                t.setTeacherPassword(teacherpassword);              

                teacher.add(t);
                model.addAttribute("teacher", teacher);
                //model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya gak)

            }

            connection.close();

        return "account/ListAccountTeacher";
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as desired (e.g., show an error message)
            return "error";
        }
        
    }

}
