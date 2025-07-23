package com.spring.clinic.dto.users;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private int age;
    private String role;

}
