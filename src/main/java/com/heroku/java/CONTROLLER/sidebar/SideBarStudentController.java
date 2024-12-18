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

import com.heroku.java.MODEL.booking.BookingBean;
import com.heroku.java.MODEL.cafe.CafeBean;
import com.heroku.java.MODEL.student.StudentBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class SideBarStudentController {
    private final DataSource dataSource;

    @Autowired
    public SideBarStudentController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/signupshow")
    public String showSignupPage(Model model) {
        // Create an empty StudentBean to bind form data to it
        StudentBean newStudent = new StudentBean();
        model.addAttribute("student", newStudent);

        return "student/sign-in/signup"; // Redirects to the signup page template
    }

    @GetMapping("/dashboardStudent")
    public String index1(@RequestParam(name = "success", required = false) Boolean success, HttpSession session,
            Model model) {
        String studentNumber = (String) session.getAttribute("studentNumber");
        System.out.println("Session studentNumber in edit profile: " + studentNumber);
        // boolean registered = isStudentRegistered(studentNumber);
        // model.addAttribute("isStudentRegistered", registered);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.student where \"studentNumber\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, studentNumber);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String studentName = resultSet.getString("studentName");
                String studentEmail = resultSet.getString("studentEmail");
                String studentPhone = resultSet.getString("studentphonenumber");
                String studentPassword = resultSet.getString("studentPassword");

                StudentBean s = new StudentBean();

                s.setStudentNumber(studentNumber);
                s.setStudentName(studentName);
                s.setStudentEmail(studentEmail);
                s.setStudentPhone(studentPhone);
                s.setStudentPassword(studentPassword);

                model.addAttribute("s", s);
                session.setAttribute("studentName", studentName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "student/dashboardStudent";
    }

    @GetMapping("/add_Booking")
    public String bookingAdd(@RequestParam(name = "success", required = false) Boolean success, Model model,
                             HttpSession session) {
        String studentNumber = (String) session.getAttribute("studentNumber");
        System.out.println("Session studentNumber in add booking: " + studentNumber);
    
        try {
            if (studentNumber != null) {
                // Create an empty Booking bean for the form
                BookingBean booking = new BookingBean();
                model.addAttribute("booking", booking);
    
                // Adding cafeNumber to the model
                model.addAttribute("cafeNumber", studentNumber);
    
                // Fetch the list of cafeterias from the database
                List<CafeBean> cafeterias = new ArrayList<>();
                try (Connection connection = dataSource.getConnection()) {
                    String sql = "SELECT \"cafeNumber\", \"cafeName\" FROM public.cafeteria_owner";
                    try (PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet resultSet = statement.executeQuery()) {
    
                        while (resultSet.next()) {
                            CafeBean cafe = new CafeBean();
                            cafe.setCafeNumber(resultSet.getString("cafeNumber"));
                            cafe.setCafeName(resultSet.getString("cafeName"));
                            cafeterias.add(cafe);
                        }
                    }
                }
    
                // Add the list of cafeterias to the model
                model.addAttribute("cafeterias", cafeterias);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return "student/order/create_order"; // Adjust the path as necessary
    }
    
    
    @GetMapping("/edit_profile")

    public String editProfile(@RequestParam(name = "success", required = false) Boolean success, HttpSession session,
            Model model) {
        String studentNumber = (String) session.getAttribute("studentNumber");
        System.out.println("Session studentNumber in edit profile: " + studentNumber);

        if (studentNumber == null) {
            return "redirect:/error"; // or handle this case as needed
        }

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM public.student WHERE \"studentNumber\"=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        StudentBean s = new StudentBean();
                        s.setStudentNumber(studentNumber);
                        s.setStudentName(resultSet.getString("studentName"));
                        s.setStudentEmail(resultSet.getString("studentEmail"));
                        s.setStudentPhone(resultSet.getString("studentphonenumber"));
                        s.setStudentPassword(resultSet.getString("studentPassword"));

                        model.addAttribute("s", s);
                        session.setAttribute("studentName", s.getStudentName());
                    } else {
                        System.out.println("No student found for student number: " + studentNumber);
                        return "redirect:/error"; // Handle the case where no student is found
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error"; // Handle exceptions appropriately
        }

        return "student/profile/edit_profile";
    }

    @GetMapping("/cafeteria_owners")
    public String listCafeteriaOwners(Model model) {
        List<CafeBean> ownersList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            // Query to fetch all cafeteria owners
            String sql = "SELECT * FROM public.cafeteria_owner"; // Adjust table name if needed
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                
                // Process the result set
                while (resultSet.next()) {
                    CafeBean owner = new CafeBean();
                    owner.setCafeName(resultSet.getString("cafeName"));
                    owner.setCafeNumber(resultSet.getString("cafeNumber"));
                    owner.setCafeEmail(resultSet.getString("cafeEmail"));
                    owner.setCafePhone(resultSet.getString("phoneNumber"));

                    ownersList.add(owner);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log any errors
        }

        // Pass the list of cafeteria owners to the model
        model.addAttribute("ownersList", ownersList);
        return "student/dashboardStudent"; // View file name
    }
}
