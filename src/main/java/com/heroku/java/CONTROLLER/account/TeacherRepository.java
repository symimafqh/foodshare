package com.heroku.java.CONTROLLER.account;

import com.heroku.java.MODEL.teacher.CafeBean;

public interface TeacherRepository {
    CafeBean getTeacherById(String username);
}
