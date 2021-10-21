package com.saber.spring_camel_jdbc.routes;

import com.saber.spring_camel_jdbc.dto.StudentAddResponse;
import com.saber.spring_camel_jdbc.dto.StudentDto;
import org.apache.camel.Exchange;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
public class AddStudentRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();
        rest("/student")
                .post("/add")
                .id("add-student-route")
                .responseMessage().code(200).responseModel(StudentDto.class).endResponseMessage()
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .enableCORS(true)
                .bindingMode(RestBindingMode.json)
                .type(StudentDto.class)
                .route()
                .routeId("add-student-route")
                .log("Receive Request for add student ====> ${in.body}")
                .process(exchange -> {
                    StudentDto dto = exchange.getIn().getBody(StudentDto.class);
                    exchange.getIn().setBody(String.format("insert into students (firstName, lastName, nationalCode, email, studentNumber, age) values ( '%s' , '%s' , '%s' , '%s','%s', %d ) ",
                            dto.getFirstName(),dto.getLastName(),dto.getNationalCode(),dto.getEmail(),dto.getStudentNumber(),dto.getAge()));
                })
                .to("jdbc:studentDataSource")
                .log("Response ====> ${in.body}")
                .process(exchange -> {
                    StudentAddResponse addResponse = new StudentAddResponse();
                    addResponse.setCode(0);
                    addResponse.setText("عملیات با موفقیت انجام شد");
                    exchange.getIn().setBody(addResponse);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE,constant(200));
    }
}
