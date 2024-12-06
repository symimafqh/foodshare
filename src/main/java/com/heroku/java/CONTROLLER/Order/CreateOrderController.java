package com.heroku.java.CONTROLLER.Order;

import com.heroku.java.MODEL.booking.BookingBean;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

@Controller
public class CreateOrderController {

    private final DataSource dataSource;

    @Autowired
    public CreateOrderController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/addBooking")
    public String addBooking(
            @ModelAttribute("addBooking") BookingBean booking, // Booking details from the form
            HttpSession session, Model model) {

        try {
            // Insert booking into the database
            try (Connection connection = dataSource.getConnection()) {
                String sql = "INSERT INTO public.booking (\"bookingmenu\", \"bookingquantity\", \"bookingdate\", \"cafeNumber\", \"studentNumber\") VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, booking.getBookingmenu()); // Menu item
                    statement.setInt(2, booking.getBookingquantity()); // Quantity
                    statement.setDate(3, Date.valueOf(booking.getBookingdate().toString())); // Booking date
                    statement.setString(4, booking.getCafeNumber()); // Cafe number from form

                    // Retrieve student number from session
                    String studentNumber = (String) session.getAttribute("studentNumber");
                    if (studentNumber == null || studentNumber.isEmpty()) {
                        model.addAttribute("error", "Session expired. Please log in again.");
                        return "redirect:/login"; // Redirect to login if session is invalid
                    }
                    statement.setString(5, studentNumber); // Student number from session

                    statement.executeUpdate();
                }
            }

            return "redirect:/dashboardStudent?success=true";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/add_Booking?error=true";
        }
    }
}
