package com.saber.spring_camel_jdbc.dto;

import lombok.Data;

@Data
public class StudentDto {
    private String firstName;
    private String lastName;
    private String nationalCode;
    private String email;
    private String studentNumber;
    private Integer age;

}
