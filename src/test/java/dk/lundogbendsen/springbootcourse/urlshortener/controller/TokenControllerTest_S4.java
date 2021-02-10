package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.service.TokenService;
import dk.lundogbendsen.springbootcourse.urlshortener.service.UserService;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.TokenAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenControllerTest_S4 {
    @MockBean
    UserService userService;
    @MockBean
    TokenService tokenService;
    @Autowired TokenController tokenController;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        when(userService.getUser("user1")).thenReturn(User.builder().username("user1").password("password1").build());
    }

    @Test
    public void createNonUniqueToken() throws Exception {
        when(tokenService.create(anyString(), anyString(), anyString(), any())).thenThrow(TokenAlreadyExistsException.class);

        Map<String, String> token = new HashMap<>();
        token.put("token", "abc");
        token.put("targetUrl", "https://dr.dk");
        token.put("protectToken", "protectAbc");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user1", "password1");
        HttpEntity<Map> requestEntity = new HttpEntity<>(token, headers);

        final ResponseEntity<Void> voidResponseEntity = restTemplate.postForEntity("/token", requestEntity, Void.class);
        assertEquals(409, voidResponseEntity.getStatusCode().value());
    }

}