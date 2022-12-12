package nextstep.app;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.app.domain.Member;
import nextstep.app.infrastructure.InmemoryMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MemberAcceptanceTest extends AcceptanceTest {
    private static final Member TEST_ADMIN_MEMBER = InmemoryMemberRepository.ADMIN_MEMBER;
    private static final Member TEST_USER_MEMBER = InmemoryMemberRepository.USER_MEMBER;

    @Test
    void get_members_after_form_login() {
        ExtractableResponse<Response> memberResponse = requestResourceAfterLogin(TEST_ADMIN_MEMBER.getEmail(), TEST_ADMIN_MEMBER.getPassword(), "/members");

        assertThat(memberResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Member> members = memberResponse.jsonPath().getList(".", Member.class);
        assertThat(members.size()).isEqualTo(2);
    }

    @MethodSource("userProvider")
    @ParameterizedTest
    void get_me_after_form_login(String email, String password) {
        ExtractableResponse<Response> memberResponse = requestResourceAfterLogin(email, password, "/members/me");

        assertThat(memberResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        Member member = memberResponse.jsonPath().getObject(".", Member.class);
        assertThat(member.getEmail()).isEqualTo(email);
    }

    @Test
    void get_me_fail_after_form_login() {
        ExtractableResponse<Response> memberResponse = requestResourceAfterLogin(TEST_ADMIN_MEMBER.getEmail(), "invalid", "/members/me");

        assertThat(memberResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static Stream<Arguments> userProvider() {
        return Stream.of(
                arguments(TEST_ADMIN_MEMBER.getEmail(), TEST_ADMIN_MEMBER.getPassword()),
                arguments(TEST_USER_MEMBER.getEmail(), TEST_USER_MEMBER.getPassword())
        );
    }

    private ExtractableResponse<Response> requestResourceAfterLogin(String email, String password, String path) {
        Map<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("password", password);

        ExtractableResponse<Response> loginResponse = RestAssured.given().log().all()
                .formParams(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .when()
                .post("/login")
                .then().log().all()
                .extract();

        return RestAssured.given().log().all()
                .cookies(loginResponse.cookies())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }
}
