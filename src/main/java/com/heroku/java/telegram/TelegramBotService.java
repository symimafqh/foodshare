package com.heroku.java.telegram;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramBotService {

    private static final String TELEGRAM_BOT_TOKEN = "7837384848:AAG2C6I3X80VkNP0l1s4PZUv1qOBbcoPcjM";  // Replace with your bot's token

    public void setWebhook(String webhookUrl) {
        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/setWebhook?url=" + webhookUrl;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(url, String.class);
    }
}
