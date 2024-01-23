// package com.heroku.java.CONTROLLER.registration;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.stereotype.Controller;
// import org.springframework.stereotype.Repository;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.stereotype.Service;
// import org.springframework.jdbc.core.JdbcTemplate;

// import com.heroku.java.MODEL.student.StudentBean;
// import com.heroku.java.CONTROLLER.activity.ActivityController.ClubBean;
// import com.heroku.java.MODEL.registration.RegistrationBean;

// import jakarta.servlet.http.HttpSession;

// import java.sql.*;
// import javax.sql.DataSource;
// import java.util.ArrayList;
// import java.util.Map;

// import java.util.List;

// @Controller
// public class registrationController {

//     private final DataSource dataSource;

//     @Autowired
//     public registrationController(DataSource dataSource) {
//         this.dataSource = dataSource;
//     }

//     @Autowired
//     private RegistrationService registrationService;

//     // @GetMapping("/")
//     // public String dropdownRegistration(HttpSession session, Model model){
//     //     String studentIC = (String) session.getAttribute("studentIC");
//     //     List<ClubBean> guests = new ArrayList<ClubBean>();
//     //     try (Connection connection = dataSource.getConnection()) {
//     //         String sql = "SELECT guesticnumber, guestname, guestphonenumber, guestgender, guestreligion, guestrace, guestaddress, guestemail, guestpassword FROM public.guest order by guestname"; // ni
//     //         final var statement = connection.prepareStatement(sql);
//     //         // statement.setString(1, "baker"); (syahir punya nih)
//     //         final var resultSet = statement.executeQuery();
//     //         System.out.println("pass try managerGuestList >>>>>");

//     //         while (resultSet.next()) {
//     //             String guestName = resultSet.getString("guestName");
//     //             String guestPhoneNumber = resultSet.getString("guestPhoneNumber");
//     //             String guestICNumber = resultSet.getString("guestICNumber");
//     //             String guestGender = resultSet.getString("guestGender");
//     //             String guestReligion = resultSet.getString("guestReligion");
//     //             String guestRace = resultSet.getString("guestRace");
//     //             String guestAddress = resultSet.getString("guestAddress");
//     //             String guestEmail = resultSet.getString("guestEmail");
//     //             String guestPassword = resultSet.getString("guestPassword");

//     //             // System.out.println("room number" + roomNum);

//     //             guest guest = new guest();
//     //             guest.setGuestName(guestName);
//     //             guest.setGuestPhoneNumber(guestPhoneNumber);
//     //             guest.setGuestICNumber(guestICNumber);
//     //             guest.setGuestGender(guestGender);
//     //             guest.setGuestRace(guestRace);
//     //             guest.setGuestReligion(guestReligion);
//     //             guest.setGuestAddress(guestAddress);
//     //             guest.setGuestEmail(guestEmail);
//     //             guest.setGuestPassword(guestPassword);

//     //             guests.add(guest);
//     //             model.addAttribute("guests", guests);
//     //             // model.addAttribute("isAdmin", staffsrole != null &&
//     //             // staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya
//     //             // gak)

//     //         }

//     //         connection.close();
//     //         return "manager/managerGuestList";
//     //     } catch (SQLException e) {
//     //         e.printStackTrace();
//     //         // Handle the exception as desired (e.g., show an error message)
//     //         return "error";
//     //     }
//     // }

//     // @PostMapping("/registration")
//     // public String registrationActivity(HttpSession session, @ModelAttribute("registercocu") RegistrationBean r,
//     //         Model model) {
//     //     String studentIC = (String) session.getAttribute("studentIC");
//     //     boolean registrationSuccessful = false;

//     //     try (Connection connection = dataSource.getConnection()) {
//     //         try {
//     //             if (registrationService.isStudentAlreadyRegistered(studentIC)) {
//     //                 // Handle case where student is already registered
//     //                 model.addAttribute("registrationStatus", "failed");
//     //                 System.out.println("already register");
//     //                 return "redirect:/registerFailed";
//     //             }

//     //             // Rest of your controller logic...
//     //             // Check if the quota is available for unit, club, and sport
//     //             boolean unitQuotaAvailable = registrationService.isQuotaAvailable(connection, r.getUnitReg());
//     //             boolean clubQuotaAvailable = registrationService.isQuotaAvailableClub(connection, r.getClubReg());
//     //             boolean sportQuotaAvailable = registrationService.isQuotaAvailableSport(connection, r.getSportReg());
//     //             System.out.println(unitQuotaAvailable);
//     //             System.out.println(clubQuotaAvailable);
//     //             System.out.println(sportQuotaAvailable);

//     //             if (!unitQuotaAvailable) {
//     //                 // Quota is full for at least one activity, redirect to quotaFull.jsp

//     //                 return "redirect:/quotaFull";
//     //             }

//     //             // Insert records for unit, club, and sport
//     //             registrationService.insertRegistrationUnit(connection, r.getStudentIC(), r.getUnitReg());
//     //             registrationService.insertRegistrationClub(connection, r.getStudentIC(), r.getClubReg());
//     //             registrationService.insertRegistrationSport(connection, r.getStudentIC(), r.getSportReg());

//     //             // Commit the transaction if all operations succeed
//     //             connection.commit();
//     //             registrationSuccessful = true;
//     //         } catch (Exception e) {
//     //             // Rollback the transaction if any exception occurs
//     //             connection.rollback();
//     //             e.printStackTrace();
//     //         } finally {
//     //             // Set auto-commit back to true
//     //             connection.setAutoCommit(true);
//     //         }
//     //     } catch (SQLException e) {
//     //         e.printStackTrace();
//     //     }

//     //     // Set the registration status in the request
//     //     model.addAttribute("registrationStatus", registrationSuccessful ? "success" : "failed");

//     //     // Forward to the appropriate JSP based on the registration status
//     //     if (registrationSuccessful) {
//     //         model.addAttribute("studentIC", r.getStudentIC());
//     //         return "redirect:/successregistration";
//     //     } else {
//     //         return "redirect:/registrationFailed";
//     //     }
//     // }

// }
