package com.epam.esm.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ResponseClientException implements Serializable {
    private String message;
    private String code;
}
