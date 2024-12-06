package com.heroku.java.CONTROLLER.Order;

import com.heroku.java.MODEL.booking.BookingBean;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingController {

    private final DataSource dataSource;

    @Autowired
    public BookingController(DataSource dataSource) {
        this.dataSource = dataSource;
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
        return "cafeteria_owner/leftover/accept_bookings"; // Name of the Thymeleaf HTML template
    }

    @PostMapping("/approveOrder")
    public String approveOrder(@RequestParam("bookingID") int bookingID) {
        String updateSql = "UPDATE public.booking SET \"status\" = 'Approved' WHERE \"bookingID\" = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateSql)) {

            statement.setInt(1, bookingID); // Set the booking ID to update
            statement.executeUpdate(); // Execute the update

            System.out.println("Status updated to 'Approved' for booking ID: " + bookingID);

            return "redirect:/viewBookings?success=approved";

        } catch (SQLException e) {
            e.printStackTrace(); // Log any error that occurs during the update
            return "redirect:/viewBookings?error=update_failed";
        }
    }

    @PostMapping("/rejectOrder")
    public String rejectOrder(@RequestParam("bookingID") int bookingID) {
        String updateSql = "UPDATE public.booking SET \"status\" = 'Rejected' WHERE \"bookingID\" = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateSql)) {

            statement.setInt(1, bookingID); // Set the booking ID to update
            statement.executeUpdate(); // Execute the update

            System.out.println("Status updated to 'Rejected' for booking ID: " + bookingID);

            return "redirect:/viewBookings?success=rejected";

        } catch (SQLException e) {
            e.printStackTrace(); // Log any error that occurs during the update
            return "redirect:/viewBookings?error=update_failed";
        }
    }
}
