package com.heroku.java.CONTROLLER.Order;

import com.heroku.java.MODEL.booking.BookingBean;
import com.heroku.java.SERVICE.WhatsappService;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingController {
private final DataSource dataSource;
    private final WhatsappService whatsAppService; // Import your WhatsApp service

    @Autowired
    public BookingController (DataSource dataSource, WhatsappService whatsAppService) {
        this.dataSource = dataSource;
        this.whatsAppService = whatsAppService; // Inject WhatsApp service
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

    @PostMapping("/approveOrder")
    public String approveOrder(@RequestParam("bookingID") int bookingID) {
        String updateSql = "UPDATE public.booking SET \"status\" = 'Approved' WHERE \"bookingid\" = ?";

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
        String updateSql = "UPDATE public.booking SET \"status\" = 'Rejected' WHERE \"bookingid\" = ?";

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

   // Load Update Booking Form
   @GetMapping("/updateBooking")
   public String updateBookingForm(@RequestParam("bookingID") int bookingID, Model model) {
       try (Connection connection = dataSource.getConnection()) {
           String sql = "SELECT * FROM public.booking WHERE \"bookingid\" = ?";
           try (PreparedStatement statement = connection.prepareStatement(sql)) {
               statement.setInt(1, bookingID);
               try (ResultSet resultSet = statement.executeQuery()) {
                   if (resultSet.next()) {
                       BookingBean booking = new BookingBean();
                       booking.setBookingID(resultSet.getInt("bookingID"));
                       booking.setBookingmenu(resultSet.getString("bookingmenu"));
                       booking.setBookingquantity(resultSet.getInt("bookingquantity"));
                       booking.setBookingdate(resultSet.getDate("bookingdate"));
                       booking.setCafeNumber(resultSet.getString("cafeNumber"));
                       booking.setStudentNumber(resultSet.getString("studentNumber"));
                       booking.setStatus(resultSet.getString("status"));
                       model.addAttribute("booking", booking);
                   }
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
           return "redirect:/viewBookings?error=load_failed";
       }
       return "cafeteria_owner/booking/update_order"; // Thymeleaf template for updating booking
   }

   @PostMapping("/saveUpdatedBooking")
public String saveUpdatedBooking(BookingBean booking, @RequestParam("bookingID") int bookingID) {
    String updateSql = "UPDATE public.booking SET \"bookingmenu\" = ?, \"bookingquantity\" = ?, \"bookingdate\" = ? WHERE \"bookingid\" = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(updateSql)) {
         
        // Update the booking details in the database
        statement.setString(1, booking.getBookingmenu());
        statement.setInt(2, booking.getBookingquantity());
        statement.setDate(3, booking.getBookingdate());
        statement.setInt(4, bookingID); // Use the passed bookingID
        statement.executeUpdate();

        // Notify the student who made the booking
        notifyStudent(connection, bookingID, booking);

        return "redirect:/viewBookings?success=updated";
    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/viewBookings?error=update_failed";
    }
}

// Notify the student about the updated booking
private void notifyStudent(Connection connection, int bookingID, BookingBean booking) {
    String studentPhoneQuery = "SELECT s.studentphonenumber FROM student s JOIN booking b ON s.\"studentNumber\" = b.\"studentNumber\" WHERE b.\"bookingid\" = ?";
    try (PreparedStatement studentStatement = connection.prepareStatement(studentPhoneQuery)) {
        studentStatement.setInt(1, bookingID);

        try (ResultSet resultSet = studentStatement.executeQuery()) {
            if (resultSet.next()) {
                String studentPhoneNumber = resultSet.getString("studentphonenumber");

                // Create the WhatsApp message
                String messageBody = "Your booking details have been updated:\n" +
                        "Menu: " + booking.getBookingmenu() + "\n" +
                        "Quantity: " + booking.getBookingquantity() + "\n" +
                        "Date: " + booking.getBookingdate() + "\n" +
                        "Please contact the cafeteria for more information if needed.";

                // Send the message using WhatsAppService
                String chatId = studentPhoneNumber + "@c.us"; // Format the phone number for WhatsApp
                String response = whatsAppService.sendMessage(chatId, messageBody);

                System.out.println("Notification sent to student: " + studentPhoneNumber);
                System.out.println("WhatsApp Response: " + response);
            } else {
                System.out.println("No student found for booking ID: " + bookingID);
            }
        }
    } catch (Exception e) {
        e.printStackTrace(); // Log any errors
        System.out.println("Failed to send notification for booking ID: " + bookingID);
    }
}



   private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @GetMapping("/bookingHistory")
    public String getBookingHistory(HttpSession session, Model model) {
        String studentNumber = (String) session.getAttribute("studentNumber");

        if (studentNumber == null) {
            return "redirect:/login";
        }

        try (Connection connection = dataSource.getConnection()) {
            String sql = """
                        SELECT
                            bookingid,
                            bookingmenu,
                            bookingquantity,
                            bookingdate,
                            status
                        FROM booking
                        WHERE \"studentNumber\" = ?
                        ORDER BY bookingdate DESC
                    """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentNumber);

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<BookingBean> bookingHistoryList = new ArrayList<>();

                    while (resultSet.next()) {
                        BookingBean booking = new BookingBean();
                        booking.setBookingID(resultSet.getInt("bookingid"));
                        booking.setBookingmenu(resultSet.getString("bookingmenu"));
                        booking.setBookingquantity(resultSet.getInt("bookingquantity"));
                        booking.setBookingdate(resultSet.getDate("bookingdate"));
                        booking.setStatus(resultSet.getString("status"));

                        bookingHistoryList.add(booking);
                    }

                    model.addAttribute("bookingHistoryList", bookingHistoryList);
                    return "student/order/viewBookingHistory";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }
}
