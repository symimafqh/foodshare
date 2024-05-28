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

import com.heroku.java.MODEL.student.StudentBean;
import com.heroku.java.MODEL.teacher.TeacherBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class SideBarAdminController {
   private final DataSource dataSource;

    @Autowired
    public SideBarAdminController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/dashboardAdmin")
    public String dashboardAdmin(@RequestParam(name = "success", required = false) Boolean success, Model model, HttpSession session) {
    //   String teacherUsername = (String) session.getAttribute("teacherUsername");
    String teacherUsername = (String) session.getAttribute("teacherUsername");
    try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT * FROM public.teacher where teacherusername=?";
        final var statement = connection.prepareStatement(sql);
        statement.setString(1, teacherUsername);
        final var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String teacherName = resultSet.getString("teacherName");
            String teacherEmail = resultSet.getString("teacherEmail");
            String teacherPhone = resultSet.getString("teacherPhone");
            String teacherDOB = resultSet.getString("teacherDOB");
            String teacherGender = resultSet.getString("teacherGender");
            String teacherRole= resultSet.getString("teacherRole");
            String teacherAddress = resultSet.getString("teacherAddress");
            String teacherPassword = resultSet.getString("teacherPassword");

            TeacherBean t = new TeacherBean();

            t.setTeacherUsername(teacherUsername);
            t.setTeacherName(teacherName);
            t.setTeacherEmail(teacherEmail);
            t.setTeacherPhone(teacherPhone);
            t.setTeacherDOB(teacherDOB);
            t.setTeacherGender(teacherGender);
            t.setTeacherRole(teacherRole);
            t.setTeacherAddress(teacherAddress);
            t.setTeacherPassword(teacherPassword);

            model.addAttribute("t", t);
            session.setAttribute("teacherName",teacherName);
            connection.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
      return "teacher/dashboardAdmin";
  }

  @GetMapping("/profileAdmin")
  public String editProfileAdmin(@RequestParam(name = "success", required = false) Boolean success, Model model, HttpSession session) {
      String teacherUsername = (String) session.getAttribute("teacherUsername");
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.teacher where teacherusername=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, teacherUsername);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String teacherName = resultSet.getString("teacherName");
                String teacherEmail = resultSet.getString("teacherEmail");
                String teacherPhone = resultSet.getString("teacherPhone");
                String teacherDOB = resultSet.getString("teacherDOB");
                String teacherGender = resultSet.getString("teacherGender");
                String teacherRole= resultSet.getString("teacherRole");
                String teacherAddress = resultSet.getString("teacherAddress");
                String teacherPassword = resultSet.getString("teacherPassword");

                TeacherBean t = new TeacherBean();

                t.setTeacherUsername(teacherUsername);
                t.setTeacherName(teacherName);
                t.setTeacherEmail(teacherEmail);
                t.setTeacherPhone(teacherPhone);
                t.setTeacherDOB(teacherDOB);
                t.setTeacherGender(teacherGender);
                t.setTeacherRole(teacherRole);
                t.setTeacherAddress(teacherAddress);
                t.setTeacherPassword(teacherPassword);

                model.addAttribute("t", t);
                session.setAttribute("teacherName",teacherName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "teacher/profileTeacher/profileAdmin";
      
  }

  //ADD TEACHER ACCOUNT
  @PostMapping("/addTeacherAccount")
    public String registerTeacher(@ModelAttribute("addTeacherAccount")TeacherBean t){
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO public.teacher(teacherid, teachername, teacheremail, teacherphone, teacherdob, teacherrole, teachergender, teacheraddress, teacherusername, teacherpassword) VALUES(?,?,?,?,?,?,?,?,?,?)";
            final var statement = connection.prepareStatement(sql);

            String teacherid=t.getTeacherID();
            String name= t.getTeacherName();
            String email=t.getTeacherEmail();
            String phone=t.getTeacherPhone();
            String role=t.getTeacherRole();
            String dob=t.getTeacherDOB();
            String gender=t.getTeacherGender();
            String username =t.getTeacherUsername();
            String address=t.getTeacherAddress();
            String password =t.getTeacherPassword();
    
            statement.setString(1,teacherid);
            statement.setString(2,name);
            statement.setString(3,email);
            statement.setString(4,phone);
            statement.setString(5,dob);
            statement.setString(6,role);
            statement.setString(7,gender);
            statement.setString(8,address);
            statement.setString(9,username);
            statement.setString(10,password);
            
            statement.executeUpdate();
            
            // System.out.println("type : "+protype);
            // System.out.println("product price : RM"+proprice);
            // System.out.println("proimg: "+proimgs.getBytes());
            
            connection.close();
                
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/teacherRegister";
                }
                
            return "redirect:/ListAccountTeacher";
    }
  //END OF ADD TEACHER ACCOUNT

      //ADD STUDENT ACCOUNT
      @PostMapping("/addStudentAccount")
      public String registerStudent(@ModelAttribute("addStudentAccount")StudentBean s){
          try {
              Connection connection = dataSource.getConnection();
              String sql = "INSERT INTO public.student(studentic, studentname, studentemail, studentphone, studentdob, studentgender, studentclass, studentaddress, studentpassword) VALUES(?,?,?,?,?,?,?,?,?)";
              final var statement = connection.prepareStatement(sql);
  
              String studIC= s.getStudentIC();
              String name= s.getStudentName();
              String email=s.getStudentEmail();
              String phone=s.getStudentPhone();
              String dob=s.getStudentDOB();
              String gender=s.getStudentGender();
              String kelas=s.getStudentClass();
              String address=s.getStudentAddress();
              String password =s.getStudentPassword();
      
              statement.setString(1,studIC);
              statement.setString(2,name);
              statement.setString(3,email);
              statement.setString(4,phone);
              statement.setString(5,dob);
              statement.setString(6,gender);
              statement.setString(7,kelas);
              statement.setString(8,address);
              statement.setString(9,password);
              
              statement.executeUpdate();
              
              // System.out.println("type : "+protype);
              // System.out.println("product price : RM"+proprice);
              // System.out.println("proimg: "+proimgs.getBytes());
              
              connection.close();
                  
                  } catch (Exception e) {
                      e.printStackTrace();
                      return "redirect:/signup";
                  }
              return "redirect:/ListAccountStudent";
      }
  //END OF STUDENT ACCOUNT
  
  // LIST ACCOUNT STUDENTS
   @GetMapping("/ListAccountStudent")
    public String studentAccountList(Model model) {

        List<StudentBean> student = new ArrayList<StudentBean>();

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM student order by studentname";
            final var statement = connection.prepareStatement(sql);
            //statement.setString(1, "baker"); (syahir punya nih)
            final var resultSet = statement.executeQuery();
           

            while (resultSet.next()) {
                String studentIC = resultSet.getString("studentIC");
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

                student.add(s);
                model.addAttribute("student", student);
                //model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya gak)

            }

            connection.close();

        return "account/ListAccountStudent";
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as desired (e.g., show an error message)
            return "error";
        }
        
    }

    @GetMapping("/viewStudentDetails")
    public String viewStudentDetails(@RequestParam("studentIC") String studentIC, HttpSession session, Model model) {
        String teacherUsername = (String) session.getAttribute("teacherUsername");
        System.out.println("IC Number : " + studentIC);
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

        return "account/viewStudentDetails";
    }

    @GetMapping("/DeleteStudentAccount")
    public String DeleteStudentAccount(HttpSession session, Model model, @RequestParam("studentIC") String studentIC) {
        //String studentIC = (String) session.getAttribute("studentIC");
        String studentName = (String) session.getAttribute("studentName");
        String studentEmail = (String) session.getAttribute("studentEmail");
        String studentPhone = (String) session.getAttribute("studentPhone");
        String studentDOB = (String) session.getAttribute("studentDOB");
        String studentGender = (String) session.getAttribute("studentGender");
        String studentClass = (String) session.getAttribute("studentClass");
        String studentAddress = (String) session.getAttribute("studentAddress");
        String studentPassword = (String) session.getAttribute("studentPassword");

    try (Connection connection = dataSource.getConnection()) {
        //start transaction
        connection.setAutoCommit(false);

        //Delete from child table (registration table)
        String deleteFromChildQuery = "DELETE FROM public.registration WHERE studentic = ?";
        final var deleteFromChildStatement = connection.prepareStatement(deleteFromChildQuery);
        deleteFromChildStatement.setString(1, studentIC);
        deleteFromChildStatement.executeUpdate();

        // Delete student from the students account list
        final var DeleteStudentAccount = connection.prepareStatement("DELETE FROM public.student WHERE studentic = ?");
        DeleteStudentAccount.setString(1, studentIC);
        DeleteStudentAccount.executeUpdate();

        connection.commit();//commit the transaction
        System.out.println("Student account successfully deleted");
    } catch (Exception e) {
        System.out.println("Failed to delete student from the student account list");
        e.printStackTrace();
    }
    return "redirect:/ListAccountStudent";
}
// END OF LIST ACCOUNT STUDENT

