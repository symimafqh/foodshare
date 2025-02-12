package com.heroku.java.MODEL.leftover;


public class LeftoverBean {
    private int foodid;                // Unique identifier for each leftover item
    private String foodname;           // Name of the leftover food item
    private int foodquantity;     
    private int initialquantity;     // Quantity of the leftover food item
    private String fooddescription;    // Description of the food item
    private String imagePath;   
    private String cafeNumber;  
    private String pickupPlace;
    private String pickupTime;      // Path to the image on the server or URL if stored externally

    // Default constructor
    public LeftoverBean() {
    }

    // Parameterized constructor
    public LeftoverBean(int foodid, String foodname, int initialquantity, int foodquantity, String fooddescription, String imagePath, String cafeNumber) {
        this.foodid = foodid;
        this.foodname = foodname;
        this.foodquantity = foodquantity;
        this.initialquantity = initialquantity;
        this.fooddescription = fooddescription;
        this.imagePath = imagePath;
        this.cafeNumber = cafeNumber;
    }

    // Getters and Setters
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

    public int getFoodquantity() {
        return foodquantity;
    }

    public void setFoodquantity(int foodquantity) {
        this.foodquantity = foodquantity;
    }

    public String getFooddescription() {
        return fooddescription;
    }

    public void setFooddescription(String fooddescription) {
        this.fooddescription = fooddescription;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public String getCafeNumber() {
        return cafeNumber;
    }

    public void setCafeNumber(String cafeNumber) {
        this.cafeNumber = cafeNumber;
    }
    // Optional: toString method for debugging
    @Override
    public String toString() {
        return "Leftover{" +
                "foodid=" + foodid +
                ", foodname='" + foodname + '\'' +
                ", foodquantity=" + foodquantity +
                ", fooddescription='" + fooddescription + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

    /**
     * @return String return the pickupPlace
     */
    public String getPickupPlace() {
        return pickupPlace;
    }

    /**
     * @param pickupPlace the pickupPlace to set
     */
    public void setPickupPlace(String pickupPlace) {
        this.pickupPlace = pickupPlace;
    }

     /**
     * @return String return the pickupPlace
     */
    public String getPickupTime() {
        return pickupTime;
    }

    /**
     * @param pickupTime the pickupPlace to set
     */
    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

     /**
     * @return String return the pickupPlace
     */
    public int getInitialQuantity() {
        return initialquantity;
    }

    /**
     * @param pickupTime the pickupPlace to set
     */
    public void setInitialQuantity(int initialquantity) {
        this.initialquantity = initialquantity;
    }




}
