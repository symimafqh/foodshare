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
        boolean unitQuotaAvailable;
        System.out.println("pass id student" + studentIC);
        try {
            Connection connection = dataSource.getConnection();
            // try
            try {
                
                // if(!unitQuotaAvailable)
                // {
                    
                //     return "redirect:/registration";
                // }
                
                
                //insert success
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

                // connection.close();

            } 


            
            catch (Exception e) {
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

                // connection.close();

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

                // connection.close();

            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/registration";
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/successregistration";
    }

//     @GetMapping("/semakpendaftaran")
// public String viewPendaftaran(HttpSession session, Model model, StudentBean sb) {
//     String studentIC = (String) session.getAttribute("studentIC");
//     // String studentName = 
//     // String
//     System.out.println("ID Number : " + studentIC);
    
//     try (Connection connection = dataSource.getConnection()) {
//         List<String> activityNames = new ArrayList<>();

//         // Query sport
//         String sql1 = "SELECT A.ACTIVITYNAME FROM REGISTRATION R "
//                 + "JOIN SPORT A ON A.ACTIVITYID = R.ACTIVITYID "
//                 + "JOIN STUDENT S ON S.STUDENTIC = R.STUDENTIC WHERE STUDENTIC=?";
//         PreparedStatement statement1 = connection.prepareStatement(sql1);
//         statement1.setString(1, studentIC);
//         ResultSet resultSet1 = statement1.executeQuery();
        
//         while (resultSet1.next()) {
//             activityNames.add(resultSet1.getString("ACTIVITYNAME"));
//         }

//         // Query unit
//         String sql2 = "SELECT A.ACTIVITYNAME FROM REGISTRATION R "
//                 + "JOIN UNIT A ON A.ACTIVITYID = R.ACTIVITYID "
//                 + "JOIN STUDENT S ON S.STUDENTIC = R.STUDENTIC WHERE STUDENTIC=?";
//         PreparedStatement statement2 = connection.prepareStatement(sql2);  // Fix variable name
//         statement2.setString(1, studentIC);
//         ResultSet resultSet2 = statement2.executeQuery();

//         while (resultSet2.next()) {
//             activityNames.add(resultSet2.getString("ACTIVITYNAME"));
//         }

//         // Query club
//         String sql3 = "SELECT A.ACTIVITYNAME FROM REGISTRATION R "
//                 + "JOIN CLUB A ON A.ACTIVITYID = R.ACTIVITYID "
//                 + "JOIN STUDENT S ON S.STUDENTIC = R.STUDENTIC WHERE STUDENTIC=?";
//         PreparedStatement statement3 = connection.prepareStatement(sql3);  // Fix variable name
//         statement3.setString(1, studentIC);
//         ResultSet resultSet3 = statement3.executeQuery();

//         while (resultSet3.next()) {
//             activityNames.add(resultSet3.getString("ACTIVITYNAME"));
//         }

//         model.addAttribute("activityNames", activityNames);
//     } catch (Exception e) {
//         e.printStackTrace();
//     }

//     return "registration/semakpendaftaran";  // Corrected return statement
// }
}
