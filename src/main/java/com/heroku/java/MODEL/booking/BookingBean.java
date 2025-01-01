package com.heroku.java.MODEL.booking;

import java.sql.Date;
import java.time.LocalDate;

public class BookingBean {

    private int bookingID; // Unique identifier for each leftover item
    private String bookingmenu; // Name of the leftover food item
    private int bookingquantity; // Quantity of the leftover food item
    private Date bookingdate;
    private String cafeNumber;
    private String studentNumber;
    private String status;




    // Getter and Setter for bookingID
    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    // Getter and Setter for bookingmenu
    public String getBookingmenu() {
        return bookingmenu;
    }

    public void setBookingmenu(String bookingmenu) {
        this.bookingmenu = bookingmenu;
    }

    // Getter and Setter for bookingquantity
    public int getBookingquantity() {
        return bookingquantity;
    }

    public void setBookingquantity(int bookingquantity) {
        this.bookingquantity = bookingquantity;
    }

    // Getter and Setter for bookingdate
    public Date getBookingdate() {
        return bookingdate;
    }

    public void setBookingdate(Date bookingdate) {
        this.bookingdate = bookingdate;
    }

    // Getter and Setter for cafeNumber
    public String getCafeNumber() {
        return cafeNumber;
    }

    public void setCafeNumber(String cafeNumber) {
        this.cafeNumber = cafeNumber;
    }

    // Getter and Setter for studentNumber
    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

}


