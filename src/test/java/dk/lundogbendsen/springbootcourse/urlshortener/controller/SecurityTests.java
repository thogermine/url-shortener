package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {
    @Autowired
    MockMvc mockMvc;


    @Test
    public void listTokensTest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/token")
                )
                .andDo(print())
                .andReturn();

    }
}
