package org.example.controllers;

import org.example.ClientProcessor;
import org.example.ProcessingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;

import static org.example.ProcessingResultStatus.OK;
import static org.example.constants.ValidationConstants.NAME_LENGTH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ClientsController.class)
public class ClientsControllerTest {
    private  static final String MESSAGE_JSON_PROP = "$.message";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ClientProcessor clientProcessor;

    static ObjectMapper mapper = new ObjectMapper();

    Map<String, Object> correctInputParams = Map.of("name", "Alice",
                                             "surname", "Thomson",
                                             "wage", BigDecimal.valueOf(5500.75),
                                             "eventTime", "2012-04-23T18:25:43:511Z");

    Map<String, Object> nameTooShort = Map.of("name", "A",
            "surname", "Thomson",
            "wage", BigDecimal.valueOf(5500.75),
            "eventTime", "2012-04-23T18:25:43:511Z");

    Map<String, Object> wrongEvenTimeFormat = Map.of("name", "A",
            "surname", "Thomson",
            "wage", BigDecimal.valueOf(5500.75),
            "eventTime", "2012-04-23TF18:25:43:511Z");


    @BeforeEach
    void setUp() {
        when(clientProcessor.process(any())).thenReturn(new ProcessingResult(OK));
    }

    @Test
    public void correctInputJsonRequestReturnsStatusAccepted() throws Exception {
        this.mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(correctInputParams)))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void badRequestWithMessageWhenNameTooShort() throws Exception {
        this.mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nameTooShort)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_JSON_PROP).value(NAME_LENGTH));
    }


    @Test
    public void badRequestWhenWrongEventTimeFormat() throws Exception {
        this.mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongEvenTimeFormat)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_JSON_PROP).value("Invalid format for  field: eventTime."));
    }

}
