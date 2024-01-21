package com.heroku.java.CONTROLLER.sidebar;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
@Controller
public class SideBarControllerTeacher {

    @GetMapping("/dashboardTeacher")
      public String dashboardTeacher(@RequestParam(name = "success", required = false) Boolean success, HttpSession session) {
        String teacherUsername = (String) session.getAttribute("teacherUsername");
        return "teacher/dashboardTeacher";
    }

    @GetMapping("/profileTeacher_edit")
    public String editProfileTeacher(HttpSession session) {
        String teacherUsername = (String) session.getAttribute("teacherUsername");
        return "teacher/profileTeacher/profileTeacher_edit";
    }
}
