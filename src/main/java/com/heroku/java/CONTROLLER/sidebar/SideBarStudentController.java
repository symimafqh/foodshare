package com.heroku.java.CONTROLLER.sidebar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

public class SideBarStudentController {
     @GetMapping("/dashboardStudent")
      public String index1(@RequestParam(name = "success", required = false) Boolean success, HttpSession session) {
        String studentIC = (String) session.getAttribute("studentIC");
        return "student/dashboardStudent";
    }

    @GetMapping("/edit_profile")
    public String editProfile(HttpSession session) {
        String studentIC = (String) session.getAttribute("studentIC");
        return "student/profile/edit_profile";
    }

    @GetMapping("/semasa")
    public String semasa() {
        return "semasa";    
    }

    @GetMapping("/pendaftaran")
    public String pendaftaran() {
        return "pendaftaran";
    }
    
    @GetMapping("/semakan")
    public String semakan() {
        return "semakan";
    }  
    
    @GetMapping("/info_unit")
    public String infoUnit() {
        return "info_unit";
    }

    @GetMapping("/info_kelab")
    public String infoKelab() {
        return "info_kelab";
    }
    
    @GetMapping("/info_sukan") 
    public String infoSukan() {
        return "info_sukan";
    }

}
