package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.controller.security.SecurityContext;
import dk.lundogbendsen.springbootcourse.urlshortener.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FollowTokenController {
    @Autowired
    TokenService tokenService;

    @GetMapping("{token}")
    public ResponseEntity<Object> follow(@PathVariable String token) {
        final String targetUrl = tokenService.resolveToken(token, SecurityContext.getProtectToken());
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, targetUrl).build();
    }

}
