import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import services.EmailService;

import javax.mail.MessagingException;
import java.util.Base64;
import java.util.logging.Logger;

@RestController
public class EmailController {

    private static final Logger logger = Logger.getLogger(EmailController.class.getName());
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/pubsub/push")
    public ResponseEntity<String> receiveMessage(@RequestBody PubSubMessage message) {
        logger.info("Received Pub/Sub message: " + message);
        String data = new String(Base64.getDecoder().decode(message.getMessage().getData()));
        logger.info("Decoded data: " + data);

        String[] parts = data.split(",");
        String email = parts[0].split("=")[1];
        String subject = "Bokningsbekräftelse";
        String body = "Tack för din bokning. Detaljer: " + data;

        try {
            emailService.sendEmail(email, subject, body);
            return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
        } catch (MessagingException e) {
            logger.severe("Failed to send email: " + e.getMessage());
            return new ResponseEntity<>("Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Getter
    public static class PubSubMessage {
        private Message message;

        public void setMessage(Message message) {
            this.message = message;
        }

        @Getter
        public static class Message {
            private String data;

            public void setData(String data) {
                this.data = data;
            }
        }
    }
}
