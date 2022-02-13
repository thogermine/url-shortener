package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InspirationalSecurityTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;


    @Test
    public void listTokensValidAuthentication() throws Exception {
        when(userService.getUser(any())).thenReturn(User.builder().username("cvw").password("pwd").build());
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/token").header("Authorization", "Basic " + Base64.getEncoder().encodeToString("cvw:pwd".getBytes()))
                )
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

    }

    @Test
    public void listTokensInvalidAuthentication() throws Exception {
        when(userService.getUser(any())).thenReturn(User.builder().username("cvw").password("pwd").build());
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/token").header("Authorization", "Basic " + Base64.getEncoder().encodeToString("cvw:pwdWrong".getBytes()))
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andReturn();

    }
}
