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
import com.heroku.java.CONTROLLER.sidebar.*;

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

        try (Connection connection = dataSource.getConnection()) {
            // Uniform registration
            String uniformQuotaSql = "SELECT uniformquota FROM uniform WHERE activityid = ?";
            try (PreparedStatement uniformQuotaStmt = connection.prepareStatement(uniformQuotaSql)) {
                uniformQuotaStmt.setInt(1, r.getUnitReg());
                try (ResultSet uniformQuotaRs = uniformQuotaStmt.executeQuery()) {
                    int uniformQuota = 0;
                    if (uniformQuotaRs.next()) {
                        uniformQuota = uniformQuotaRs.getInt("uniformquota");
                    }
                    if (uniformQuota > 0) {
                        String uniformInsertSql = "INSERT INTO registration(studentic, activityid) VALUES (?, ?)";
                        try (PreparedStatement uniformInsertStmt = connection.prepareStatement(uniformInsertSql)) {
                            uniformInsertStmt.setString(1, studentIC);
                            uniformInsertStmt.setInt(2, r.getUnitReg());
                            uniformInsertStmt.executeUpdate();
                        }
                        int newUniformQuota = uniformQuota - 1;
                        String updateUniformQuotaSql = "UPDATE uniform SET uniformquota = ? WHERE activityid = ?";
                        try (PreparedStatement updateUniformQuotaStmt = connection.prepareStatement(updateUniformQuotaSql)) {
                            updateUniformQuotaStmt.setInt(1, newUniformQuota);
                            updateUniformQuotaStmt.setInt(2, r.getUnitReg());
                            updateUniformQuotaStmt.executeUpdate();
                        }
                        System.out.println("Successfully inserted in uniform and quota updated.");
                    } else {
                        System.out.println("Uniform quota reached. Registration not allowed.");
                        return "redirect:/registerQuota";
                    }
                }
            }

            // Club registration
            String clubQuotaSql = "SELECT clubquota FROM club WHERE activityid = ?";
            try (PreparedStatement clubQuotaStmt = connection.prepareStatement(clubQuotaSql)) {
                clubQuotaStmt.setInt(1, r.getClubReg());
                try (ResultSet clubQuotaRs = clubQuotaStmt.executeQuery()) {
                    int clubQuota = 0;
                    if (clubQuotaRs.next()) {
                        clubQuota = clubQuotaRs.getInt("clubquota");
                    }
                    if (clubQuota > 0) {
                        String clubInsertSql = "INSERT INTO registration(studentic, activityid) VALUES (?, ?)";
                        try (PreparedStatement clubInsertStmt = connection.prepareStatement(clubInsertSql)) {
                            clubInsertStmt.setString(1, studentIC);
                            clubInsertStmt.setInt(2, r.getClubReg());
                            clubInsertStmt.executeUpdate();
                        }
                        int newClubQuota = clubQuota - 1;
                        String updateClubQuotaSql = "UPDATE club SET clubquota = ? WHERE activityid = ?";
                        try (PreparedStatement updateClubQuotaStmt = connection.prepareStatement(updateClubQuotaSql)) {
                            updateClubQuotaStmt.setInt(1, newClubQuota);
                            updateClubQuotaStmt.setInt(2, r.getClubReg());
                            updateClubQuotaStmt.executeUpdate();
                        }
                        System.out.println("Successfully inserted in club and quota updated.");
                    } else {
                        System.out.println("Club quota reached. Registration not allowed.");
                        return "redirect:/registerQuota";
                    }
                }
            }

            // Sport registration
            String sportQuotaSql = "SELECT sportquota FROM sport WHERE activityid = ?";
            try (PreparedStatement sportQuotaStmt = connection.prepareStatement(sportQuotaSql)) {
                sportQuotaStmt.setInt(1, r.getSportReg());
                try (ResultSet sportQuotaRs = sportQuotaStmt.executeQuery()) {
                    int sportQuota = 0;
                    if (sportQuotaRs.next()) {
                        sportQuota = sportQuotaRs.getInt("sportquota");
                    }
                    if (sportQuota > 0) {
                        String sportInsertSql = "INSERT INTO registration(studentic, activityid) VALUES (?, ?)";
                        try (PreparedStatement sportInsertStmt = connection.prepareStatement(sportInsertSql)) {
                            sportInsertStmt.setString(1, studentIC);
                            sportInsertStmt.setInt(2, r.getSportReg());
                            sportInsertStmt.executeUpdate();
                        }
                        int newSportQuota = sportQuota - 1;
                        String updateSportQuotaSql = "UPDATE sport SET sportquota = ? WHERE activityid = ?";
                        try (PreparedStatement updateSportQuotaStmt = connection.prepareStatement(updateSportQuotaSql)) {
                            updateSportQuotaStmt.setInt(1, newSportQuota);
                            updateSportQuotaStmt.setInt(2, r.getSportReg());
                            updateSportQuotaStmt.executeUpdate();
                        }
                        System.out.println("Successfully inserted in sport and quota updated.");
                    } else {
                        System.out.println("Sport quota reached. Registration not allowed.");
                        return "redirect:/registerQuota";
                    }
                }
            }

            return "redirect:/successregistration"; // Successful registration for all

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/registration";
        }
    }
}
// @Controller
// public class registrationNewController {

