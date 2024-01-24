package com.heroku.java.CONTROLLER.activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heroku.java.CONTROLLER.activity.ActivityController.ActivityBean;
import com.heroku.java.CONTROLLER.activity.ActivityController.Bean;
import com.heroku.java.CONTROLLER.activity.ActivityController.SukanBean;
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
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api")
public class SukanController {

    @Autowired
    private DataSource dataSource; // Inject the DataSource bean

    @GetMapping("/addNewSukan")
public ResponseEntity<List<SukanBean>> sukanList() {
    List<SukanBean> sukanList = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
        // create statement
        final var statement = connection.createStatement();
        String sql = "SELECT DISTINCT a.activityid, a.activityname, s.sportinformation, s.sportquota " +
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

        return new ResponseEntity<>(sukanList, HttpStatus.OK);
    } catch (SQLException e) {
        e.printStackTrace(); // Consider logging or throwing a custom exception
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
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


   
}
