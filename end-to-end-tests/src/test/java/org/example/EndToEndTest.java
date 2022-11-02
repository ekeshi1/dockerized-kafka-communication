package org.example;

import lombok.SneakyThrows;
import org.example.utils.EndToEndSuite;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EndToEndTest extends EndToEndSuite {

    private static final String CLIENTS_ENDPOINT = "/api/clients";
    @Test
    @SneakyThrows
    void successfullyPostedMessageOnce(){

        ResponseEntity result = rest.post(CLIENTS_ENDPOINT, Map.of(
                "name", "Alice",
                "surname","Thmson",
                "wage", 5500.75,
                "eventTime","2012-04-23T18:25:43:511Z"
        ), Void.class);

        assertTrue(
                result.getStatusCode().equals(HttpStatus.ACCEPTED),
                "Unexpected status code: " + result.getStatusCode()
        );

        Thread.sleep(3000);

        Integer count = dbFacade.checkNameInserted("Alice");
        assertEquals(1, count);

    }


    @Test
    @SneakyThrows
    void shortNameIsNotInsertedToDb(){

        ResponseEntity<Map> result  =
                rest.post(CLIENTS_ENDPOINT, Map.of(
                "name", "A",
                "surname","Thomson",
                "wage", 5500.75,
                "eventTime","2012-04-23T18:25:43:511Z"
        ), Map.class);


        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String,String> errorMessage = (Map<String, String>) result.getBody();

        assertEquals("Name must be between 2 and 32 characters long", errorMessage.get("message"));
        Thread.sleep(3000);

        Integer count = dbFacade.checkNameInserted("A");
        assertEquals(0, count);


    }


    @Test
    @SneakyThrows
    void testInvalidEventTimeClientNotInserted(){

        ResponseEntity<Map> result  =
                rest.post(CLIENTS_ENDPOINT, Map.of(
                        "name", "UniqueName",
                        "surname","Thomson",
                        "wage", 5500.75,
                        "eventTime","2012-04-23FG18:25:43:511Z"
                ), Map.class);


        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String,String> errorMessage = (Map<String, String>) result.getBody();

        assertEquals("Name must be between 2 and 32 characters long", errorMessage.get("message"));
        Thread.sleep(3000);

        Integer count = dbFacade.checkNameInserted("UniqueName");
        assertEquals(0, count);
    }

    @Test
    @SneakyThrows
    void testNegativeClientWageNotAccepted(){

        ResponseEntity<Map> result  =
                rest.post(CLIENTS_ENDPOINT, Map.of(
                        "name", "UniqueName",
                        "surname","Thomson",
                        "wage", -5500.75,
                        "eventTime","2012-04-23FG18:25:43:511Z"
                ), Map.class);


        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String,String> errorMessage = (Map<String, String>) result.getBody();

        assertEquals("Wage must be positive", errorMessage.get("message"));
        Thread.sleep(3000);

        Integer count = dbFacade.checkNameInserted("UniqueName");
        assertEquals(0, count);
    }



}