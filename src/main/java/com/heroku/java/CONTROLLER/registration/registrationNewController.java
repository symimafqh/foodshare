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
public String registrationCocu(HttpSession session, @ModelAttribute("registration") RegistrationBean r, Model model) {
    String studentIC = (String) session.getAttribute("studentIC");
    try (Connection connection = dataSource.getConnection()) {
        // Register for unit activity
        int unit = r.getUnitReg();
        if (!insertRegistrationUnit(connection, studentIC, unit)) {
            return "redirect:/registerQuota"; // Redirect if unit registration fails
        }

        // Register for club activity
        int club = r.getClubReg();
        if (!insertRegistrationUnit(connection, studentIC, club)) {
            return "redirect:/registerQuota"; // Redirect if club registration fails
        }

        // Register for sport activity
        int sport = r.getSportReg();
        if (!insertRegistrationUnit(connection, studentIC, sport)) {
            return "redirect:/registerQuota"; // Redirect if sport registration fails
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/registration";
    }
    return "redirect:/successregistration";
}

public boolean insertRegistrationUnit(Connection con, String studentIC, int activityID) {
    try {
        // Check if the quota is available
        if (isQuotaAvailable(con, activityID)) {
            try (PreparedStatement ps = con
                    .prepareStatement("INSERT INTO registration(studentIC, activityID) VALUES (?,?)")) {
                ps.setString(1, studentIC);
                ps.setInt(2, activityID);

                // execute update for insert operation
                ps.executeUpdate();
                System.out.println(
                        "Successfully inserted registration for " + studentIC + " and activityID " + activityID);

                // Update the quota (decrement by 1)
                int newQuota = updateQuotaForActivity(con, activityID,
                        getQuotaForActivityUnit(con, activityID) - 1);
                System.out.println("Dah decrement satu untuk " + activityID + ". New quota: " + newQuota);
                return true;
            }
        } else {
            System.out.println("Quota not available for activityID: " + activityID);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

// Check if the quota is available
public boolean isQuotaAvailable(Connection con, int activityID) throws SQLException {
    int currentQuota = getQuotaForActivityUnit(con, activityID);
    System.out.println("current quota " + activityID + " is " + currentQuota);
    return currentQuota > 0;
}

// GET QUOTA
private int getQuotaForActivityUnit(Connection con, int activityID) throws SQLException {
    String sql = "SELECT UNIFORMQUOTA FROM UNIT WHERE ACTIVITYID = ?";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setInt(1, activityID);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("UNIFORMQUOTA");
            }
        }
    }
    return 0; // Default to 0 if activityID not found (you may want to handle this differently)
}

// UPDATE QUOTA
private int updateQuotaForActivity(Connection con, int activityID, int newQuota) throws SQLException {
    String sql = "UPDATE UNIFORM SET UNIFORMQUOTA = ? WHERE ACTIVITYID = ?";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setInt(1, newQuota);
        pstmt.setInt(2, activityID);
        pstmt.executeUpdate();
    }
    return newQuota;
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
