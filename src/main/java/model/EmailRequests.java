package model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequests {
    private String email;
    private String subject;
    private String body;
}
