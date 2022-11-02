package org.example.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientDTO implements Serializable {
    @NotNull(message = "Name may not be null")
    @NotEmpty
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters long")
    private String name;

    @NotNull(message = "Surname may not be null")
    @NotEmpty
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters long")
    private String surname;

    @NotNull(message = "Wage may not be null")
    @Positive
    BigDecimal wage;

    @NotNull(message = "Event time was null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss:SSSXXX")
    ZonedDateTime eventTime;
}
