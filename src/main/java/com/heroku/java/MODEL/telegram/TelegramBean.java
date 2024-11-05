package com.heroku.java.MODEL.telegram;

public class TelegramBean {
    private String telegramChatId;
    private String username;
    

    /**
     * @return String return the telegramChatId
     */
    public String getTelegramChatId() {
        return telegramChatId;
    }

    /**
     * @param telegramChatId the telegramChatId to set
     */
    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    /**
     * @return String return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