//LIST OF ACCOUNT TEACHER
@GetMapping("/ListAccountTeacher")
public String teacherAccountList(Model model) {

    List<TeacherBean> teacher = new ArrayList<TeacherBean>();

    try (Connection connection = dataSource.getConnection()) {
        String sql = "SELECT * FROM teacher order by teacherid";
        final var statement = connection.prepareStatement(sql);
        //statement.setString(1, "baker"); (syahir punya nih)
        final var resultSet = statement.executeQuery();
       

        while (resultSet.next()) {
            String teacherID = resultSet.getString("teacherID");
            String teacherUsername = resultSet.getString("teacherUsername");
            String teacherName = resultSet.getString("teacherName");
            String teacherEmail = resultSet.getString("teacherEmail");
            String teacherPhone = resultSet.getString("teacherPhone");
            String teacherDOB = resultSet.getString("teacherDOB");
            String teacherGender = resultSet.getString("teacherGender");
           String teacherRole = resultSet.getString("teacherRole");
           String teacherAddress = resultSet.getString("teacherAddress");
           String teacherPassword = resultSet.getString("teacherPassword");
            
            TeacherBean t = new TeacherBean();
            t.setTeacherID(teacherID);
            t.setTeacherUsername(teacherUsername);
            t.setTeacherName(teacherName);
            t.setTeacherEmail(teacherEmail);
            t.setTeacherPhone(teacherPhone);
            t.setTeacherGender(teacherGender);  
            t.setTeacherRole(teacherRole);
            t.setTeacherAddress(teacherAddress);
            t.setTeacherPassword(teacherPassword); 
            t.setTeacherDOB(teacherDOB);             

            teacher.add(t);
            model.addAttribute("teacher", teacher);
            //model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to the modelF (syahir punya gak)

        }

        connection.close();

    return "account/ListAccountTeacher";
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the exception as desired (e.g., show an error message)
        return "error";
    }
    
}

    @GetMapping("/viewTeacherDetails")
