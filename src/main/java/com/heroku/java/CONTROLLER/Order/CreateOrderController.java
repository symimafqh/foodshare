package com.heroku.java.CONTROLLER.Order;

import com.heroku.java.MODEL.booking.BookingBean;
import com.heroku.java.MODEL.student.StudentBean;
import com.heroku.java.SERVICE.WhatsappService;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CreateOrderController {

    private final DataSource dataSource;
    private final WhatsappService whatsAppService;

    @Autowired
    public CreateOrderController(DataSource dataSource, WhatsappService whatsAppService) {
        this.dataSource = dataSource;
        this.whatsAppService = whatsAppService; // Inject WhatsApp service
    }

    @PostMapping("/addBooking")
    public String addBooking(
            @ModelAttribute("addBooking") BookingBean booking, // Booking details from the form
            HttpSession session, Model model) {
        String status = "Pending";

        try {
            // Insert booking into the database
            try (Connection connection = dataSource.getConnection()) {
                String sql = "INSERT INTO public.booking (\"bookingmenu\", \"bookingquantity\", \"bookingdate\", \"cafeNumber\", \"studentNumber\", \"status\") VALUES (?, ?, ?, ?, ?, ?)";
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
                    statement.setString(6, status); // Status

                    statement.executeUpdate();
                }
            }

            // Notify the cafeteria owner
            notifyCafe(booking, session);

            return "redirect:/dashboardStudent?success=true";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/add_Booking?error=true";
        }
    }

    private void notifyCafe(BookingBean booking, HttpSession session) {
        // Step 1: Get list of cafe phone numbers
        List<String> cafeNumbers = getCafePhoneNumbers(booking.getCafeNumber());

        // Step 2: Retrieve student details using the studentNumber from the session
        String studentNumber = (String) session.getAttribute("studentNumber");
        StudentBean student = getStudentDetails(studentNumber); // Fetch student details

        // Check if student details were successfully retrieved
        if (student == null) {
            System.out.println("Student not found.");
            return; // Exit the method if student details are not found
        }

        // Step 3: Create the message to be sent
        String messageBody = "New booking request by a student!\n" +
                "Student Name: " + student.getStudentName() + "\n" +
                "Student Number: " + student.getStudentNumber() + "\n" +
                "Booking Menu: " + booking.getBookingmenu() + "\n" +
                "Quantity: " + booking.getBookingquantity() + "\n" +
                "Booking Date: " + booking.getBookingdate() + "\n" +
                "Please check and confirm the booking.";

        // Step 4: Send the message to each cafe
        for (String cafePhoneNumber : cafeNumbers) {
            try {
                // Assuming you have a WhatsAppService that handles sending messages
                String chatId = cafePhoneNumber + "@c.us"; // Construct the chat ID for WhatsApp
                String response = whatsAppService.sendMessage(chatId, messageBody);
                System.out.println("Message sent to: " + cafePhoneNumber);
                System.out.println("WhatsApp Response: " + response);
            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                System.out.println("Failed to send message to: " + cafePhoneNumber);
            }
        }
    }

    private List<String> getCafePhoneNumbers(String cafeNumber) {
        List<String> numbers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT DISTINCT c.\"phoneNumber\" FROM public.cafeteria_owner c WHERE c.\"cafeNumber\" = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, cafeNumber);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        String phoneNumber = rs.getString("phoneNumber");

                        // Add '6' prefix if the phone number does not start with '6'
                        if (!phoneNumber.startsWith("6")) {
                            phoneNumber = "6" + phoneNumber;
                        }

                        numbers.add(phoneNumber); // Add phone number to the list
                        System.out.println("Phone Number: " + phoneNumber);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
        }

        return numbers;
    }

    private StudentBean getStudentDetails(String studentNumber) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT \"studentName\", \"studentNumber\" FROM public.student WHERE \"studentNumber\" = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, studentNumber);

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        StudentBean student = new StudentBean();
                        student.setStudentName(rs.getString("studentName"));
                        student.setStudentNumber(rs.getString("studentNumber"));
                        return student;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
