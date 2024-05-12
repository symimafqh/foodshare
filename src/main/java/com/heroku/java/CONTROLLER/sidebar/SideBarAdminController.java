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

import com.heroku.java.MODEL.teacher.TeacherBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class SideBarAdminController {
   private final DataSource dataSource;

    @Autowired
    public SideBarAdminController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/dashboardAdmin")
    public String dashboardAdmin(@RequestParam(name = "success", required = false) Boolean success, Model model, HttpSession session) {
    //   String teacherUsername = (String) session.getAttribute("teacherUsername");
    String teacherUsername = (String) session.getAttribute("teacherUsername");
    try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT * FROM public.teacher where teacherusername=?";
        final var statement = connection.prepareStatement(sql);
        statement.setString(1, teacherUsername);
        final var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String teacherName = resultSet.getString("teacherName");
            String teacherEmail = resultSet.getString("teacherEmail");
            String teacherPhone = resultSet.getString("teacherPhone");
            String teacherDOB = resultSet.getString("teacherDOB");
            String teacherGender = resultSet.getString("teacherGender");
            String teacherRole= resultSet.getString("teacherRole");
            String teacherAddress = resultSet.getString("teacherAddress");
            String teacherPassword = resultSet.getString("teacherPassword");

            TeacherBean t = new TeacherBean();

            t.setTeacherUsername(teacherUsername);
            t.setTeacherName(teacherName);
            t.setTeacherEmail(teacherEmail);
            t.setTeacherPhone(teacherPhone);
            t.setTeacherDOB(teacherDOB);
            t.setTeacherGender(teacherGender);
            t.setTeacherRole(teacherRole);
            t.setTeacherAddress(teacherAddress);
            t.setTeacherPassword(teacherPassword);

            model.addAttribute("t", t);

            connection.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
      return "teacher/dashboardAdmin";
  }

  @GetMapping("/profileAdmin")
  public String editProfileAdmin(@RequestParam(name = "success", required = false) Boolean success, Model model, HttpSession session) {
      String teacherUsername = (String) session.getAttribute("teacherUsername");
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.teacher where teacherusername=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, teacherUsername);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String teacherName = resultSet.getString("teacherName");
                String teacherEmail = resultSet.getString("teacherEmail");
                String teacherPhone = resultSet.getString("teacherPhone");
                String teacherDOB = resultSet.getString("teacherDOB");
                String teacherGender = resultSet.getString("teacherGender");
                String teacherRole= resultSet.getString("teacherRole");
                String teacherAddress = resultSet.getString("teacherAddress");
                String teacherPassword = resultSet.getString("teacherPassword");

                TeacherBean t = new TeacherBean();

                t.setTeacherUsername(teacherUsername);
                t.setTeacherName(teacherName);
                t.setTeacherEmail(teacherEmail);
                t.setTeacherPhone(teacherPhone);
                t.setTeacherDOB(teacherDOB);
                t.setTeacherGender(teacherGender);
                t.setTeacherRole(teacherRole);
                t.setTeacherAddress(teacherAddress);
                t.setTeacherPassword(teacherPassword);

                model.addAttribute("t", t);

                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "teacher/profileTeacher/profileAdmin";
      
  }

  

}
