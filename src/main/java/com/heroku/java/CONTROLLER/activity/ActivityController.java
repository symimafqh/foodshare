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
import org.springframework.web.bind.annotation.RequestBody;


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
                unit.setInfoUnit(resultSet.getString("uniforminformation"));
                unit.setQuotaUnit(resultSet.getInt("uniformquota"));

                unitList.add(unit);
            }
        }
        connection.close();

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
        connection.close();

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
@GetMapping("/addSukann")
    public String AddNewSukan() {
        return "teacher/activity/AddNewSukan";
    }

    @PostMapping("/AddNewSukan")
    public String AddNewSukan(@RequestParam String namaSukan, @RequestParam String info, @RequestParam Integer quota, Model model, @ModelAttribute("AddNewSukan") SukanBean sukanBean, ActivityBean activityBean, HttpSession session) {
        String teacherID = (String) session.getAttribute("teacherID");
        System.out.println("ID Number : " + teacherID);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO activity(activityname, teacherid) VALUES (?, ?)";
            final var statement = connection.prepareStatement(sql);

            String activity = namaSukan;
            // String TeacherID =activityBean.getTeacherID();
           

            statement.setString(1, activity);
            statement.setString(2, teacherID);

            statement.executeUpdate();

            // Get id from database for sql 2 from sql 1
            ActivityBean parent = new ActivityBean();
            Integer parentActivity = parent.getMaxActivityID();
            // String sportsInfo = sukanBean.getInfoSukan();
            // Integer sportsQuota = sukanBean.getQuotaSukan();
            String sportsInfo = info;
            Integer sportsQuota = quota;


            System.out.println("activityID from PARENT: " + parentActivity);
                
                    String child = "INSERT INTO sport (activityid, activityname, teacherid, sportinformation, sportquota) VALUES (?, ?, ?, ?, ?)";
                    final var sportStatement = connection.prepareStatement(child);
                    sportStatement.setInt(1, parentActivity);
                    sportStatement.setString(2, activity);
                    sportStatement.setString(3, teacherID);
                    sportStatement.setString(4, sportsInfo);
                    sportStatement.setInt(5, sportsQuota);

                    sportStatement.executeUpdate();
               
                model.addAttribute("success", true);

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/AddNewSukan?success=false";
        }

        return "redirect:/AddNewSukan?success=true";
    }


    //---------------------------ADD CLUB------------------------------//
@GetMapping("/addClubb")
public String AddNewClub() {
    return "teacher/activity/AddNewClub";
}

@PostMapping("/AddNewClub")
public String AddNewClub(@RequestParam String namaClub, @RequestParam String info, @RequestParam Integer quota, Model model, @ModelAttribute("AddNewClub") ClubBean sukanBean, ActivityBean activityBean, HttpSession session) {
    String teacherID = (String) session.getAttribute("teacherID");
    System.out.println("ID Number : " + teacherID);
    try {
        Connection connection = dataSource.getConnection();
        String sql = "INSERT INTO activity(activityname, teacherid) VALUES (?, ?)";
        final var statement = connection.prepareStatement(sql);

        String activity = namaClub;
        // String TeacherID =activityBean.getTeacherID();
       

        statement.setString(1, activity);
        statement.setString(2, teacherID);

        statement.executeUpdate();

        // Get id from database for sql 2 from sql 1
        ActivityBean parent = new ActivityBean();
        Integer parentActivity = parent.getMaxActivityID();
        // String sportsInfo = sukanBean.getInfoClub();
        // Integer sportsQuota = sukanBean.getQuotaClub();
        String clubInfo = info;
        Integer clubQuota = quota;


        System.out.println("activityID from PARENT: " + parentActivity);
            
                String child = "INSERT INTO club (activityid, activityname, teacherid, clubinformation, clubquota) VALUES (?, ?, ?, ?, ?)";
                final var clubStatement = connection.prepareStatement(child);
                clubStatement.setInt(1, parentActivity);
                clubStatement.setString(2, activity);
                clubStatement.setString(3, teacherID);
                clubStatement.setString(4, clubInfo);
                clubStatement.setInt(5, clubQuota);

                clubStatement.executeUpdate();
           
            model.addAttribute("success", true);

        connection.close();

    } catch (SQLException e) {
        e.printStackTrace();
        return "redirect:/AddNewClub?success=false";
    }

    return "redirect:/AddNewClub?success=true";
}

    //---------------------------ADD Unit------------------------------//
    @GetMapping("/addUnitt")
    public String AddNewUnit() {
        return "teacher/activity/addNewUnit";
    }
    
    @PostMapping("/AddNewUnit")
    public String AddNewUnit(@RequestParam String namaUnit, @RequestParam String info, @RequestParam Integer quota, Model model, @ModelAttribute("AddNewUnit") UnitBean sukanBean, ActivityBean activityBean, HttpSession session) {
        String teacherID = (String) session.getAttribute("teacherID");
        System.out.println("ID Number : " + teacherID);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO activity(activityname, teacherid) VALUES (?, ?)";
            final var statement = connection.prepareStatement(sql);
    
            String activity = namaUnit;
            // String TeacherID =activityBean.getTeacherID();
           
    
            statement.setString(1, activity);
            statement.setString(2, teacherID);
    
            statement.executeUpdate();
    
            // Get id from database for sql 2 from sql 1
            ActivityBean parent = new ActivityBean();
            Integer parentActivity = parent.getMaxActivityID();
            // String sportsInfo = sukanBean.getInfoUnit();
            // Integer sportsQuota = sukanBean.getQuotaUnit();
            String unitInfo = info;
            Integer unitQuota = quota;
    
    
            System.out.println("activityID from PARENT: " + parentActivity);
                
                    String child = "INSERT INTO uniform (activityid, activityname, teacherid, uniforminformation, uniformquota) VALUES (?, ?, ?, ?, ?)";
                    final var unitStatement = connection.prepareStatement(child);
                    unitStatement.setInt(1, parentActivity);
                    unitStatement.setString(2, activity);
                    unitStatement.setString(3, teacherID);
                    unitStatement.setString(4, unitInfo);
                    unitStatement.setInt(5, unitQuota);
    
                    unitStatement.executeUpdate();
               
                model.addAttribute("success", true);
    
            connection.close();
    
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/addNewUnit?success=false";
        }
    
        return "redirect:/addNewUnit?success=true";
    }
    
    //---------------------------UPDATE SUKAN------------------------------//
    @GetMapping("/UpdateSukan")
