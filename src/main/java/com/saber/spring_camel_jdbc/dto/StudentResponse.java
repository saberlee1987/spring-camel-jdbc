package com.saber.spring_camel_jdbc.dto;

import lombok.Data;

import java.util.List;
@Data
public class StudentResponse {
    private List<StudentDto> response;
}
