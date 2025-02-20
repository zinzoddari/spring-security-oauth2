package nextstep.app.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${oauth2.providers.github.client-id}")
    private String githubClientId;

    @Value("${oauth2.providers.google.client-id}")
    private String googleClientId;

    @Test
    @DisplayName("리다이렉트 정보를 성공적으로 받아 옵니다.")
    void redirectTest() throws Exception {
        //given
        final String requestUri = "/oauth2/authorization/github";

        final String expectedRedirectUri = "https://github.com/login/oauth/authorize" +
                "?client_id=" + githubClientId +
                "&response_type=code" +
                "&scope=read:user" +
                "&redirect_uri=http://localhost:8080/login/oauth2/code/github";

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }

    @Test
    @DisplayName("구글 oAuth 로그인 요청을 할 경우, 구글 리다이렉트 정보를 받아 옵니다.")
    void googleRedirectTest() throws Exception {
        //given
        final String requestUri = "/oauth2/authorization/google";

        final String expectedRedirectUri = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + googleClientId +
                "&response_type=code" +
                "&scope=read:user" +
                "&redirect_uri=http://localhost:8080/login/oauth2/code/google";

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get(requestUri))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUri));
    }
}
