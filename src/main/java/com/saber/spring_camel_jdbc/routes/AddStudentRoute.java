package com.saber.spring_camel_jdbc.routes;

import com.saber.spring_camel_jdbc.dto.StudentAddResponse;
import com.saber.spring_camel_jdbc.dto.StudentDto;
import com.saber.spring_camel_jdbc.exceptions.ResourceExistException;
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
                .setHeader(Headers.Request_Body, simple("${in.body}"))
                .setHeader(Headers.NationalCode, simple("${in.body.nationalCode}"))
                .to("direct:find-by-nationalCode-route")
                .process(exchange -> {
                    String nationalCode = exchange.getIn().getHeader(Headers.NationalCode, String.class);
                    String response = exchange.getIn().getBody(String.class);
                    if (!response.trim().equals("[]")) {
                        throw new ResourceExistException(String.format("Student with nationalCode %s exist", nationalCode),"/camel/student/add");
                    }
                })
                .log("Receive Request for add student ====> ${in.body}")
                .process(exchange -> {
                    StudentDto dto = exchange.getIn().getHeader(Headers.Request_Body, StudentDto.class);
                    exchange.getIn().setBody(String.format("insert into students (firstName, lastName, nationalCode, email, studentNumber, age) values ( '%s' , '%s' , '%s' , '%s','%s', %d ) ",
                            dto.getFirstName(),dto.getLastName(),dto.getNationalCode(),dto.getEmail(),dto.getStudentNumber(),dto.getAge()));
                })
                .to("jdbc:studentDataSource")
                .log("Response ====> ${in.body}")
                .process(exchange -> {
                    StudentAddResponse addResponse = new StudentAddResponse();
                    addResponse.setCode(0);
                    addResponse.setText("???????????? ???? ???????????? ?????????? ????");
                    exchange.getIn().setBody(addResponse);
                })
                .removeHeader(Headers.Request_Body)
                .removeHeader(Headers.NationalCode)
                .setHeader(Exchange.HTTP_RESPONSE_CODE,constant(200));
    }
}