//     private final DataSource dataSource;

//     @Autowired
//     public registrationNewController(DataSource dataSource) {
//         this.dataSource = dataSource;
//     }

//     @PostMapping("/registration")
//     public String registrationCocu(HttpSession session, @ModelAttribute("registration") RegistrationBean r,
//             Model model) {
//         String studentIC = (String) session.getAttribute("studentIC");
//         boolean unitQuotaAvailable;
//         System.out.println("pass id student" + studentIC);
//         try {
//             Connection connection = dataSource.getConnection();
//             // try
//             try {
                
//                 //insert success
//                 String sql = "INSERT INTO registration(studentic, activityid) VALUES (?,?)";
//                 final var statement = connection.prepareStatement(sql);

//                 // studentIC = r.getStudentIC();
//                 int unit = r.getUnitReg();

//                 statement.setString(1, studentIC);
//                 statement.setInt(2, unit);

//                 statement.executeUpdate();

//                 System.out.println("successfully inserted");
//                 // System.out.println("product price : RM"+proprice);
//                 // System.out.println("proimg: "+proimgs.getBytes());

//                 // connection.close();

//             } 
//             catch (Exception e) {
//                 e.printStackTrace();
//                 return "redirect:/registration";
//             }
            
//             // club
//             try {

//                 String sql = "INSERT INTO registration(studentic, activityid) VALUES (?,?)";
//                 final var statement = connection.prepareStatement(sql);

//                 // studentIC = r.getStudentIC();
//                 int club = r.getClubReg();

//                 statement.setString(1, studentIC);
//                 statement.setInt(2, club);

//                 statement.executeUpdate();

//                 System.out.println("successfully inserted");
//                 // System.out.println("product price : RM"+proprice);
//                 // System.out.println("proimg: "+proimgs.getBytes());

//                 // connection.close();

//             } catch (Exception e) {
//                 e.printStackTrace();
//                 return "redirect:/registration";
//             }

//             // sport
//             try {
//                 String sql = "INSERT INTO registration(studentic, activityid) VALUES (?,?)";
//                 final var statement = connection.prepareStatement(sql);

//                 // studentIC = r.getStudentIC();
//                 int sport = r.getSportReg();

//                 statement.setString(1, studentIC);
//                 statement.setInt(2, sport);

//                 statement.executeUpdate();

//                 System.out.println("successfully inserted");
//                 // System.out.println("product price : RM"+proprice);
//                 // System.out.println("proimg: "+proimgs.getBytes());

//                 // connection.close();
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 return "redirect:/registration";
//             }
//             connection.close();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return "redirect:/successregistration";
//     }
