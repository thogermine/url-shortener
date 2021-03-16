package dk.lundogbendsen.springbootcourse.urlshortener.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    String token;
    String protectToken;
    String targetUrl;
    User user;
}
