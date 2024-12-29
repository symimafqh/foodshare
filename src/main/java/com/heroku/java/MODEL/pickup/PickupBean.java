package com.heroku.java.MODEL.pickup;

public class PickupBean {
    private String timePickup; // Expected format: "YYYY-MM-DD HH:mm:ss"
    private int foodId;
    private String imagePath; 
    private String studentNumber;

    // Getters and Setters
    public String getTimePickup() {
        return timePickup;
    }

    public void setTimePickup(String timePickup) {
        this.timePickup = timePickup;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    /**
     * @return String return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return String return the studentNumber
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * @param studentNumber the studentNumber to set
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

}

