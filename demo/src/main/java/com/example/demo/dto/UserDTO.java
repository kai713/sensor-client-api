package com.example.demo.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String password;
}
