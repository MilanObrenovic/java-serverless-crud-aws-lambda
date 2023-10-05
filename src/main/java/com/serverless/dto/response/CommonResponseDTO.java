package com.serverless.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommonResponseDTO {

    @JsonProperty(value = "message")
    private String message;

}
