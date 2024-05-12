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

import com.heroku.java.MODEL.student.StudentBean;
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
            session.setAttribute("teacherName",teacherName);
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
                session.setAttribute("teacherName",teacherName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "teacher/profileTeacher/profileAdmin";
      
  }

  // LIST ACCOUNT STUDENTS
   @GetMapping("/ListAccountStudent")
    public String studentAccountList(Model model) {

        List<StudentBean> student = new ArrayList<StudentBean>();

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM student order by studentname";
            final var statement = connection.prepareStatement(sql);
            //statement.setString(1, "baker"); (syahir punya nih)
            final var resultSet = statement.executeQuery();
           

            while (resultSet.next()) {
                String studentIC = resultSet.getString("studentIC");
                String studentName = resultSet.getString("studentName");
                String studentEmail = resultSet.getString("studentEmail");
                String studentPhone = resultSet.getString("studentPhone");
                String studentDOB = resultSet.getString("studentDOB");
                String studentGender = resultSet.getString("studentGender");
               String studentClass = resultSet.getString("studentClass");
               String studentAddress = resultSet.getString("studentAddress");
               String studentPassword = resultSet.getString("studentPassword");
                
                StudentBean s = new StudentBean();
                s.setStudentIC(studentIC);
                s.setStudentName(studentName);
                s.setStudentEmail(studentEmail);
                s.setStudentPhone(studentPhone);
                s.setStudentDOB(studentDOB);
                s.setStudentGender(studentGender);
                s.setStudentClass(studentClass);  
                s.setStudentAddress(studentAddress);
                s.setStudentPassword(studentPassword);              

                student.add(s);
                model.addAttribute("student", student);
                //model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya gak)

            }

            connection.close();

        return "account/ListAccountStudent";
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as desired (e.g., show an error message)
            return "error";
        }
        
    }

    @GetMapping("/viewStudentDetails")
    public String viewStudentDetails(@RequestParam("studentIC") String studentIC, HttpSession session, Model model) {
        String teacherUsername = (String) session.getAttribute("teacherUsername");
        System.out.println("IC Number : " + studentIC);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.student where studentic=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, studentIC);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String studentName = resultSet.getString("studentName");
                String studentEmail = resultSet.getString("studentEmail");
                String studentPhone = resultSet.getString("studentPhone");
                String studentDOB = resultSet.getString("studentDOB");
                String studentGender = resultSet.getString("studentGender");
                String studentClass = resultSet.getString("studentClass");
                String studentAddress = resultSet.getString("studentAddress");
                String studentPassword = resultSet.getString("studentPassword");

                StudentBean s = new StudentBean();

                s.setStudentIC(studentIC);
                s.setStudentName(studentName);
                s.setStudentEmail(studentEmail);
                s.setStudentPhone(studentPhone);
                s.setStudentDOB(studentDOB);
                s.setStudentGender(studentGender);
                s.setStudentClass(studentClass);
                s.setStudentAddress(studentAddress);
                s.setStudentPassword(studentPassword);

                model.addAttribute("s", s);

                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "account/viewStudentDetails";
    }

    @GetMapping("/DeleteStudentAccount")
    public String DeleteStudentAccount(HttpSession session, Model model, @RequestParam("studentIC") String studentIC) {
        //String studentIC = (String) session.getAttribute("studentIC");
        String studentName = (String) session.getAttribute("studentName");
        String studentEmail = (String) session.getAttribute("studentEmail");
        String studentPhone = (String) session.getAttribute("studentPhone");
        String studentDOB = (String) session.getAttribute("studentDOB");
        String studentGender = (String) session.getAttribute("studentGender");
        String studentClass = (String) session.getAttribute("studentClass");
        String studentAddress = (String) session.getAttribute("studentAddress");
        String studentPassword = (String) session.getAttribute("studentPassword");

    try (Connection connection = dataSource.getConnection()) {
        // Delete student from the students account list
        final var DeleteStudentAccount = connection.prepareStatement("DELETE FROM public.student WHERE studentic = ?");
        DeleteStudentAccount.setString(1, studentIC);
        DeleteStudentAccount.executeUpdate();

        System.out.println("Student account successfully deleted");
    } catch (Exception e) {
        System.out.println("Failed to delete student from the student account list");
        e.printStackTrace();
    }
    return "redirect:/ListAccountStudent";
}
// END OF LIST ACCOUNT STUDENT
  
