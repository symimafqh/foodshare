package com.heroku.java.CONTROLLER.account;

import com.heroku.java.MODEL.cafe.CafeBean;

public interface TeacherRepository {
    CafeBean getTeacherById(String username);
}
