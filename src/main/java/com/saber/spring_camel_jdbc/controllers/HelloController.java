package com.saber.spring_camel_jdbc.controllers;

import com.saber.spring_camel_jdbc.dto.HelloDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloController {

    @GetMapping(value = "/sayHello")

    public ResponseEntity<HelloDto> sayHello(@RequestParam(name = "firstName") String firstName, @RequestParam(name = "lastName") String lastName) {

        String message = String.format("Hello %s %s", firstName, lastName);
        HelloDto helloDto = new HelloDto();
        helloDto.setMessage(message);

        return ResponseEntity.ok(helloDto);
    }
}
