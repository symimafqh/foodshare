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
    public String clubListTeacher(Model model) {

        List<ClubBean> clubListTeacher = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            
            final var statement = connection.createStatement();
            String sql = "SELECT a.activityid, a.activityname " +
            "FROM activity a JOIN club c ON a.activityid = c.activityid";

            try (ResultSet resultSet = statement.executeQuery(sql)) {
           
                while (resultSet.next()) {
                    ClubBean club = new ClubBean();
				    ActivityBean activity = new ActivityBean();

                    activity.setActivityID(resultSet.getInt("activityID"));
                    activity.setActivityName(resultSet.getString("activityName"));

                    // Set ActivityBean as a property of SukanBean
                    club.setActivity(activity);
         

                    clubListTeacher.add(club);
        
                    //model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya gak)

            }
        }

            connection.close();

            model.addAttribute("clubListTeacher", clubListTeacher);

            

        return "teacher/teacherActivity/infoClubTeacher";
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as desired (e.g., show an error message)
            return "error";
        }
        
    }


    //---------------------------BEANS------------------------------//
    interface Bean {
        int getActivityID();
        void setActivityID(int activityID);
    }
    
    //---------------------------SUKAN BEAN------------------------------//
    public class SukanBean implements Bean  {
	
        private String namaSukan;
        private String infoSukan;
        private int quotaSukan;
        private int activityID;

        public SukanBean() {
        }
        
        @Override
        public int getActivityID() {
            return activityID;
        }
        @Override
        public void setActivityID(int activityID) {
            this.activityID = activityID;
        }
    
        private ActivityBean activity;
    
        // Setter method for ActivityBean
        public void setActivity(ActivityBean activity) {
            this.activity = activity;
        }
    
        // Getter method for ActivityBean
        public ActivityBean getActivity() {
            return activity;
        }
          
        public String getNamaSukan() {
            return namaSukan;
        }
    
        public void setNamaSukan(String namaSukan) {
            this.namaSukan = namaSukan;
        }
    
        public String getInfoSukan() {
            return infoSukan;
        }
    
        public void setInfoSukan(String infoSukan) {
            this.infoSukan = infoSukan;
        }
    
        public int getQuotaSukan() {
            return quotaSukan;
        }
    
        public void setQuotaSukan(int quotaSukan) {
            this.quotaSukan = quotaSukan;
        }
    

    }

    //---------------------------UNIT BEAN------------------------------//
    public class UnitBean {
	
        private String namaUnit;
        private String infoUnit;
        private int quotaUnit;
        private int activityID;
        
        public int getActivityID() {
            return activityID;
        }
    
        public void setActivityID(int activityID) {
            this.activityID = activityID;
        }
    
        private ActivityBean activity;

        // Setter method for ActivityBean
        public void setActivity(ActivityBean activity) {
            this.activity = activity;
        }
    
        // Getter method for ActivityBean
        public ActivityBean getActivity() {
            return activity;
        }
          
        public String getNamaUnit() {
            return namaUnit;
        }
    
        public void setNamaUnit(String namaUnit) {
            this.namaUnit = namaUnit;
        }
    
        public String getInfoUnit() {
            return infoUnit;
        }
    
        public void setInfoUnit(String infoUnit) {
            this.infoUnit = infoUnit;
        }
    
        public int getQuotaUnit() {
            return quotaUnit;
        }
    
        public void setQuotaUnit(int quotaUnit) {
            this.quotaUnit = quotaUnit;
        }
    }

    //---------------------------CLUB BEAN------------------------------//
public class ClubBean {
	
	private String namaClub;
	private String infoClub;
	private int quotaClub;
	private int activityID;
	
	public int getActivityID() {
		return activityID;
	}

	public void setActivityID(int activityID) {
		this.activityID = activityID;
	}

	private ActivityBean activity;
	
	/*
	 * public int getActivityID() { // This getter is not necessary if it's already
	 * defined in ActivityBean return super.getActivityID(); // This calls the
	 * inherited getter from ActivityBean }
	 * 
	 * public void setActivityID(int activityID) { // This setter is not necessary
	 * if it's already defined in ActivityBean super.setActivityID(activityID); //
	 * This calls the inherited setter from ActivityBean }
	 */

	// Setter method for ActivityBean
    public void setActivity(ActivityBean activity) {
        this.activity = activity;
    }

    // Getter method for ActivityBean
    public ActivityBean getActivity() {
        return activity;
    }
	  
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
}

    

    //---------------------------ACTIVITY BEAN------------------------------//
    public class ActivityBean implements Bean{
	
        private String activityName;
        private String TeacherID;
        private  int activityID;

        public ActivityBean() {
        }
        
        @Override
        public int getActivityID() {
            return activityID;
        }
    
        public Integer getMaxActivityID() {
            int maxActivityID = 0;
		try (Connection con = dataSource.getConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT MAX(ACTIVITYID) FROM ACTIVITY")) {

			if (rs.next()) {
				maxActivityID = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// Handle exceptions as needed
		}
		return maxActivityID;
        }

        @Override
        public void setActivityID(int activityID) {
            this.activityID = activityID;
        }
    
        public String getTeacherID() {
            return TeacherID;
        }
    
        public void setTeacherID(String teacherID) {
            TeacherID = teacherID;
        }
    
        public String getActivityName() {
            return activityName;
        }
    
        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }
    
    }
}
