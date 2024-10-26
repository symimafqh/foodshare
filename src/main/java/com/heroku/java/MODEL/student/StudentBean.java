package com.heroku.java.MODEL.student;

public class StudentBean {
    private String studentNumber;
	private String studentName;
	private String studentEmail;
    private String studentPassword;
    private String studentPhone;

    public StudentBean(){
    	
    }

    /**
     * @return String return the studentIC
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * @param studentNumber the studentIC to set
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * @return String return the studentName
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * @param studentName the studentName to set
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * @return String return the studentEmail
     */
    public String getStudentEmail() {
        return studentEmail;
    }

    /**
     * @param studentEmail the studentEmail to set
     */
    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    /**
     * @return String return the studentPhone
     */
    public String getStudentPhone() {
        return studentPhone;
    }

    /**
     * @param studentPhone the studentPhone to set
     */
    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }
    /**
     * @return String return the studentPassword
     */
    public String getStudentPassword() {
        return studentPassword;
    }

    /**
     * @param studentPassword the studentPassword to set
     */
    public void setStudentPassword(String studentPassword) {
        this.studentPassword = studentPassword;
    }


}