public String viewTeacherDetails(@RequestParam("teacherID") String teacherID, HttpSession session, Model model) {
    //String teacherUsername = (String) session.getAttribute("teacherUsername");
    System.out.println("ID Number : " + teacherID);
    try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT * FROM public.teacher where teacherid=?";
        final var statement = connection.prepareStatement(sql);
        statement.setString(1, teacherID);
        final var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String teacherUsername = resultSet.getString("teacherUsername");
            String teacherName = resultSet.getString("teacherName");
            String teacherEmail = resultSet.getString("teacherEmail");
            String teacherPhone = resultSet.getString("teacherPhone");
            String teacherDOB = resultSet.getString("teacherDOB");
            String teacherGender = resultSet.getString("teacherGender");
           String teacherRole = resultSet.getString("teacherRole");
           String teacherAddress = resultSet.getString("teacherAddress");
           String teacherPassword = resultSet.getString("teacherPassword");

           TeacherBean t = new TeacherBean();

           t.setTeacherID(teacherID);
           t.setTeacherUsername(teacherUsername);
           t.setTeacherName(teacherName);
           t.setTeacherEmail(teacherEmail);
           t.setTeacherPhone(teacherPhone);
           t.setTeacherGender(teacherGender);  
           t.setTeacherRole(teacherRole);
           t.setTeacherAddress(teacherAddress);
           t.setTeacherPassword(teacherPassword); 
           t.setTeacherDOB(teacherDOB);   

            model.addAttribute("t", t);

            connection.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return "account/viewTeacherDetails";
}

