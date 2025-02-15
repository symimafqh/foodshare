package com.heroku.java.CONTROLLER.sidebar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.MODEL.booking.BookingBean;
import com.heroku.java.MODEL.cafe.CafeBean;
import com.heroku.java.MODEL.leftover.FoodRequestDetail;
import com.heroku.java.MODEL.leftover.LeftoverBean;
import com.heroku.java.MODEL.pickup.PickupStatusBean;
import com.heroku.java.MODEL.student.StudentBean;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class SideBarControllerCafe {

    private final DataSource dataSource;

    @Autowired
    public SideBarControllerCafe(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     @GetMapping("/cafeRegisterr")
    public String showSignupPage(Model model) {
        // Create an empty StudentBean to bind form data to it
        StudentBean newStudent = new StudentBean();
        model.addAttribute("student", newStudent);

        return "cafeteria_owner/sign-in/ownerRegister"; // Redirects to the signup page template
    }


    @GetMapping("/dashboardCafe")
    public String dashboardCafe(@RequestParam(name = "success", required = false) Boolean success, Model model,
            HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");

        System.out.println("Cafe Number" + cafeNumber);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.cafeteria_owner where \"cafeNumber\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, cafeNumber);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String cafeName = resultSet.getString("cafeName");
                String cafeEmail = resultSet.getString("cafeEmail");
                String cafePassword = resultSet.getString("cafePassword");

                CafeBean t = new CafeBean();

                t.setCafeNumber(cafeNumber);
                t.setCafeName(cafeName);
                t.setCafeEmail(cafeEmail);
                t.setCafePassword(cafePassword);

                model.addAttribute("t", t);
                session.setAttribute("cafeName", cafeName);
                connection.close();
                System.out.println("Cafe Number" + cafeName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/dashboardCafe";
    }

    @GetMapping("/profileCafe_edit")
    public String editProfileCafe(@RequestParam(name = "success", required = false) Boolean success, Model model,
            HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        System.out.println("Masuk side bar controller" + cafeNumber);
        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM public.cafeteria_owner where \"cafeNumber\"=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, cafeNumber);
            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String cafeName = resultSet.getString("cafeName");
                String cafeEmail = resultSet.getString("cafeEmail");
                String cafePassword = resultSet.getString("cafePassword");

                CafeBean t = new CafeBean();

                t.setCafeNumber(cafeNumber);
                t.setCafeName(cafeName);
                t.setCafeEmail(cafeEmail);
                t.setCafePassword(cafePassword);

                model.addAttribute("t", t);
                session.setAttribute("cafeName", cafeName);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/profileCO/profileCafe_edit";
    }

    // Get Mapping for Add Leftover Page
    @GetMapping("/add_Leftover")
    public String leftoverAdd(@RequestParam(name = "success", required = false) Boolean success, Model model,
            HttpSession session) {
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        System.out.println("Masuk side bar controller lagi " + cafeNumber);
        
        try {
            if (cafeNumber != null) {
                // Create an empty Leftover bean for the form
                LeftoverBean leftover = new LeftoverBean();
                
                // Adding the Leftover bean to the model
                model.addAttribute("leftover", leftover);
                
                // Adding cafeNumber to the session to ensure we have it when the leftover is added
                model.addAttribute("cafeNumber", cafeNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "cafeteria_owner/leftover/add_leftover";
    }
    
    @GetMapping("/foodList")
    public String listFoodItems(Model model, HttpSession session) {
        // Retrieve the cafeNumber from the session
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        List<LeftoverBean> foodList = new ArrayList<>();

        System.out.println("Masuk side bar controller lagi " + cafeNumber);
    
        // Check if cafeNumber is null or empty
        if (cafeNumber == null || cafeNumber.isEmpty()) {
            return "redirect:/error"; // Redirect if no cafeNumber is available
        }
    
        try (Connection connection = dataSource.getConnection()) {
            // SQL query to fetch food items for the specific cafe and today’s date
            String sql = "SELECT * FROM public.leftover WHERE \"cafeNumber\" = ? AND DATE(\"created_at\") = CURRENT_DATE";
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, cafeNumber); // Set the cafeNumber parameter
    
                ResultSet resultSet = statement.executeQuery(); // Execute the query
                while (resultSet.next()) {
                    // Create a new LeftoverBean and populate it with data from the result set
                    LeftoverBean food = new LeftoverBean();
                    food.setFoodid(resultSet.getInt("foodid"));
                    food.setFoodname(resultSet.getString("foodname"));
                    food.setInitialQuantity(resultSet.getInt("initial_quantity"));
                    food.setFoodquantity(resultSet.getInt("foodquantity"));
                    food.setPickupPlace(resultSet.getString("place_to_pickup"));
                    food.setPickupTime(resultSet.getString("pickup_time"));
                    food.setImagePath(resultSet.getString("image_path"));
                    foodList.add(food); // Add the food item to the list

                    System.out.println("Initial Quantity: " + food.getInitialQuantity());
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return "redirect:/error"; // Redirect in case of an error
        }
    
        // Add the food list to the model for rendering in the view
        model.addAttribute("foodList", foodList);
        return "cafeteria_owner/leftover/foodList"; // Return the view name
    }

     @GetMapping("/view_request")
    public String listFoodRequest(Model model, HttpSession session) {

        // Retrieve the cafeNumber from the session
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        System.out.println("Masuk side bar controller lagi " + cafeNumber);
    
        List<FoodRequestDetail> foodRequestDetails = new ArrayList<>();
    
        // Check if cafeNumber is null or empty
        if (cafeNumber == null || cafeNumber.isEmpty()) {
            return "redirect:/error"; // Redirect if no cafeNumber is available
        }
    
        try (Connection connection = dataSource.getConnection()) {
            // Prepare the SQL statement to fetch food items along with student details for the specific cafe
            String sql = "SELECT l.\"foodid\", l.\"foodname\",l.\"place_to_pickup\",l.\"pickup_time\", r.\"quantityrequest\", s.\"studentName\", s.\"studentNumber\", r.\"status\" " +
            "FROM public.leftover l " +
            "JOIN public.request r ON l.\"foodid\" = r.\"foodid\" " +
            "JOIN public.student s ON r.\"studentNumber\" = s.\"studentNumber\" " +
            "WHERE l.\"cafeNumber\" = ?";

    
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set the cafeNumber parameter in the query
                statement.setString(1, cafeNumber);
    
                ResultSet resultSet = statement.executeQuery(); // Execute the query
                while (resultSet.next()) {
                    // Create a new FoodRequestDetail object and populate it with data from the result set
                    FoodRequestDetail detail = new FoodRequestDetail();
                    System.out.println("TESTTTTTT");
                    
                    detail.setFoodid(resultSet.getInt("foodid"));
                    detail.setFoodname(resultSet.getString("foodname"));
                    detail.setPickupPlace(resultSet.getString("place_to_pickup"));
                    detail.setPickupTime(resultSet.getString("pickup_time"));
                    detail.setQuantity(resultSet.getInt("quantityrequest"));
                    detail.setStudentName(resultSet.getString("studentName"));
                    detail.setStudentNumber(resultSet.getString("studentNumber"));
                    detail.setStatus(resultSet.getString("status"));
    
                    foodRequestDetails.add(detail); // Add the combined data to the list
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return "redirect:/error"; // Redirect in case of an error
        }
    
        // Add the foodRequestDetails list to the model
        model.addAttribute("foodRequestDetails", foodRequestDetails);
        return "cafeteria_owner/leftover/accept_leftover"; // Return the view name to be rendered
    }

     @GetMapping("/viewBookings")
    public String viewBookings(Model model, HttpSession session) {
        // Retrieve the cafeNumber from the session
        String cafeNumber = (String) session.getAttribute("cafeNumber");
        List<BookingBean> bookings = new ArrayList<>();

        // Check if cafeNumber is valid
        if (cafeNumber == null || cafeNumber.isEmpty()) {
            model.addAttribute("error", "No valid cafeNumber found in session.");
            return "error_page"; // Redirect to an error page if cafeNumber is missing
        }

        try (Connection connection = dataSource.getConnection()) {
            // SQL query to fetch orders specific to the cafe owner
            String sql = "SELECT * FROM public.booking WHERE \"cafeNumber\" = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, cafeNumber); // Set the cafeNumber in the query
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        BookingBean booking = new BookingBean();
                        booking.setBookingID(resultSet.getInt("bookingID"));
                        booking.setBookingmenu(resultSet.getString("bookingmenu"));
                        booking.setBookingquantity(resultSet.getInt("bookingquantity"));
                        booking.setBookingdate(resultSet.getDate("bookingdate"));
                        booking.setCafeNumber(resultSet.getString("cafeNumber"));
                        booking.setStudentNumber(resultSet.getString("studentNumber"));
                        booking.setStatus(resultSet.getString("status"));
                        bookings.add(booking);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("bookings", bookings);
        return "cafeteria_owner/booking/accept_bookings"; // Name of the Thymeleaf HTML template
    }

    @GetMapping("/pickupStatus")
    public String getPickupStatus(HttpSession session, Model model) {
        // Retrieve the cafeteria number from the session
        String cafeNumber = (String) session.getAttribute("cafeNumber");

        if (cafeNumber == null) {
            return "redirect:/login"; // Redirect if no cafeteria number is set in the session
        }

        try (Connection connection = dataSource.getConnection()) {
            String sql = """
                        SELECT r."requestID", r."studentNumber", r."cafeNumber", r.status AS requestStatus,
                               r."quantityrequest", r.request_time,
                               CASE
                                   WHEN p.pickupid IS NOT NULL THEN 'Already Pickup'
                                   ELSE 'Did Not Pickup'
                               END AS pickupStatus
                        FROM request r
                        LEFT JOIN pickup p ON r.foodid = p.foodid AND r."studentNumber" = p."studentnumber"
                        WHERE r."cafeNumber" = ? -- Filter by cafeteria number from the session
                        ORDER BY r.request_time DESC
                    """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, cafeNumber); // Set the cafeteria number from the session

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<PickupStatusBean> pickupStatusList = new ArrayList<>();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    while (resultSet.next()) {
                        PickupStatusBean status = new PickupStatusBean();
                        status.setRequestID(resultSet.getString("requestID"));
                        status.setStudentNumber(resultSet.getString("studentNumber"));
                        status.setCafeNumber(resultSet.getString("cafeNumber"));
                        status.setRequestStatus(resultSet.getString("requestStatus"));
                        status.setQuantityRequest(resultSet.getInt("quantityrequest"));
                        LocalDateTime requestTime = resultSet.getTimestamp("request_time").toLocalDateTime();
                        status.setRequestTime(requestTime);
                        status.setFormattedRequestTime(requestTime.format(formatter)); // Add this
                        status.setPickupStatus(resultSet.getString("pickupStatus"));

                        pickupStatusList.add(status);
                    }
                    model.addAttribute("pickupStatusList", pickupStatusList);
                    return "cafeteria_owner/leftover/pickupView";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

}
