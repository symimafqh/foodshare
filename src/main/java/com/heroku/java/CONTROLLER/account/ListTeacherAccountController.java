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

    @GetMapping("/ListAccountTeacher")
    public String studentAccountList(Model model) {

        List<TeacherBean> teacher = new ArrayList<TeacherBean>();

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM teacher order by teacherid";
            final var statement = connection.prepareStatement(sql);
            //statement.setString(1, "baker"); (syahir punya nih)
            final var resultSet = statement.executeQuery();
           

            while (resultSet.next()) {
                String teacherID = resultSet.getString("teacherID");
                String teacherUsername = resultSet.getString("teacherUsername");
                String teacherName = resultSet.getString("teacherName");
                String teacherEmail = resultSet.getString("teacherEmail");
                String teacherPhone = resultSet.getString("teacherPhone");
                String teacherDOB = resultSet.getString("teacherDOB");
                String teacherGender = resultSet.getString("teacherGender");
               String teacherRole = resultSet.getString("teacherRole");
               String teacherAddress = resultSet.getString("teacherAddress");
               String teacherPassword = resultSet.getString("teacherPassword");
                
                TeacherBean t = new TeacherBean();
                t.setTeacherID(teacherID);
                t.setTeacherUsername(teacherUsername);
                t.setTeacherName(teacherName);
                t.setTeacherEmail(teacherEmail);
                t.setTeacherPhone(teacherPhone);
                t.setTeacherGender(teacherGender);  
                t.setTeacherRole(teacherRole);
                t.setTeacherAddress(teacherAddress);
                t.setTeacherPassword(teacherPassword); 
                t.setTeacherDOB(teacherDOB);             

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

    @GetMapping("/DeleteTeacherAccount")
    public String DeleteTeacherAccount(HttpSession session, Model model, @RequestParam("teacherID") String teacherID) {

        String teacherUsername = (String) session.getAttribute("teacherUsername");
        String teacherName = (String) session.getAttribute("teacherName");
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        String teacherPhone = (String) session.getAttribute("teacherPhone");
        String teacherDOB = (String) session.getAttribute("teacherDOB");
        String teacherGender = (String) session.getAttribute("teacherGender");
        String teacherClass = (String) session.getAttribute("teacherClass");
        String teacherAddress = (String) session.getAttribute("teacherAddress");
        String teacherPassword = (String) session.getAttribute("teacherPassword");

    try (Connection connection = dataSource.getConnection()) {
        // Delete student from the students account list
        final var DeleteTeacherAccount = connection.prepareStatement("DELETE FROM public.teacher WHERE teacherid = ?");
        DeleteTeacherAccount.setString(1, teacherID);
        DeleteTeacherAccount.executeUpdate();

        System.out.println("Student account successfully deleted");
    } catch (Exception e) {
        System.out.println("Failed to delete teacher from the teacher account list");
        e.printStackTrace();
    }
    return "redirect:/ListAccountTeacher";
}

}