@GetMapping("/DeleteTeacherAccount")
public String DeleteTeacherAccount(HttpSession session, Model model, @RequestParam("teacherID") String teacherID) {

    String teacherUsername = (String) session.getAttribute("teacherUsername");
    String teacherName = (String) session.getAttribute("teacherName");
    String teacherEmail = (String) session.getAttribute("teacherEmail");
    String teacherPhone = (String) session.getAttribute("teacherPhone");
    String teacherDOB = (String) session.getAttribute("teacherDOB");
    String teacherGender = (String) session.getAttribute("teacherGender");
    String teacherClass = (String) session.getAttribute("teacherClass");
    String teacherAddress = (String) session.getAttribute("teacherAddress");
    String teacherPassword = (String) session.getAttribute("teacherPassword");

try (Connection connection = dataSource.getConnection()) {
    // Delete student from the students account list
    final var DeleteTeacherAccount = connection.prepareStatement("DELETE FROM public.teacher WHERE teacherid = ?");
    DeleteTeacherAccount.setString(1, teacherID);
    DeleteTeacherAccount.executeUpdate();

    System.out.println("Student account successfully deleted");
} catch (Exception e) {
    System.out.println("Failed to delete teacher from the teacher account list");
    e.printStackTrace();
}
return "redirect:/ListAccountTeacher";
}
//END OF LIST ACCOUNT TEACHER

//ACTIVITY CONTROLLER
//---------------------------LIST SUKAN------------------------------//
    @GetMapping("/AddNewSukan")
    public String sukanList(Model model) {
        List<SukanBean> sukanList = new ArrayList<SukanBean>();
    
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
    
            // Use the Model object to pass the list to the view
            model.addAttribute("sukanList", sukanList);
    
            return "teacher/activity/AddNewSukan"; // Return the view name as a String
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging or throwing a custom exception
            return "error"; // Replace with an appropriate error view name
        }
    }
    
//---------------------------LIST UNIT------------------------------//
@GetMapping("/addNewUnit")
public String unitList(Model model) {
    List<UnitBean> unitList = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
        // create statement
        final var statement = connection.createStatement();
        String sql = "SELECT DISTINCT a.activityid, a.activityname, s.uniforminformation, s.uniformquota " +
                     "FROM activity a JOIN uniform s ON a.activityid = s.activityid";

        try (ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                UnitBean unit = new UnitBean();
                ActivityBean activity = new ActivityBean();

                activity.setActivityID(resultSet.getInt("activityID"));
                activity.setActivityName(resultSet.getString("activityName"));

                unit.setActivity(activity);
                unit.setInfoUnit(resultSet.getString("uniforminformation"));
                unit.setQuotaUnit(resultSet.getInt("uniformquota"));

                unitList.add(unit);
            }
        }
        connection.close();

        // Use the Model object to pass the list to the view
        model.addAttribute("unitList", unitList);

        return "teacher/activity/addNewUnit";
    } catch (SQLException e) {
        // Log the exception using a logging framework like SLF4J
        e.printStackTrace(); // Consider logging or throwing a custom exception
        return "error"; // Replace with an appropriate error view name
    }
}

//---------------------------LIST club------------------------------//
@GetMapping("/AddNewClub")
public String clubList(Model model) {
    List<ClubBean> clubList = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
        // create statement
        final var statement = connection.createStatement();
        String sql = "SELECT DISTINCT a.activityid, a.activityname, s.clubinformation, s.clubquota " +
                     "FROM activity a JOIN club s ON a.activityid = s.activityid";

        try (ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                ClubBean club = new ClubBean();
                ActivityBean activity = new ActivityBean();

                activity.setActivityID(resultSet.getInt("activityID"));
                activity.setActivityName(resultSet.getString("activityName"));

                club.setActivity(activity);
                club.setInfoClub(resultSet.getString("clubInformation"));
                club.setQuotaClub(resultSet.getInt("clubQuota"));

                clubList.add(club);
            }
        }
        connection.close();

        // Use the Model object to pass the list to the view
        model.addAttribute("clubList", clubList);

        return "teacher/activity/AddNewClub";
    } catch (SQLException e) {
        // Log the exception using a logging framework like SLF4J
        e.printStackTrace(); // Consider logging or throwing a custom exception
        return "error"; // Replace with an appropriate error view name
    }
}

