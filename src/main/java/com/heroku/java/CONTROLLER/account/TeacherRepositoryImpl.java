// package com.heroku.java.CONTROLLER.account;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Repository;

// import com.heroku.java.MODEL.teacher.CafeBean;

// import javax.sql.DataSource;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;

// @Repository
// public class TeacherRepositoryImpl implements TeacherRepository {

//     @Autowired
//     private DataSource dataSource;

//     @Override
//     public CafeBean getTeacherById(String username) {
//         CafeBean teacher = new CafeBean();
//         try {
//             Connection connection = dataSource.getConnection();

//             PreparedStatement ps = connection.prepareStatement("SELECT * FROM TEACHER WHERE teacherUsername=?");
//             ps.setString(1, username);

//             ResultSet rs = ps.executeQuery();
//             if (rs.next()) {
//                 teacher.setTeacherUsername(rs.getString("teacherUsername"));
//                 teacher.setTeacherID(rs.getString("teacherID"));
//                 teacher.setTeacherName(rs.getString("teacherName"));
//                 teacher.setTeacherEmail(rs.getString("teacherEmail"));
//                 teacher.setTeacherPhone(rs.getString("teacherPhone"));
//                 teacher.setTeacherDOB(rs.getString("teacherDOB"));
//                 teacher.setTeacherGender(rs.getString("teacherGender"));
//                 teacher.setTeacherPassword(rs.getString("teacherPassword"));
//                 teacher.setTeacherAddress(rs.getString("teacherAddress"));
//                 teacher.setTeacherRole(rs.getString("teacherRole"));
//             }
//             connection.close();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return teacher;
//     }
// }
