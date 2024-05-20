package controller;

import lombok.Getter;
import model.EmailRequests;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import services.EmailService;

import javax.mail.MessagingException;
import java.util.Base64;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> receiveMessage(@RequestBody EmailRequests emailRequest) {
        try {
            emailService.sendEmail(emailRequest.getEmail(), emailRequest.getSubject(), emailRequest.getBody());
            return ResponseEntity.ok("Email sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email");
        }
    }

    @PostMapping("/pubsub/push")
    public ResponseEntity<String> receiveMessage(@RequestBody PubSubMessage message) {
        String data = new String(Base64.getDecoder().decode(message.getMessage().getData()));
        String[] parts = data.split(",");
        String email = parts[0].split("=")[1];
        String subject = "Bokningsbekräftelse";
        String body = "Tack för din bokning på BookingBee. Detaljer: " + data;

        try {
            emailService.sendEmail(email, subject, body);
            return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
        } catch (MessagingException e) {
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