//---------------------------ADD SUKAN------------------------------//
@GetMapping("/addSukann")
    public String AddNewSukan() {
        return "teacher/activity/AddNewSukan";
    }

    @PostMapping("/AddNewSukan")
    public String AddNewSukan(@RequestParam String namaSukan, @RequestParam String info, @RequestParam Integer quota, Model model, @ModelAttribute("AddNewSukan") SukanBean sukanBean, ActivityBean activityBean, HttpSession session) {
        String teacherID = (String) session.getAttribute("teacherID");
        System.out.println("ID Number : " + teacherID);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO activity(activityname, teacherid) VALUES (?, ?)";
            final var statement = connection.prepareStatement(sql);

            String activity = namaSukan;
            // String TeacherID =activityBean.getTeacherID();
           

            statement.setString(1, activity);
            statement.setString(2, teacherID);

            statement.executeUpdate();

            // Get id from database for sql 2 from sql 1
            ActivityBean parent = new ActivityBean();
            Integer parentActivity = parent.getMaxActivityID();
            // String sportsInfo = sukanBean.getInfoSukan();
            // Integer sportsQuota = sukanBean.getQuotaSukan();
            String sportsInfo = info;
            Integer sportsQuota = quota;


            System.out.println("activityID from PARENT: " + parentActivity);
                
                    String child = "INSERT INTO sport (activityid, activityname, teacherid, sportinformation, sportquota) VALUES (?, ?, ?, ?, ?)";
                    final var sportStatement = connection.prepareStatement(child);
                    sportStatement.setInt(1, parentActivity);
                    sportStatement.setString(2, activity);
                    sportStatement.setString(3, teacherID);
                    sportStatement.setString(4, sportsInfo);
                    sportStatement.setInt(5, sportsQuota);

                    sportStatement.executeUpdate();
               
                model.addAttribute("success", true);

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/AddNewSukan?success=false";
        }

        return "redirect:/AddNewSukan?success=true";
    }


    //---------------------------ADD CLUB------------------------------//
@GetMapping("/addClubb")
public String AddNewClub() {
    return "teacher/activity/AddNewClub";
}

