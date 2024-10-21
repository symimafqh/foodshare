// package com.heroku.java.CONTROLLER.cafeteria_owner;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.heroku.java.MODEL.student.StudentBean;
// import com.heroku.java.MODEL.teacher.CafeBean;

// import jakarta.servlet.http.HttpSession;

// import java.sql.*;
// import javax.sql.DataSource;
// import java.util.ArrayList;
// import java.util.Map;

// import java.util.List;

// @Controller
// public class LoginAdminController {

//     private final DataSource dataSource;

//     @Autowired
//     public LoginAdminController(DataSource dataSource) {
//         this.dataSource = dataSource;
//     }

//     @GetMapping("/adminSignin")
//     public String adminsignin() {
//         return "teacher/sign-in/adminSignin";
//     }

//     @PostMapping("/adminSignin")
//     public String LoginAdmin(HttpSession session,@RequestParam(name = "success", required = false) Boolean success, String teacherUsername, String teacherPassword, CafeBean t, Model model) {

//         try {
//             // String returnPage = null;
//             Connection connection = dataSource.getConnection();

//             String sql = "SELECT * FROM public.teacher WHERE teacherusername=? AND teacherpassword=? AND teacherrole='admin'";
//             final var statement = connection.prepareStatement(sql);
//             statement.setString(1, teacherUsername);
//             statement.setString(2, teacherPassword);

//             final var resultSet = statement.executeQuery();

//             System.out.println("teacher ic : " + teacherUsername);
//             System.out.println("teacher pass : " + teacherPassword);

//             if (resultSet.next()) {

//                 // String guestICNumber = resultSet.getString("guestICNumber");
//                 String teacherID = resultSet.getString("teacherID");
//                 String username = resultSet.getString("teacherUsername");
//                 String password = resultSet.getString("teacherPassword");
//                 String teacherName = resultSet.getString("teacherName");
//                 String teacherEmail = resultSet.getString("teacherEmail");
//                 String teacherPhone = resultSet.getString("teacherPhone");
//                 String teacherDOB = resultSet.getString("teacherDOB");
//                 String teacherGender = resultSet.getString("teacherGender");
//                String teacherRole = resultSet.getString("teacherRole");
//                String teacherAddress = resultSet.getString("teacherAddress");

//                 System.out.println(username);
//                 // if they're admin
//                 // System.out.println("Email : " + guestEmail.equals(email) + " | " + email);
//                 // System.out.println("Password status : " + guestPassword.equals(password));

//                 if (username.equals(teacherUsername) && password.equals(teacherPassword)) {

//                     session.setAttribute("teacherID", teacherID);
//                     session.setAttribute("teacherUsername", teacherUsername);
//                     session.setAttribute("teacherPassword", teacherPassword);
//                     session.setAttribute("teacherName", teacherName);
//                     session.setAttribute("teacherEmail", teacherEmail);
//                     session.setAttribute("teacherPhone", teacherPhone);
//                     session.setAttribute("teacherDOB", teacherDOB);
//                     session.setAttribute("teacherGender", teacherGender);
//                     session.setAttribute("teacherRole", teacherRole);
//                     session.setAttribute("teacherAddress", teacherAddress);


//                     return "redirect:/dashboardAdmin?success=true" ;
//                 }
//             }

//             connection.close();
//             return "redirect:/adminSignin?invalidUsername&Password";

//         } catch (SQLException sqe) {
//             System.out.println("Error Code = " + sqe.getErrorCode());
//             System.out.println("SQL state = " + sqe.getSQLState());
//             System.out.println("Message = " + sqe.getMessage());
//             System.out.println("printTrace /n");
//             sqe.printStackTrace();

//             return "redirect:/adminsignin?error";

//         } catch (Exception e) {
//             System.out.println("E message : " + e.getMessage());
//             return "redirect:/adminsignin?error";
//         }
//     }

//     @GetMapping("/logoutadmin")
//     public String logoutAdmin(HttpSession session) {
//         session.invalidate();
//         return "index";
//     }
// }
