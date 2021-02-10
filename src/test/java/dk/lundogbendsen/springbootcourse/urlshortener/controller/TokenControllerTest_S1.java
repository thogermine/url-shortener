package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.controller.security.SecurityIntercepter;
import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.service.TokenService;
import dk.lundogbendsen.springbootcourse.urlshortener.service.UserService;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.TokenAlreadyExistsException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TokenControllerTest_S1 {
    @Mock
    UserService userService;
    @Mock
    TokenService tokenService;
    @InjectMocks
    TokenController tokenController;
    @InjectMocks
    SecurityIntercepter securityIntercepter;
    @InjectMocks
    ControllerAdvicerServiceLayer exceptionHandlerAdvicer;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        // MockMvc standalone approach
        mvc = MockMvcBuilders
                .standaloneSetup(tokenController)
                .addInterceptors(securityIntercepter)
                .setControllerAdvice(exceptionHandlerAdvicer)
                .build();

        when(userService.getUser("user1")).thenReturn(User.builder().username("user1").password("password1").build());
    }

    @Test
    public void createNonUniqueToken() throws Exception {
        when(tokenService.create(anyString(), anyString(), anyString(), any())).thenThrow(TokenAlreadyExistsException.class);

        Map<String, String> token = new HashMap<>();
        token.put("token", "abc");
        token.put("targetUrl", "https://dr.dk");
        token.put("protectToken", "protectAbc");
        JSONObject json = new JSONObject(token);
        final String content = json.toString();
        mvc.perform(
                post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user1:password1".getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
        ;
    }
}