@PostMapping("/AddNewClub")
public String AddNewClub(@RequestParam String namaClub, @RequestParam String info, @RequestParam Integer quota, Model model, @ModelAttribute("AddNewClub") ClubBean sukanBean, ActivityBean activityBean, HttpSession session) {
    String teacherID = (String) session.getAttribute("teacherID");
    System.out.println("ID Number : " + teacherID);
    try {
        Connection connection = dataSource.getConnection();
        String sql = "INSERT INTO activity(activityname, teacherid) VALUES (?, ?)";
        final var statement = connection.prepareStatement(sql);

        String activity = namaClub;
        // String TeacherID =activityBean.getTeacherID();
       

        statement.setString(1, activity);
        statement.setString(2, teacherID);

        statement.executeUpdate();

        // Get id from database for sql 2 from sql 1
        ActivityBean parent = new ActivityBean();
        Integer parentActivity = parent.getMaxActivityID();
        // String sportsInfo = sukanBean.getInfoClub();
        // Integer sportsQuota = sukanBean.getQuotaClub();
        String clubInfo = info;
        Integer clubQuota = quota;


        System.out.println("activityID from PARENT: " + parentActivity);
            
                String child = "INSERT INTO club (activityid, activityname, teacherid, clubinformation, clubquota) VALUES (?, ?, ?, ?, ?)";
                final var clubStatement = connection.prepareStatement(child);
                clubStatement.setInt(1, parentActivity);
                clubStatement.setString(2, activity);
                clubStatement.setString(3, teacherID);
                clubStatement.setString(4, clubInfo);
                clubStatement.setInt(5, clubQuota);

                clubStatement.executeUpdate();
           
            model.addAttribute("success", true);

        connection.close();

    } catch (SQLException e) {
        e.printStackTrace();
        return "redirect:/AddNewClub?success=false";
    }

    return "redirect:/AddNewClub?success=true";
}

    //---------------------------ADD Unit------------------------------//
    @GetMapping("/addUnitt")
    public String AddNewUnit() {
        return "teacher/activity/addNewUnit";
    }
    
    @PostMapping("/AddNewUnit")
    public String AddNewUnit(@RequestParam String namaUnit, @RequestParam String info, @RequestParam Integer quota, Model model, @ModelAttribute("AddNewUnit") UnitBean sukanBean, ActivityBean activityBean, HttpSession session) {
        String teacherID = (String) session.getAttribute("teacherID");
        System.out.println("ID Number : " + teacherID);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO activity(activityname, teacherid) VALUES (?, ?)";
            final var statement = connection.prepareStatement(sql);
    
            String activity = namaUnit;
            // String TeacherID =activityBean.getTeacherID();
           
    
            statement.setString(1, activity);
            statement.setString(2, teacherID);
    
            statement.executeUpdate();
    
            // Get id from database for sql 2 from sql 1
            ActivityBean parent = new ActivityBean();
            Integer parentActivity = parent.getMaxActivityID();
            // String sportsInfo = sukanBean.getInfoUnit();
            // Integer sportsQuota = sukanBean.getQuotaUnit();
            String unitInfo = info;
            Integer unitQuota = quota;
    
    
            System.out.println("activityID from PARENT: " + parentActivity);
                
                    String child = "INSERT INTO uniform (activityid, activityname, teacherid, uniforminformation, uniformquota) VALUES (?, ?, ?, ?, ?)";
                    final var unitStatement = connection.prepareStatement(child);
                    unitStatement.setInt(1, parentActivity);
                    unitStatement.setString(2, activity);
                    unitStatement.setString(3, teacherID);
                    unitStatement.setString(4, unitInfo);
                    unitStatement.setInt(5, unitQuota);
    
                    unitStatement.executeUpdate();
               
                model.addAttribute("success", true);
    
            connection.close();
    
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/addNewUnit?success=false";
        }
    
        return "redirect:/addNewUnit?success=true";
    }
    
    //---------------------------UPDATE SUKAN------------------------------//
    @GetMapping("/UpdateSukan")
public String UpdateSukan( @RequestParam("activityID") int activityid,Model model, HttpSession session) {
    //Integer activityid = (Integer) session.getAttribute("activityID");
    System.out.println("activity id "+activityid);

    SukanBean sukan = new SukanBean(); // Instantiate SukanBean
    ActivityBean activity = new ActivityBean(); // Instantiate ActivityBean

    try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT DISTINCT a.activityid, a.activityname, s.sportinformation, s.sportquota " +
            "FROM activity a JOIN sport s ON a.activityid = s.activityid WHERE a.activityid = ?";
        final var statement = connection.prepareStatement(sql);
        statement.setInt(1, activityid);
        final var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String activityName = resultSet.getString("activityname");
            String infoSukan = resultSet.getString("sportinformation");
            int quotaSukan = resultSet.getInt("sportquota");

            // SukanBean sukan = new SukanBean(); // Instantiate SukanBean
            // ActivityBean activity = new ActivityBean(); // Instantiate ActivityBean

            System.out.println("Sukan object: " + sukan);
            // Set the values to the Sukan object
            sukan.setActivityID(activityid);
            sukan.setNamaSukan(activityName);
            sukan.setInfoSukan(infoSukan);
            sukan.setQuotaSukan(quotaSukan);

            // model.addAttribute("sukan", sukan);
            // model.addAttribute("activity", activity);

            connection.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    model.addAttribute("sukan", sukan);
    model.addAttribute("activity", activity);


    return "teacher/activity/UpdateSukan";
}


    @PostMapping("/UpdateSukan")
    public String UpdateSukan(@RequestParam("activityID") int activityid, @RequestParam("namaSukan") String activityName, @RequestParam("info") String info, @RequestParam("quota") Integer quota, ActivityBean ab, SukanBean sb, Model model) {
        
        //int activity = activityId;
        //String unitName = activityName;

        System.out.println(activityid);
        System.out.println(activityName);
        System.out.println(info);
        System.out.println(quota);
	    try {
            Connection con = dataSource.getConnection();
	        String sql1 = "UPDATE ACTIVITY SET ACTIVITYNAME = ? WHERE ACTIVITYID = ?";
	        try (PreparedStatement ps1 = con.prepareStatement(sql1)) {
	            ps1.setString(1, activityName);
	            ps1.setInt(2, activityid);
	            ps1.executeUpdate();
	            System.out.println("Successfully updated activity table "+activityid);
	        }

	        String sql2 = "UPDATE SPORT SET SPORTINFORMATION=?, SPORTQUOTA=?  WHERE ACTIVITYID = ?";
	        try (PreparedStatement ps2 = con.prepareStatement(sql2)) {
	            ps2.setString(1, info);
	            ps2.setInt(2, quota); // Assuming getQuotaSukan() returns an integer
	            ps2.setInt(3, activityid);
	            ps2.executeUpdate();
	            System.out.println("Successfully updated sport table");
	        }
            model.addAttribute("success", true);
    
            con.close();

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Handle the exception as needed
            return "redirect:/AddNewSukan?success=false";
	    }   
        return "redirect:/AddNewSukan?success=true";
    }
    
    
