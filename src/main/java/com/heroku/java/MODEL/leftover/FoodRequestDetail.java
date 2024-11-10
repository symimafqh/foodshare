package com.heroku.java.MODEL.leftover;

public class FoodRequestDetail {
    private int foodid;
    private String foodname;
    private String cafeNumber;
    private String studentName;
    private String studentNumber;
    private String status;
    private String foodDescription;

    // Getters and setters for each field

    public int getFoodid() {
        return foodid;
    }

    public void setFoodid(int foodid) {
        this.foodid = foodid;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getCafeNumber() {
        return cafeNumber;
    }

    public void setCafeNumber(String cafeNumber) {
        this.cafeNumber = cafeNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

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

    // Optionally, you can override the toString() method for easier debugging
    @Override
    public String toString() {
        return "FoodRequestDetail{" +
                "foodid=" + foodid +
                ", foodname='" + foodname + '\'' +
                ", cafeNumber='" + cafeNumber + '\'' +
                ", studentName='" + studentName + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    /**
     * @return String return the foodDescription
     */
    public String getFoodDescription() {
        return foodDescription;
    }

    /**
     * @param foodDescription the foodDescription to set
     */
    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

}
