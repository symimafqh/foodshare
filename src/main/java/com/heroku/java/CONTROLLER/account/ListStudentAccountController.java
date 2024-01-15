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

import com.heroku.java.MODEL.student.StudentBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class ListStudentAccountController {
    private final DataSource dataSource;

    @Autowired
    public ListStudentAccountController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/ListAccountStudent")
    public String studentAccountList(Model model) {

        List<StudentBean> student = new ArrayList<StudentBean>();
        // Retrieve the logged-in room's role from the session (syahir punya nih)
        //String staffsrole = (String) session.getAttribute("staffsrole");
        //System.out.println("staffrole managerRoomList : " + staffsrole);
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM public.student order by studentname";
            final var statement = connection.prepareStatement(sql);
            //statement.setString(1, "baker"); (syahir punya nih)
            final var resultSet = statement.executeQuery();
           

            while (resultSet.next()) {
                String studentic = resultSet.getString("studentIC");
                String studentname = resultSet.getString("studentName");
                String studentemail = resultSet.getString("studentEmail");
                String studentphone = resultSet.getString("studentPhone");
                String studentdob = resultSet.getString("studentDOB");
                String studentgender = resultSet.getString("studentGender");
                String studentclass = resultSet.getString("studentClass");
                String studentaddress = resultSet.getString("studentAddress");
                String studentpassword = resultSet.getString("studentPassword");
                
                StudentBean s = new StudentBean();
                s.setStudentIC(studentic);
                s.setStudentName(studentname);
                s.setStudentEmail(studentemail);
                s.setStudentPhone(studentphone);
                s.setStudentDOB(studentdob);
                s.setStudentGender(studentgender);
                s.setStudentClass(studentclass);  
                s.setStudentAddress(studentaddress);
                s.setStudentPassword(studentpassword);              

                student.add(s);
                model.addAttribute("students", student);
                //model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya gak)

            }

            connection.close();

        return "account/ListAccountStudent";
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as desired (e.g., show an error message)
            return "error";
        }
        
    }

}