//---------------------------UPDATE CLUB------------------------------//
@GetMapping("/UpdateClub")
public String UpdateClub( @RequestParam("activityID") int activityid,Model model, HttpSession session) {
    //Integer activityid = (Integer) session.getAttribute("activityID");
    System.out.println("activity id "+activityid);

    ClubBean club = new ClubBean(); // Instantiate ClubBean
    ActivityBean activity = new ActivityBean(); // Instantiate ActivityBean

    try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT DISTINCT a.activityid, a.activityname, s.clubinformation, s.clubquota " +
            "FROM activity a JOIN club s ON a.activityid = s.activityid WHERE a.activityid = ?";
        final var statement = connection.prepareStatement(sql);
        statement.setInt(1, activityid);
        final var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String activityName = resultSet.getString("activityname");
            String infoClub = resultSet.getString("clubinformation");
            int quotaClub = resultSet.getInt("clubquota");

            // ClubBean sukan = new ClubBean(); // Instantiate ClubBean
            // ActivityBean activity = new ActivityBean(); // Instantiate ActivityBean

            System.out.println("Club object: " + club);
            // Set the values to the Club object
            club.setActivityID(activityid);
            club.setNamaClub(activityName);
            club.setInfoClub(infoClub);
            club.setQuotaClub(quotaClub);

            // model.addAttribute("sukan", sukan);
            // model.addAttribute("activity", activity);

            connection.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    model.addAttribute("club", club);
    model.addAttribute("activity", activity);


    return "teacher/activity/UpdateClub";
}


    @PostMapping("/UpdateClub")
    public String UpdateClub(@RequestParam("activityID") int activityid, @RequestParam("namaClub") String activityName, @RequestParam("info") String info, @RequestParam("quota") Integer quota, ActivityBean ab, ClubBean sb, Model model) {
        
        //int activity = activityId;
        //String unitName = activityName;

        System.out.println(activityid);
        System.out.println(activityName);
        System.out.println(info);
        System.out.println(quota);
	    try {
            Connection con = dataSource.getConnection();
	        String sql1 = "UPDATE ACTIVITY SET ACTIVITYNAME = ? WHERE ACTIVITYID = ?";
	        try (PreparedStatement ps1 = con.prepareStatement(sql1)) {
	            ps1.setString(1, activityName);
	            ps1.setInt(2, activityid);
	            ps1.executeUpdate();
	            System.out.println("Successfully updated activity table "+activityid);
	        }

	        String sql2 = "UPDATE CLUB SET CLUBINFORMATION=?, CLUBQUOTA=?  WHERE ACTIVITYID = ?";
	        try (PreparedStatement ps2 = con.prepareStatement(sql2)) {
	            ps2.setString(1, info);
	            ps2.setInt(2, quota); // Assuming getQuotaClub() returns an integer
	            ps2.setInt(3, activityid);
	            ps2.executeUpdate();
	            System.out.println("Successfully updated club table");
	        }
            model.addAttribute("success", true);
    
            con.close();

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Handle the exception as needed
            return "redirect:/AddNewClub?success=false";
	    } 
        return "redirect:/AddNewClub?success=true";
    }
