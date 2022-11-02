package org.example.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpMethod.POST;

@TestComponent
public class TestRestFacade {

    @Value("${api.exposed-port}")
    private int port;
    @Value("${api.host}")
    private String host;
    @Autowired
    private TestRestTemplate rest;

    public <T> ResponseEntity<T> post(String url, Object body, Class<T> responseType) {

        return rest.exchange(
                "http://" + host + ":" + port + url,
                POST,
                new HttpEntity<>(body),
                responseType
        );
    }
}
