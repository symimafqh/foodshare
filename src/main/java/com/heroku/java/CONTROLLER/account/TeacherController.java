package com.heroku.java.CONTROLLER.account;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heroku.java.MODEL.teacher.TeacherBean;

@Controller
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("/viewDetails")
    public ResponseEntity<TeacherBean> viewTeacherDetails(@RequestParam("teacherUsername") String teacherUsername) {
        try {
            TeacherBean teacher = teacherRepository.getTeacherById(teacherUsername);

            if (teacher != null) {
                TeacherBean teacherBean = new TeacherBean();
                teacherBean.setTeacherID(teacher.getTeacherID());
                teacherBean.setTeacherUsername(teacher.getTeacherUsername());
                teacherBean.setTeacherName(teacher.getTeacherName());
                // Set other properties...

                return new ResponseEntity<>(teacherBean, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}