package com.heroku.java.MODEL.registration;

import com.heroku.java.MODEL.activity.ActivityBean;

//import com.heroku.java.MODEL.activity.ActivityBean;
//import com.heroku.java.MODEL.student.StudentBean;

public class RegistrationBean {

    private int registrationid;
    private String studentIC;
    private String activityid;
    private int unitReg;
	private int clubReg;
	private int sportReg;
    private ActivityBean activity;
    private int quotauniform;


    public RegistrationBean(){

    }

    /**
     * @return int return the registrationid
     */
    public int getRegistrationid() {
        return registrationid;
    }

    /**
     * @param registrationid the registrationid to set
     */
    public void setRegistrationid(int registrationid) {
        this.registrationid = registrationid;
    }

    /**
     * @return String return the studentic
     */
    public String getStudentIC() {
        return studentIC;
    }

    /**
     * @param studentIC the studentic to set
     */
    public void setStudentIC(String studentIC) {
        this.studentIC = studentIC;
    }

    /**
     * @return String return the activityid
     */
    public String getActivityid() {
        return activityid;
    }

    /**
     * @param activityid the activityid to set
     */
    public void setActivityid(String activityid) {
        this.activityid = activityid;
    }


    /**
     * @return int return the unitReg
     */
    public int getUnitReg() {
        return unitReg;
    }

    /**
     * @param unitReg the unitReg to set
     */
    public void setUnitReg(int unitReg) {
        this.unitReg = unitReg;
    }

    /**
     * @return int return the clubReg
     */
    public int getClubReg() {
        return clubReg;
    }

    /**
     * @param clubReg the clubReg to set
     */
    public void setClubReg(int clubReg) {
        this.clubReg = clubReg;
    }

    /**
     * @return int return the sportReg
     */
    public int getSportReg() {
        return sportReg;
    }

    /**
     * @param sportReg the sportReg to set
     */
    public void setSportReg(int sportReg) {
        this.sportReg = sportReg;
    }

     /**
     * @return int return the sportReg
     */
    public int getQuotaUniform() {
        return quotauniform;
    }

    /**
     * @param sportReg the sportReg to set
     */
    public void setQuotaUniform(int quotauniform) {
        this.quotauniform = quotauniform;
    }

}
