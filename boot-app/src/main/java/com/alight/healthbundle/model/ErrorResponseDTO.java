package com.alight.healthbundle.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDTO {
    private int statusCode;
    private String errorMessage;

    public ErrorResponseDTO(int statusCode, String errorMessage) {
        super();
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public ErrorResponseDTO(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

}
