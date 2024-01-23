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
import com.heroku.java.MODEL.activity.ActivityBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;
@Controller
public class SideBarControllerTeacher {

     private final DataSource dataSource;

    @Autowired
    public SideBarControllerTeacher(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/dashboardTeacher")
      public String dashboardTeacher(@RequestParam(name = "success", required = false) Boolean success, HttpSession session) {
        String teacherUsername = (String) session.getAttribute("teacherUsername");
        return "teacher/dashboardTeacher";
    }

    @GetMapping("/profileTeacher_edit")
    public String editProfileTeacher(@RequestParam(name = "success", required = false) Boolean success, Model model, HttpSession session) {
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

                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "teacher/profileTeacher/profileTeacher_edit";
    }

        @GetMapping("/infoClubTeacher")
    public String clubList(Model model) {

        List<ClubBean> club = new ArrayList<ClubBean>();

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM club order by namaclub";
            final var statement = connection.prepareStatement(sql);
            //statement.setString(1, "baker"); (syahir punya nih)
            final var resultSet = statement.executeQuery();
           

            while (resultSet.next()) {
                String namaClub = resultSet.getString("namaClub");
                String infoClub = resultSet.getString("infoClub");
                int quotaClub = resultSet.getInt("quotaClub");
                int activityID = resultSet.getInt("activityID");
                
                ClubBean c = new ClubBean();
                c.setNamaClub(namaClub);
                c.setInfoClub(infoClub);
                c.setQuotaClub(quotaClub);
                c.setActivityID(activityID);
         

                club.add(c);
                model.addAttribute("club", club);
                //model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya gak)

            }

            connection.close();

        return "teacherActivity/infoClubTeacher";
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as desired (e.g., show an error message)
            return "error";
        }
        
    }

    public class ClubBean {
	
        private String namaClub;
        private String infoClub;
        private int quotaClub;
        private int activityID;

        public String getNamaClub() {
            return namaClub;
        }
    
        public void setNamaClub(String namaClub) {
            this.namaClub = namaClub;
        }
    
        public String getInfoClub() {
            return infoClub;
        }
    
        public void setInfoClub(String infoClub) {
            this.infoClub = infoClub;
        }
    
        public int getQuotaClub() {
            return quotaClub;
        }
    
        public void setQuotaClub(int quotaClub) {
            this.quotaClub = quotaClub;
        }

        public int getActivityID() {
            return activityID;
        }
    
        public void setActivityID(int activityID) {
            this.activityID = activityID;
        }
}
}