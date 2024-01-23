package com.heroku.java.CONTROLLER.activity;
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

import com.heroku.java.MODEL.teacher.TeacherBean;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class ActivityController {
    private final DataSource dataSource;

    @Autowired
    public ActivityController(DataSource dataSource) {
        this.dataSource = dataSource;
    }
//---------------------------LIST SUKAN------------------------------//
    @GetMapping("/AddNewSukan")
    public String sukanList(Model model) {
        List<SukanBean> sukanList = new ArrayList<SukanBean>();
    
        try (Connection connection = dataSource.getConnection()) {
            // create statement
            final var statement = connection.createStatement();
            String sql = "SELECT a.activityid, a.activityname, s.sportinformation, s.sportquota " +
                         "FROM activity a JOIN sport s ON a.activityid = s.activityid";
    
            final var resultSet = statement.executeQuery(sql);
    
            while (resultSet.next()) {
                SukanBean sukan = new SukanBean();
                ActivityBean activity = new ActivityBean();
    
                // Assuming ACTIVITYID and ACTIVITYNAME come from the ACTIVITY table
                activity.setActivityID(resultSet.getInt("activityID"));
                activity.setActivityName(resultSet.getString("activityName"));
    
                // Set ActivityBean as a property of SukanBean
                sukan.setActivity(activity);
    
                // Assuming SPORTINFORMATION and SPORTQUOTA come from the SPORT table
                sukan.setInfoSukan(resultSet.getString("sportInformation"));
                sukan.setQuotaSukan(resultSet.getInt("sportQuota"));
    
                sukanList.add(sukan);
            }
    
            connection.close();
    
            // Use the Model object to pass the list to the view
            model.addAttribute("sukanList", sukanList);
    
            return "teacher/activity/AddNewSukan"; // Return the view name as a String
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging or throwing a custom exception
            return "error"; // Replace with an appropriate error view name
        }
    }
    
//---------------------------LIST UNIT------------------------------//
@GetMapping("/addNewUnit")
public String unitList(Model model) {
    List<UnitBean> unitList = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
        // create statement
        final var statement = connection.createStatement();
        String sql = "SELECT a.activityid, a.activityname, s.uniforminformation, s.uniformquota " +
                     "FROM activity a JOIN uniform s ON a.activityid = s.activityid";

        try (ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                UnitBean unit = new UnitBean();
                ActivityBean activity = new ActivityBean();

                activity.setActivityID(resultSet.getInt("activityID"));
                activity.setActivityName(resultSet.getString("activityName"));

                unit.setActivity(activity);
                unit.setInfoUnit(resultSet.getString("unitInformation"));
                unit.setQuotaUnit(resultSet.getInt("unitQuota"));

                unitList.add(unit);
            }
        }

        // Use the Model object to pass the list to the view
        model.addAttribute("unitList", unitList);

        return "teacher/activity/addNewUnit";
    } catch (SQLException e) {
        // Log the exception using a logging framework like SLF4J
        e.printStackTrace(); // Consider logging or throwing a custom exception
        return "error"; // Replace with an appropriate error view name
    }
}

//---------------------------LIST club------------------------------//
@GetMapping("/AddNewClub")
public String clubList(Model model) {
    List<ClubBean> clubList = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
        // create statement
        final var statement = connection.createStatement();
        String sql = "SELECT a.activityid, a.activityname, s.clubinformation, s.clubquota " +
                     "FROM activity a JOIN club s ON a.activityid = s.activityid";

        try (ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                ClubBean club = new ClubBean();
                ActivityBean activity = new ActivityBean();

                activity.setActivityID(resultSet.getInt("activityID"));
                activity.setActivityName(resultSet.getString("activityName"));

                club.setActivity(activity);
                club.setInfoClub(resultSet.getString("clubInformation"));
                club.setQuotaClub(resultSet.getInt("clubQuota"));

                clubList.add(club);
            }
        }

        // Use the Model object to pass the list to the view
        model.addAttribute("clubList", clubList);

        return "teacher/activity/AddNewClub";
    } catch (SQLException e) {
        // Log the exception using a logging framework like SLF4J
        e.printStackTrace(); // Consider logging or throwing a custom exception
        return "error"; // Replace with an appropriate error view name
    }
}

//---------------------------ADD SUKAN------------------------------//
@PostMapping("/AddSukan")
public String addSukan(@ModelAttribute("sukanForm") Bean bean, Model model) {
    try {
        Connection connection = dataSource.getConnection();

        if (bean instanceof ActivityBean) {
            handleActivityBean((ActivityBean) bean, connection);
        } else if (bean instanceof SukanBean) {
            handleSukanBean((SukanBean) bean, connection);
        }

        // Successfully inserted
        model.addAttribute("success", true);
        connection.close();

        return "redirect:/teacher/activity/AddNewSukan?success=true";

    } catch (Exception e) {
        e.printStackTrace();

        // Error handling - log details or provide more meaningful messages
        model.addAttribute("success", false);
        return "redirect:/teacher/activity/AddNewSukan?success=false";
    }
}

private void handleActivityBean(ActivityBean activityBean, Connection connection) throws SQLException {
    String activityName = activityBean.getActivityName();
    String teacherID = activityBean.getTeacherID();

    try (var preparedStatement = connection.prepareStatement(
        "INSERT INTO ACTIVITY(activityName, teacherID) VALUES (?, ?)")) {

        preparedStatement.setString(1, activityName);
        preparedStatement.setString(2, teacherID);
        preparedStatement.executeUpdate();
    }
}

private void handleSukanBean(SukanBean sukanBean, Connection connection) throws SQLException {
    String sportsInfo = sukanBean.getInfoSukan();
    Integer sportsQuota = sukanBean.getQuotaSukan();

    ActivityBean parent = new ActivityBean();
    Integer parentActivity = parent.getMaxActivityID();

    try (var preparedStatement = connection.prepareStatement(
        "INSERT INTO SPORT (activityid, sportinformation, sportquota) VALUES (?, ?, ?)")) {

        preparedStatement.setInt(1, parentActivity);
        preparedStatement.setString(2, sportsInfo);
        preparedStatement.setInt(3, sportsQuota);
        preparedStatement.executeUpdate();
    }
}







    public interface Bean {
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
