package com.heroku.java.CONTROLLER.account;

import com.heroku.java.MODEL.teacher.TeacherBean;

public interface TeacherRepository {
    TeacherBean getTeacherById(String username);
}
