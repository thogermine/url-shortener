package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    MockMvc mvc;
    @Mock
    UserService userService;

    @InjectMocks UserController userController;

    @BeforeEach
    public void initMockMvc() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userService.create(any(), any())).thenReturn(User.builder().username("cvw").password("pwsd").build());
        mvc.perform(
                        MockMvcRequestBuilders.post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"cvw\", \"password\":\"pwd\"}")
                )
                .andExpect(jsonPath("$.username", is("cvw")))
                .andDo(print())
                .andReturn();
    }
}
