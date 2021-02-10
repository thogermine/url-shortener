package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest_S1 {
    @Mock
    TokenService tokenService;
    @InjectMocks
    FollowTokenController followController;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        // MockMvc standalone approach
        mvc = MockMvcBuilders
                .standaloneSetup(followController)
                .build();
    }

    @Test
    public void followToken() throws Exception {
        when(tokenService.resolveToken("abc", null)).thenReturn("https://dr.dk");
        mvc.perform(get("/abc"))
                .andExpect(status().is(HttpStatus.MOVED_PERMANENTLY.value()))
                .andExpect(header().string("location", "https://dr.dk"))
        ;
    }
}
