package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.service.TokenService;
import dk.lundogbendsen.springbootcourse.urlshortener.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FollowControllerTest_S3 {
    @MockBean
    UserService userService;
    @MockBean
    TokenService tokenService;
    @Autowired
    FollowTokenController followController;

    @Autowired MockMvc mvc;

    @Test
    public void followToken() throws Exception {
        when(tokenService.resolveToken("abc", null)).thenReturn("https://dr.dk");
        mvc.perform(get("/abc"))
                .andExpect(status().is(HttpStatus.MOVED_PERMANENTLY.value()))
                .andExpect(header().string("location", "https://dr.dk"))
        ;
    }
}