public String UpdateSukan( @RequestParam("activityID") int activityid,Model model, HttpSession session) {
    //Integer activityid = (Integer) session.getAttribute("activityID");
    System.out.println("activity id "+activityid);

    SukanBean sukan = new SukanBean(); // Instantiate SukanBean
    ActivityBean activity = new ActivityBean(); // Instantiate ActivityBean

    try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT a.activityid, a.activityname, s.sportinformation, s.sportquota " +
            "FROM activity a JOIN sport s ON a.activityid = s.activityid WHERE a.activityid = ?";
        final var statement = connection.prepareStatement(sql);
        statement.setInt(1, activityid);
        final var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String activityName = resultSet.getString("activityname");
            String infoSukan = resultSet.getString("sportinformation");
            int quotaSukan = resultSet.getInt("sportquota");

            // SukanBean sukan = new SukanBean(); // Instantiate SukanBean
            // ActivityBean activity = new ActivityBean(); // Instantiate ActivityBean

            System.out.println("Sukan object: " + sukan);
            // Set the values to the Sukan object
            sukan.setActivityID(activityid);
            sukan.setNamaSukan(activityName);
            sukan.setInfoSukan(infoSukan);
            sukan.setQuotaSukan(quotaSukan);

            // model.addAttribute("sukan", sukan);
            // model.addAttribute("activity", activity);

            connection.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    model.addAttribute("sukan", sukan);
    model.addAttribute("activity", activity);


    return "teacher/activity/UpdateSukan";
}


    @PostMapping("/UpdateSukan")
    public String UpdateSukan(@RequestParam("activityID") int activityid, @RequestParam("namaSukan") String activityName, @RequestParam("info") String info, @RequestParam("quota") Integer quota, ActivityBean ab, SukanBean sb, Model model) {
        
        //int activity = activityId;
        //String unitName = activityName;

        System.out.println(activityid);
        System.out.println(activityName);
        System.out.println(info);
        System.out.println(quota);
	    try {
            Connection con = dataSource.getConnection();
	        String sql1 = "UPDATE ACTIVITY SET ACTIVITYNAME = ? WHERE ACTIVITYID = ?";
	        try (PreparedStatement ps1 = con.prepareStatement(sql1)) {
	            ps1.setString(1, activityName);
	            ps1.setInt(2, activityid);
	            ps1.executeUpdate();
	            System.out.println("Successfully updated activity table "+activityid);
	        }

	        String sql2 = "UPDATE SPORT SET SPORTINFORMATION=?, SPORTQUOTA=?  WHERE ACTIVITYID = ?";
	        try (PreparedStatement ps2 = con.prepareStatement(sql2)) {
	            ps2.setString(1, info);
	            ps2.setInt(2, quota); // Assuming getQuotaSukan() returns an integer
	            ps2.setInt(3, activityid);
	            ps2.executeUpdate();
	            System.out.println("Successfully updated sport table");
	        }
            model.addAttribute("success", true);
    
            con.close();

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Handle the exception as needed
            return "redirect:/AddNewSukan?success=false";
	    } 
	    


        
        return "redirect:/AddNewSukan?success=true";
    }
    
    





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
