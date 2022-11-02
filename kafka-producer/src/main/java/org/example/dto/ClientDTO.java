package org.example.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.example.constants.ValidationConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDTO implements Serializable {

    @NotNull(message = NAME_NOT_NULL)
    @NotEmpty(message = NAME_NOT_EMTPY)
    @Size(min = 2, max = 32, message =  NAME_LENGTH)
    private String name;

    @NotNull(message = SURNAME_NOT_NULL)
    @NotEmpty(message = SURNAME_NOT_EMTPY)
    @Size(min = 2, max = 32, message = SURNAME_LENGTH)
    private String surname;

    @NotNull(message = WAGE_NON_NULL)
    @Positive(message = POSITIVE_WAGE)
    BigDecimal wage;

    @NotNull(message = EVENT_TIME_NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss:SSSXXX")
    ZonedDateTime eventTime;
}
