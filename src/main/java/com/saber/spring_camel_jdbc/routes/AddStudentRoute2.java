package com.saber.spring_camel_jdbc.routes;

import com.saber.spring_camel_jdbc.dto.ServiceErrorResponse;
import com.saber.spring_camel_jdbc.dto.StudentAddResponse;
import com.saber.spring_camel_jdbc.dto.StudentDto;
import com.saber.spring_camel_jdbc.exceptions.ResourceExistException;
import org.apache.camel.Exchange;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Component
public class AddStudentRoute2 extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();
        rest("/student")
                .post("/add2")
                .id("add2-student-route")
                .responseMessage().code(200).responseModel(StudentDto.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServiceErrorResponse.class).endResponseMessage()
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .enableCORS(true)
                .bindingMode(RestBindingMode.json)
                .type(StudentDto.class)
                .route()
                .routeId("add2-student-route")
                .transacted("PROPAGATION_REQUIRED")
                .setHeader(Headers.Request_Body, simple("${in.body}"))
                .setHeader(Headers.NationalCode, simple("${in.body.nationalCode}"))
                .to("direct:find-by-nationalCode-route")
                .process(exchange -> {
                    String nationalCode = exchange.getIn().getHeader(Headers.NationalCode, String.class);
                    String response = exchange.getIn().getBody(String.class);
                    if (!response.trim().equals("[]")) {
                        throw new ResourceExistException(String.format("Student with nationalCode %s exist", nationalCode),"/camel/student/add2");
                    }
                })
                .log("Receive Request for add2 student ====> ${in.body}")
                .process(exchange -> {
                    StudentDto dto = exchange.getIn().getHeader(Headers.Request_Body, StudentDto.class);
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("firstName", dto.getFirstName());
                    studentMap.put("lastName", dto.getLastName());
                    studentMap.put("nationalCode", dto.getNationalCode());
                    studentMap.put("email", dto.getEmail());
                    studentMap.put("studentNumber", dto.getStudentNumber());
                    studentMap.put("age", dto.getAge());
                    exchange.getIn().setBody(studentMap);
                })
                .to("sql:insert into students (firstName, lastName, nationalCode, email, studentNumber, age) values ( :#firstName , :#lastName , :#nationalCode , :#email , :#studentNumber , :#age )")
                .log("Response ====> ${in.body}")
                .process(exchange -> {
                    StudentAddResponse addResponse = new StudentAddResponse();
                    addResponse.setCode(0);
                    addResponse.setText("عملیات با موفقیت انجام شد");
                    exchange.getIn().setBody(addResponse);
                })
                .removeHeader(Headers.Request_Body)
                .removeHeader(Headers.NationalCode)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }
}
