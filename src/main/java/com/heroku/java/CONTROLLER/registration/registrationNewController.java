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


        try {
            Connection connection = dataSource.getConnection();
        
            try {
                // Step 1: Retrieve the quota for the activity from the uniform table
                String quotaSql = "SELECT uniformquota FROM uniform WHERE activityid = ?";
                final var quotaStmt = connection.prepareStatement(quotaSql);
                quotaStmt.setInt(1, r.getUnitReg());  // Use activityid from r.getUnitReg()
                ResultSet quotaRs = quotaStmt.executeQuery();
                
                int quota = 0;
                if (quotaRs.next()) {
                    quota = quotaRs.getInt("uniformquota");
                } 
        
                // Step 2: Check if the quota is greater than 0
                if (quota > 0) {
                    // Step 3: Insert the new registration if the quota is greater than 0
                    String insertSql = "INSERT INTO registration(studentic, activityid) VALUES (?, ?)";
                    final var insertStmt = connection.prepareStatement(insertSql);
                    insertStmt.setString(1, studentIC);
                    insertStmt.setInt(2, r.getUnitReg());
                    insertStmt.executeUpdate();

                    System.out.println("Successfully inserted");

                    // Step 4: Decrement the quota by 1 and update the uniform table
                    int newQuota = quota - 1;
                    String updateQuotaSql = "UPDATE uniform SET uniformquota = ? WHERE activityid = ?";
                    final var updateQuotaStmt = connection.prepareStatement(updateQuotaSql);
                    updateQuotaStmt.setInt(1, newQuota);
                    updateQuotaStmt.setInt(2, r.getUnitReg());
                    updateQuotaStmt.executeUpdate();

                } else {
                    System.out.println("Quota reached for this activity. Registration not allowed.");
                    return "redirect:/registerQuota";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/registration";
            }
            
            // club
            try {
                // Step 1: Retrieve the quota for the activity from the uniform table
                String quotaSql = "SELECT clubquota FROM club WHERE activityid = ?";
                final var quotaStmt = connection.prepareStatement(quotaSql);
                quotaStmt.setInt(1, r.getClubReg());  // Use activityid from r.getUnitReg()
                ResultSet quotaRs = quotaStmt.executeQuery();
                
                int quota = 0;
                if (quotaRs.next()) {
                    quota = quotaRs.getInt("clubquota");
                } 
        
                // Step 2: Check if the quota is greater than 0
                if (quota > 0) {
                    // Step 3: Insert the new registration if the quota is greater than 0
                    String insertSql = "INSERT INTO registration(studentic, activityid) VALUES (?, ?)";
                    final var insertStmt = connection.prepareStatement(insertSql);
                    insertStmt.setString(1, studentIC);
                    insertStmt.setInt(2, r.getClubReg());
                    insertStmt.executeUpdate();

                    System.out.println("Successfully inserted");

                    // Step 4: Decrement the quota by 1 and update the uniform table
                    int newQuota = quota - 1;
                    String updateQuotaSql = "UPDATE club SET clubquota = ? WHERE activityid = ?";
                    final var updateQuotaStmt = connection.prepareStatement(updateQuotaSql);
                    updateQuotaStmt.setInt(1, newQuota);
                    updateQuotaStmt.setInt(2, r.getClubReg());
                    updateQuotaStmt.executeUpdate();

                } else {
                    System.out.println("Quota reached for this activity. Registration not allowed.");
                    return "redirect:/registerQuota";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/registration";
            }

            // sport
            try {
                // Step 1: Retrieve the quota for the activity from the uniform table
                String quotaSql = "SELECT sportquota FROM sport WHERE activityid = ?";
                final var quotaStmt = connection.prepareStatement(quotaSql);
                quotaStmt.setInt(1, r.getSportReg());  // Use activityid from r.getUnitReg()
                ResultSet quotaRs = quotaStmt.executeQuery();
                
                int quota = 0;
                if (quotaRs.next()) {
                    quota = quotaRs.getInt("sportquota");
                } 
        
                // Step 2: Check if the quota is greater than 0
                if (quota > 0) {
                    // Step 3: Insert the new registration if the quota is greater than 0
                    String insertSql = "INSERT INTO registration(studentic, activityid) VALUES (?, ?)";
                    final var insertStmt = connection.prepareStatement(insertSql);
                    insertStmt.setString(1, studentIC);
                    insertStmt.setInt(2, r.getSportReg());
                    insertStmt.executeUpdate();

                    System.out.println("Successfully inserted");

                    // Step 4: Decrement the quota by 1 and update the uniform table
                    int newQuota = quota - 1;
                    String updateQuotaSql = "UPDATE sport SET sportquota = ? WHERE activityid = ?";
                    final var updateQuotaStmt = connection.prepareStatement(updateQuotaSql);
                    updateQuotaStmt.setInt(1, newQuota);
                    updateQuotaStmt.setInt(2, r.getSportReg());
                    updateQuotaStmt.executeUpdate();

                } else {
                    System.out.println("Quota reached for this activity. Registration not allowed.");
                    return "redirect:/registerQuota";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/registration";
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
