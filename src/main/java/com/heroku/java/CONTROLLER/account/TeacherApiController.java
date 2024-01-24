package com.heroku.java.CONTROLLER.account;
import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.heroku.java.MODEL.teacher.TeacherBean;

@Controller
public class TeacherApiController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/api/teachers/viewDetails")
    public String viewTeacherDetails(
            @RequestParam("teacherID") String teacherID,
            RedirectAttributes redirectAttributes) {

            System.out.println("Received teacherID: " + teacherID);

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM public.teacher where teacherid=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, teacherID);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                TeacherBean teacher = new TeacherBean();
                teacher.setTeacherID(teacherID);
                teacher.setTeacherUsername(resultSet.getString("teacherUsername"));
                teacher.setTeacherName(resultSet.getString("teacherName"));
                teacher.setTeacherEmail(resultSet.getString("teacherEmail"));
                teacher.setTeacherPhone(resultSet.getString("teacherPhone"));
                teacher.setTeacherDOB(resultSet.getString("teacherDOB"));
                teacher.setTeacherGender(resultSet.getString("teacherGender"));
                teacher.setTeacherRole(resultSet.getString("teacherRole"));
                teacher.setTeacherAddress(resultSet.getString("teacherAddress"));
                teacher.setTeacherPassword(resultSet.getString("teacherPassword"));

                // Add teacher object to RedirectAttributes
                redirectAttributes.addFlashAttribute("t", teacher);
                return "redirect:/account/viewTeacherDetails";
                // RedirectView redirectView = new RedirectView("/account/viewTeacherDetails");
                // redirectView.addStaticAttribute("teacherID", teacherID);
                // return new ModelAndView(redirectView);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception and redirect appropriately
            return "redirect:/error";
        }

        // If teacher is not found, redirect to an appropriate page
        return "redirect:/notFound";
    }
}