package com.heroku.java.CONTROLLER.registration;

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

import com.heroku.java.MODEL.registration.RegistrationBean;
import com.heroku.java.MODEL.student.StudentBean;
import com.heroku.java.MODEL.teacher.TeacherBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class registrationNewController {

    private final DataSource dataSource;

    @Autowired
    public registrationNewController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/registration")
    public String registrationCocu(HttpSession session, @ModelAttribute("registration") RegistrationBean r,
            Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        System.out.println("pass id student" + studentIC);

      
        try {
            Connection connection = dataSource.getConnection();
            boolean registeractivity = isStudentRegistered(studentIC, connection);
            if (registeractivity) {
                // try
                try {

                    String sql = "INSERT INTO registration(studentic, activityid) VALUES (?,?)";
                    final var statement = connection.prepareStatement(sql);

                    // studentIC = r.getStudentIC();
                    int unit = r.getUnitReg();

                    statement.setString(1, studentIC);
                    statement.setInt(2, unit);

                    statement.executeUpdate();

                    System.out.println("successfully inserted");
                    // System.out.println("product price : RM"+proprice);
                    // System.out.println("proimg: "+proimgs.getBytes());

                    connection.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/registration";
                }

                // club
                try {

                    String sql = "INSERT INTO registration(studentic, activityid) VALUES (?,?)";
                    final var statement = connection.prepareStatement(sql);

                    // studentIC = r.getStudentIC();
                    int club = r.getClubReg();

                    statement.setString(1, studentIC);
                    statement.setInt(2, club);

                    statement.executeUpdate();

                    System.out.println("successfully inserted");
                    // System.out.println("product price : RM"+proprice);
                    // System.out.println("proimg: "+proimgs.getBytes());

                    connection.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/registration";
                }

                // sport
                try {
                    String sql = "INSERT INTO registration(studentic, activityid) VALUES (?,?)";
                    final var statement = connection.prepareStatement(sql);

                    // studentIC = r.getStudentIC();
                    int sport = r.getSportReg();

                    statement.setString(1, studentIC);
                    statement.setInt(2, sport);

                    statement.executeUpdate();

                    System.out.println("successfully inserted");
                    // System.out.println("product price : RM"+proprice);
                    // System.out.println("proimg: "+proimgs.getBytes());

                    connection.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/registration";
                }

            } else {
                System.out.println("gagal insert into table reservationservice sebab dah register");
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("failed to insert into reservationservice");
            e.printStackTrace();
            return "redirect:/registered";
        }
        return "redirect:/successregistration";
    }

    // method to check dah register ke belum
    public static boolean isStudentRegistered(String studentIC, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registration WHERE studentic = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentIC);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int existingReservationsCount = resultSet.getInt(1);
                    return existingReservationsCount == 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("fail at method isStudentRegistered()");
            e.printStackTrace();
        }
        return false;
    }

}
