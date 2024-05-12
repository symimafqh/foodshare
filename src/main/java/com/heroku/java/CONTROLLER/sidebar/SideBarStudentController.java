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

import com.heroku.java.CONTROLLER.sidebar.SideBarAdminController.ActivityBean;
import com.heroku.java.CONTROLLER.sidebar.SideBarAdminController.ClubBean;
import com.heroku.java.CONTROLLER.sidebar.SideBarAdminController.SukanBean;
import com.heroku.java.MODEL.student.StudentBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class SideBarStudentController {
    private final DataSource dataSource;

    @Autowired
    public SideBarStudentController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/dashboardStudent")
    public String index1(@RequestParam(name = "success", required = false) Boolean success, HttpSession session, Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        boolean registered = isStudentRegistered(studentIC);
        model.addAttribute("isStudentRegistered", registered);
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
                session.setAttribute("studentName",studentName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "student/dashboardStudent";
    }

    @GetMapping("/edit_profile")
    public String editProfile(@RequestParam(name = "success", required = false) Boolean success, HttpSession session,
            Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        boolean registered = isStudentRegistered(studentIC);
        model.addAttribute("isStudentRegistered", registered);
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
                session.setAttribute("studentName",studentName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "student/profile/edit_profile";
    }

    @GetMapping("/registration")
    public String registration(HttpSession session, Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        // int activityid = (int) session.getAttribute("activityID");
        System.out.println("guestICNumber: " + studentIC);

            // Connection connection = dataSource.getConnection();
            //check registered activity
            boolean registered = isStudentRegistered(studentIC);
            model.addAttribute("isStudentRegistered", registered);
        List<SukanBean> sukan = new ArrayList<SukanBean>();
        try {
             Connection connection = dataSource.getConnection();
            String sql = "SELECT DISTINCT a.activityid, a.activityname, s.sportinformation, s.sportquota " +
                    "FROM activity a JOIN sport s ON a.activityid = s.activityid";
            final var statement = connection.prepareStatement(sql);
            final var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                SukanBean s = new SukanBean();
                ActivityBean activity = new ActivityBean();
                activity.setActivityID(resultSet.getInt("activityID"));
                activity.setActivityName(resultSet.getString("activityName"));

                s.setActivity(activity);
                s.setInfoSukan(resultSet.getString("sportInformation"));
                s.setQuotaSukan(resultSet.getInt("sportQuota"));

                sukan.add(s);

            }
            connection.close();
            model.addAttribute("sukan", sukan);

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ClubBean> club = new ArrayList<ClubBean>();
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT DISTINCT a.activityid, a.activityname, c.clubinformation, c.clubquota " +
                    "FROM activity a JOIN club c ON a.activityid = c.activityid";
            final var statement = connection.prepareStatement(sql);
            final var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ClubBean c = new ClubBean();
                ActivityBean activity = new ActivityBean();
                activity.setActivityID(resultSet.getInt("activityID"));
                activity.setActivityName(resultSet.getString("activityName"));

                c.setActivity(activity);
                c.setInfoClub(resultSet.getString("clubInformation"));
                c.setQuotaClub(resultSet.getInt("clubQuota"));

                club.add(c);
            }
            connection.close();
            model.addAttribute("club", club);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<UnitBean> unit = new ArrayList<UnitBean>();
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT DISTINCT a.activityid, a.activityname, u.uniforminformation, u.uniformquota " +
                    "FROM activity a JOIN uniform u ON a.activityid = u.activityid";
            final var statement = connection.prepareStatement(sql);
            final var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UnitBean u = new UnitBean();
                ActivityBean activity = new ActivityBean();
                activity.setActivityID(resultSet.getInt("activityID"));
                activity.setActivityName(resultSet.getString("activityName"));

                u.setActivity(activity);
                u.setInfoUnit(resultSet.getString("uniformInformation"));
                u.setQuotaUnit(resultSet.getInt("uniformQuota"));

                unit.add(u);

            }
            connection.close();
            model.addAttribute("unit", unit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "student/registration/registration";
    }

     // method to check dah register ke belum
     public boolean isStudentRegistered(String studentIC) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM registration WHERE studentic = ?")) {

            preparedStatement.setString(1, studentIC);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // If the count is greater than 0, the student is registered
                return resultSet.next() && resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception based on your application's requirements
        }

        return false;
    }

    @GetMapping("/semakan")
    public String semakan() {
        return "semakan";
    }

    @GetMapping("/successregistration")
    public String successregister(HttpSession session,
            Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        boolean registered = isStudentRegistered(studentIC);
        model.addAttribute("isStudentRegistered", registered);
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

        return "student/registration/registrationsuccess";
    }

    @GetMapping("/registered")
    public String registered(HttpSession session, Model model) {
        String studentIC = (String) session.getAttribute("studentIC");
        boolean registered = isStudentRegistered(studentIC);
        model.addAttribute("isStudentRegistered", registered);
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

        return "student/registration/registered";
    }

    // ---------------------------BEANS------------------------------//
    interface Bean {
        int getActivityID();

        void setActivityID(int activityID);
    }

    // ---------------------------SUKAN BEAN------------------------------//
    public class SukanBean implements Bean {

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

    // ---------------------------UNIT BEAN------------------------------//
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

    // ---------------------------CLUB BEAN------------------------------//
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

    // ---------------------------ACTIVITY BEAN------------------------------//
    public class ActivityBean implements Bean {

        private String activityName;
        private String TeacherID;
        private int activityID;

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
    @GetMapping("/semakpendaftaran")
public String viewPendaftaran(HttpSession session, Model model, StudentBean sb) {
    String studentIC = (String) session.getAttribute("studentIC");
    // String studentName = 
    // String
    System.out.println("ID Number : " + studentIC);
    boolean registered = isStudentRegistered(studentIC);
        model.addAttribute("isStudentRegistered", registered);
    
    try (Connection connection = dataSource.getConnection()) {
        List<String> activityNamesUnit = new ArrayList<>();
        List<String> activityNamesClub = new ArrayList<>();
        List<String> activityNamesSport = new ArrayList<>();

        // Query sport
        String sql1 = "SELECT A.activityname FROM registration R "
                + "JOIN sport A ON A.activityid = R.activityid "
                + "JOIN student S ON S.studentic = R.studentic WHERE R.studentic=?";
        PreparedStatement statement1 = connection.prepareStatement(sql1);
        statement1.setString(1, studentIC);
        ResultSet resultSet1 = statement1.executeQuery();
        
        while (resultSet1.next()) {
            
            activityNamesSport.add(resultSet1.getString("activityname"));

            System.out.println(activityNamesSport);
        }

        // Query unit
        String sql2 = "SELECT A.activityname FROM registration R "
                + "JOIN uniform A ON A.activityid = R.activityid "
                + "JOIN student S ON S.studentic = R.studentic WHERE R.studentic=?";
        PreparedStatement statement2 = connection.prepareStatement(sql2);  // Fix variable name
        statement2.setString(1, studentIC);
        ResultSet resultSet2 = statement2.executeQuery();

        while (resultSet2.next()) {
            activityNamesUnit.add(resultSet2.getString("activityname"));
            System.out.println(activityNamesUnit);
        }

        // Query club
        String sql3 = "SELECT A.activityname FROM registration R "
                + "JOIN club A ON A.activityid = R.activityid "
                + "JOIN student S ON S.studentic = R.studentic WHERE R.studentic=?";
        PreparedStatement statement3 = connection.prepareStatement(sql3);  // Fix variable name
        statement3.setString(1, studentIC);
        ResultSet resultSet3 = statement3.executeQuery();

        while (resultSet3.next()) {
            activityNamesClub.add(resultSet3.getString("activityname"));
            System.out.println(activityNamesClub);
        }

        model.addAttribute("activityNamesUnit", activityNamesUnit);
        model.addAttribute("activityNamesSport", activityNamesSport);
        model.addAttribute("activityNamesClub", activityNamesClub);
    } catch (Exception e) {
        e.printStackTrace();
    }

    return "student/registration/semakpendaftaran";  // Corrected return statement
}

}
