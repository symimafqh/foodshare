// package com.heroku.java.CONTROLLER.teacher;
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

// import com.heroku.java.MODEL.teacher.TeacherBean;

// import jakarta.servlet.http.HttpSession;

// import java.sql.*;
// import javax.sql.DataSource;
// import java.util.ArrayList;
// import java.util.Map;

// import java.util.List;

// @Controller
// public class SignUpTeacher {
//     private final DataSource dataSource;

//     @Autowired
//     public SignUpTeacher(DataSource dataSource) {
//         this.dataSource = dataSource;
//     }

//     @PostMapping("/teacherRegister")
//     public String registerTeacher(@ModelAttribute("teacherRegister")TeacherBean t){
//         try {
//             Connection connection = dataSource.getConnection();
//             String sql = "INSERT INTO public.teacher(teacherid, teachername, teacheremail, teacherphone, teacherdob, teacherrole, teachergender, teacheraddress, teacherusername, teacherpassword) VALUES(?,?,?,?,?,?,?,?,?,?)";
//             final var statement = connection.prepareStatement(sql);

//             String teacherid=t.getTeacherID();
//             String name= t.getTeacherName();
//             String email=t.getTeacherEmail();
//             String phone=t.getTeacherPhone();
//             String role=t.getTeacherRole();
//             String dob=t.getTeacherDOB();
//             String gender=t.getTeacherGender();
//             String username =t.getTeacherUsername();
//             String address=t.getTeacherAddress();
//             String password =t.getTeacherPassword();
    
//             statement.setString(1,teacherid);
//             statement.setString(2,name);
//             statement.setString(3,email);
//             statement.setString(4,phone);
//             statement.setString(5,dob);
//             statement.setString(6,role);
//             statement.setString(7,gender);
//             statement.setString(8,address);
//             statement.setString(9,username);
//             statement.setString(10,password);
            
//             statement.executeUpdate();
            
//             // System.out.println("type : "+protype);
//             // System.out.println("product price : RM"+proprice);
//             // System.out.println("proimg: "+proimgs.getBytes());
            
//             connection.close();
                
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                     return "redirect:/teacherRegister";
//                 }
                
//             return "redirect:/ListAccountTeacher";
//     }
// }
