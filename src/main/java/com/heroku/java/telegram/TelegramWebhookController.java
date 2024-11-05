import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TelegramWebhookController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TelegramWebhookController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

   @PostMapping("/webhook")
public String handleTelegramUpdates(@RequestBody String payload) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(payload);
        JsonNode messageNode = rootNode.path("message");

        if (!messageNode.isMissingNode()) {
            JsonNode chatNode = messageNode.path("chat");
            String chatId = chatNode.path("id").asText();
            String username = chatNode.path("username").asText();

            // Store the chat ID in the telegram_users table
            storeChatIdInDatabase(chatId, username);
            return "Chat ID saved successfully!";
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "Error processing the update.";
    }

    return "No message found in the update.";
}

private void storeChatIdInDatabase(String chatId, String username) {
    String sql = "INSERT INTO public.telegram_users (telegramChatId, username) VALUES (?, ?) ON CONFLICT (telegramChatId) DO NOTHING";
    jdbcTemplate.update(sql, chatId, username);
}

}
