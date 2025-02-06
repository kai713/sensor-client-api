package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLogin {
    Integer statusCode;
    String message;
    String token;
    String refreshToken;
    String role;
    String expirationTime;
}
