package com.heroku.java.MODEL.pickup;

import java.time.LocalDateTime;

public class PickupStatusBean {
    private String requestID;
    private String studentNumber;
    private String cafeNumber;
    private String requestStatus;
    private int quantityRequest;
    private LocalDateTime requestTime;
    private String pickupStatus;

    // Getters and Setters
    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getCafeNumber() {
        return cafeNumber;
    }

    public void setCafeNumber(String cafeNumber) {
        this.cafeNumber = cafeNumber;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public int getQuantityRequest() {
        return quantityRequest;
    }

    public void setQuantityRequest(int quantityRequest) {
        this.quantityRequest = quantityRequest;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getPickupStatus() {
        return pickupStatus;
    }

    public void setPickupStatus(String pickupStatus) {
        this.pickupStatus = pickupStatus;
    }

    private String formattedRequestTime;

    public String getFormattedRequestTime() {
        return formattedRequestTime;
    }

    public void setFormattedRequestTime(String formattedRequestTime) {
        this.formattedRequestTime = formattedRequestTime;
    }
}
