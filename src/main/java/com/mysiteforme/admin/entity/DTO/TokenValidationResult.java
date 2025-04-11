package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.util.TokenErrorType;

import lombok.Data;

@Data
public class TokenValidationResult {

    private boolean isValid;
    private String errorMessage;
    private TokenErrorType errorType;

    public TokenValidationResult(){}

    public TokenValidationResult(boolean isValid) {
        this.isValid = isValid;
    }
    public TokenValidationResult(boolean isValid, String errorMessage, TokenErrorType errorType) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

    public boolean isValid() {
        return isValid;
    }
    
}
