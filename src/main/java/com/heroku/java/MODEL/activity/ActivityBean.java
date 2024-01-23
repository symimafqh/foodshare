package com.heroku.java.MODEL.activity;

public class ActivityBean {
    private String activityName;
	private String TeacherID;
	private  int activityID;

    public ActivityBean(){

    }

    /**
     * @return String return the activityName
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * @param activityName the activityName to set
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * @return String return the TeacherID
     */
    public String getTeacherID() {
        return TeacherID;
    }

    /**
     * @param TeacherID the TeacherID to set
     */
    public void setTeacherID(String TeacherID) {
        this.TeacherID = TeacherID;
    }

    /**
     * @return int return the activityID
     */
    public int getActivityID() {
        return activityID;
    }

    /**
     * @param activityID the activityID to set
     */
    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

}
