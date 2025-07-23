package com.spring.clinic.dto.users;

import lombok.Data;

@Data
public class UserDTO {
    private long userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private String password;
    private int age;
    private String userRole;
}
