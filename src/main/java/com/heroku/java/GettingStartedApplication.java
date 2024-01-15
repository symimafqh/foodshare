package com.heroku.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

@SpringBootApplication
@Controller
public class GettingStartedApplication {
    private final DataSource dataSource;

    @Autowired
    public GettingStartedApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    //student 
    // @GetMapping("/signin")
    // public String studentsignin() {
    //     return "student/sign-in/signin";
    // }

    @GetMapping("/signup")
    public String signup() {
        return "student/sign-in/signup";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(name = "success", required = false) Boolean success) {
        return "student/dashboard";
    }
    

     @GetMapping("/adminSignin")
    public String coorsignin() {
        return "teacher/sign-in/adminSignin";
    }

    @GetMapping("/teacherRegister")
    public String teacherRegister(){
        return "teacher/sign-in/teacherRegister";
    }

    @GetMapping("/teachersignin")
    public String teacherSignin(){
        return "teacher/sign-in/teachersignin";
    }

    @GetMapping("/AddNewSukan")
    public String addnewsukan(){
        return "teacher/activity/AddNewSukan";
    }

    // @GetMapping("/ListAccountStudent")
    // public String listaccountstudent(){
    //     return "account/ListAccountStudent";
    // }

    //  @GetMapping("/reminder")
    // public String reminder() {
    //     return "sign-in/reminder";
    // }

    // @GetMapping("/dashboardStudent")
    // public String dashboardstudent(){
    //     return "dashboard/dashboardStudent";
    // }

    //  @GetMapping("/edit_profile")
    // public String profileStudent(){
    //     return "profileStud/edit_profile";
    // }

    //    @GetMapping("/semasa")
    // public String semasa(){
    //     return "kokurikulum/semasa";
    // }

    //     @GetMapping("/pendaftaran")
    // public String registration(){
    //     return "kokurikulum/pendaftaran";
    // }

    //      @GetMapping("/semakan")
    // public String resultcheck(){
    //     return "kokurikulum/semakan";
    // }

    //       @GetMapping("/info_kelab")
    // public String info_kelab(){
    //     return "info_cat/info_kelab";
    // }

    //        @GetMapping("/info_sukan")
    // public String info_sukan(){
    //     return "info_cat/info_sukan";
    // }

    //        @GetMapping("/info_unit")
    // public String info_unit(){
    //     return "info_cat/info_unit";
    // }

    //         @GetMapping("/dashboardCoor")
    // public String dashboardCoor(){
    //     return "dashboard/dashboardCoor";
    // }

    //         @GetMapping("/info_unitCoor")
    // public String info_unitCoor(){
    //     return "info_cat/info_unitCoor";
    // }

    //         @GetMapping("/info_sukanCoor")
    // public String info_sukanCoor(){
    //     return "info_cat/info_sukanCoor";
    // }

    //         @GetMapping("/info_kelabCoor")
    // public String info_kelabCoor(){
    //     return "info_cat/info_kelabCoor";
    // }

    @GetMapping("/database")
    String database(Map<String, Object> model) {
        try (Connection connection = dataSource.getConnection()) {
            final var statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
            statement.executeUpdate("INSERT INTO ticks VALUES (now())");

            final var resultSet = statement.executeQuery("SELECT tick FROM ticks");
            final var output = new ArrayList<>();
            while (resultSet.next()) {
                output.add("Read from DB: " + resultSet.getTimestamp("tick"));
            }

            model.put("records", output);
            return "database";

        } catch (Throwable t) {
            model.put("message", t.getMessage());
            return "error";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(GettingStartedApplication.class, args);
    }
}