//LIST OF ACCOUNT TEACHER
@GetMapping("/ListAccountTeacher")
public String studentAccountList(Model model) {

    List<TeacherBean> teacher = new ArrayList<TeacherBean>();

    try (Connection connection = dataSource.getConnection()) {
        String sql = "SELECT * FROM teacher order by teacherid";
        final var statement = connection.prepareStatement(sql);
        //statement.setString(1, "baker"); (syahir punya nih)
        final var resultSet = statement.executeQuery();
       

        while (resultSet.next()) {
            String teacherID = resultSet.getString("teacherID");
            String teacherUsername = resultSet.getString("teacherUsername");
            String teacherName = resultSet.getString("teacherName");
            String teacherEmail = resultSet.getString("teacherEmail");
            String teacherPhone = resultSet.getString("teacherPhone");
            String teacherDOB = resultSet.getString("teacherDOB");
            String teacherGender = resultSet.getString("teacherGender");
           String teacherRole = resultSet.getString("teacherRole");
           String teacherAddress = resultSet.getString("teacherAddress");
           String teacherPassword = resultSet.getString("teacherPassword");
            
            TeacherBean t = new TeacherBean();
            t.setTeacherID(teacherID);
            t.setTeacherUsername(teacherUsername);
            t.setTeacherName(teacherName);
            t.setTeacherEmail(teacherEmail);
            t.setTeacherPhone(teacherPhone);
            t.setTeacherGender(teacherGender);  
            t.setTeacherRole(teacherRole);
            t.setTeacherAddress(teacherAddress);
            t.setTeacherPassword(teacherPassword); 
            t.setTeacherDOB(teacherDOB);             

            teacher.add(t);
            model.addAttribute("teacher", teacher);
            //model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya gak)

        }

        connection.close();

    return "account/ListAccountTeacher";
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the exception as desired (e.g., show an error message)
        return "error";
    }
    
}

    @GetMapping("/viewTeacherDetails")
public String viewTeacherDetails(@RequestParam("teacherID") String teacherID, HttpSession session, Model model) {
    //String teacherUsername = (String) session.getAttribute("teacherUsername");
    System.out.println("ID Number : " + teacherID);
    try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT * FROM public.teacher where teacherid=?";
        final var statement = connection.prepareStatement(sql);
        statement.setString(1, teacherID);
        final var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String teacherUsername = resultSet.getString("teacherUsername");
            String teacherName = resultSet.getString("teacherName");
            String teacherEmail = resultSet.getString("teacherEmail");
            String teacherPhone = resultSet.getString("teacherPhone");
            String teacherDOB = resultSet.getString("teacherDOB");
            String teacherGender = resultSet.getString("teacherGender");
           String teacherRole = resultSet.getString("teacherRole");
           String teacherAddress = resultSet.getString("teacherAddress");
           String teacherPassword = resultSet.getString("teacherPassword");

           TeacherBean t = new TeacherBean();

           t.setTeacherID(teacherID);
           t.setTeacherUsername(teacherUsername);
           t.setTeacherName(teacherName);
           t.setTeacherEmail(teacherEmail);
           t.setTeacherPhone(teacherPhone);
           t.setTeacherGender(teacherGender);  
           t.setTeacherRole(teacherRole);
           t.setTeacherAddress(teacherAddress);
           t.setTeacherPassword(teacherPassword); 
           t.setTeacherDOB(teacherDOB);   

            model.addAttribute("t", t);

            connection.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return "account/viewTeacherDetails";
}

@GetMapping("/DeleteTeacherAccount")
public String DeleteTeacherAccount(HttpSession session, Model model, @RequestParam("teacherID") String teacherID) {

    String teacherUsername = (String) session.getAttribute("teacherUsername");
    String teacherName = (String) session.getAttribute("teacherName");
    String teacherEmail = (String) session.getAttribute("teacherEmail");
    String teacherPhone = (String) session.getAttribute("teacherPhone");
    String teacherDOB = (String) session.getAttribute("teacherDOB");
    String teacherGender = (String) session.getAttribute("teacherGender");
    String teacherClass = (String) session.getAttribute("teacherClass");
    String teacherAddress = (String) session.getAttribute("teacherAddress");
    String teacherPassword = (String) session.getAttribute("teacherPassword");

try (Connection connection = dataSource.getConnection()) {
    // Delete student from the students account list
    final var DeleteTeacherAccount = connection.prepareStatement("DELETE FROM public.teacher WHERE teacherid = ?");
    DeleteTeacherAccount.setString(1, teacherID);
    DeleteTeacherAccount.executeUpdate();

    System.out.println("Student account successfully deleted");
} catch (Exception e) {
    System.out.println("Failed to delete teacher from the teacher account list");
    e.printStackTrace();
}
return "redirect:/ListAccountTeacher";
}
//END OF LIST ACCOUNT TEACHER
}
