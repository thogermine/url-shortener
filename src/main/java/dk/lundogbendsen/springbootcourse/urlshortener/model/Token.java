package dk.lundogbendsen.springbootcourse.urlshortener.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    String token;
    String protectToken;
    String targetUrl;
    @ManyToOne
    User user;
}
