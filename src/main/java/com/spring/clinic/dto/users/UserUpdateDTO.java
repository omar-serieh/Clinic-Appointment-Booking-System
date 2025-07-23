package com.spring.clinic.dto.users;

import lombok.Data;


import java.util.Set;

@Data
public class UserUpdateDTO {
    private String name;
    private String PhoneNumber;
    private Integer age;
    private Set<Long> specialtyIds;

}
