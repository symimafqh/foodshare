package com.heroku.java.MODEL.teacher;

public class CafeBean {
    private String cafeNumber;
    private String cafeName;
    private String cafeEmail;
    private String cafePassword;
    ;
	public CafeBean() {
		
	}

    /**
     * @return String return the cafeNumber
     */
    public String getCafeNumber() {
        return cafeNumber;
    }

    /**
     * @param cafeNumber the cafeNumber to set
     */
    public void setCafeNumber(String cafeNumber) {
        this.cafeNumber = cafeNumber;
    }

    /**
     * @return String return the cafeName
     */
    public String getCafeName() {
        return cafeName;
    }

    /**
     * @param cafeName the cafeName to set
     */
    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    /**
     * @return String return the cafeEmail
     */
    public String getCafeEmail() {
        return cafeEmail;
    }

    /**
     * @param cafeEmail the cafeEmail to set
     */
    public void setCafeEmail(String cafeEmail) {
        this.cafeEmail = cafeEmail;
    }

    /**
     * @return String return the cafePassword
     */
    public String getCafePassword() {
        return cafePassword;
    }

    /**
     * @param cafePassword the cafePassword to set
     */
    public void setCafePassword(String cafePassword) {
        this.cafePassword = cafePassword;
    }

}