//---------------------------UPDATE UNIT------------------------------//
@GetMapping("/UpdateUnit")
public String UpdateUnit( @RequestParam("activityID") int activityid,Model model, HttpSession session) {
    //Integer activityid = (Integer) session.getAttribute("activityID");
    System.out.println("activity id "+activityid);

    UnitBean unit = new UnitBean(); // Instantiate UnitBean
    ActivityBean activity = new ActivityBean(); // Instantiate ActivityBean

    try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT DISTINCT a.activityid, a.activityname, s.uniforminformation, s.uniformquota " +
            "FROM activity a JOIN uniform s ON a.activityid = s.activityid WHERE a.activityid = ?";
        final var statement = connection.prepareStatement(sql);
        statement.setInt(1, activityid);
        final var resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String activityName = resultSet.getString("activityname");
            String infoUnit = resultSet.getString("uniforminformation");
            int quotaUnit = resultSet.getInt("uniformquota");

            // UnitBean sukan = new UnitBean(); // Instantiate UnitBean
            // ActivityBean activity = new ActivityBean(); // Instantiate ActivityBean

            System.out.println("Unit object: " + unit);
            // Set the values to the Unit object
            unit.setActivityID(activityid);
            unit.setNamaUnit(activityName);
            unit.setInfoUnit(infoUnit);
            unit.setQuotaUnit(quotaUnit);

            // model.addAttribute("sukan", unit);
            // model.addAttribute("activity", activity);

            connection.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    model.addAttribute("unit", unit);
    model.addAttribute("activity", activity);


    return "teacher/activity/UpdateUnit";
}


    @PostMapping("/UpdateUnit")
    public String UpdateUnit(@RequestParam("activityID") int activityid, @RequestParam("namaUnit") String activityName, @RequestParam("info") String info, @RequestParam("quota") Integer quota, ActivityBean ab, UnitBean sb, Model model) {
        
        //int activity = activityId;
        //String unitName = activityName;

        System.out.println(activityid);
        System.out.println(activityName);
        System.out.println(info);
        System.out.println(quota);
	    try {
            Connection con = dataSource.getConnection();
	        String sql1 = "UPDATE ACTIVITY SET ACTIVITYNAME = ? WHERE ACTIVITYID = ?";
	        try (PreparedStatement ps1 = con.prepareStatement(sql1)) {
	            ps1.setString(1, activityName);
	            ps1.setInt(2, activityid);
	            ps1.executeUpdate();
	            System.out.println("Successfully updated activity table "+activityid);
	        }

	        String sql2 = "UPDATE UNIFORM SET UNIFORMINFORMATION=?, UNIFORMQUOTA=?  WHERE ACTIVITYID = ?";
	        try (PreparedStatement ps2 = con.prepareStatement(sql2)) {
	            ps2.setString(1, info);
	            ps2.setInt(2, quota); // Assuming getQuotaUnit() returns an integer
	            ps2.setInt(3, activityid);
	            ps2.executeUpdate();
	            System.out.println("Successfully updated UNIFROM table");
	        }
            model.addAttribute("success", true);
    
            con.close();

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Handle the exception as needed
            return "redirect:/addNewUnit?success=false";
	    }   
        return "redirect:/addNewUnit?success=true";
    }



    interface Bean {
        int getActivityID();
        void setActivityID(int activityID);
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

    //---------------------------UNIT BEAN------------------------------//
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

    //---------------------------CLUB BEAN------------------------------//
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

    

    //---------------------------ACTIVITY BEAN------------------------------//
    public class ActivityBean implements Bean{
	
        private String activityName;
        private String TeacherID;
        private  int activityID;

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
//END OF ACTIVITY CONTROLLER
}
