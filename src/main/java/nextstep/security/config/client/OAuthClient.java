package nextstep.security.config.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class OAuthClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OAuthClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public <R> R get(final String uri, final HttpHeaders headers, final TypeReference<R> returnTypeReference) {
        RequestEntity<Void> request = RequestEntity
                .get(uri)
                .headers(headers)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        final ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("호출 에러 응답:" + response.getStatusCode() + ":" + response.getBody());
        }

        final String body = response.getBody();
        if (body == null) {
            throw new RuntimeException("응답값이 비어있습니다.");
        }

        return parseResponse(body, returnTypeReference);
    }

    public <I, R> R post(final String uri, final I input, final TypeReference<R> returnTypeReference) {
        RequestEntity<I> request = RequestEntity
                .post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .body(input);

        final ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("호출 에러 응답:" + response.getStatusCode() + ":" + response.getBody());
        }

        final String body = response.getBody();
        if (body == null) {
            throw new RuntimeException("응답값이 비어있습니다.");
        }

        return parseResponse(body, returnTypeReference);
    }

    private <R> R parseResponse(String response, TypeReference<R> typeReference) {
        try {
            return objectMapper.readValue(Optional.ofNullable(response).orElse("{}"), typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
