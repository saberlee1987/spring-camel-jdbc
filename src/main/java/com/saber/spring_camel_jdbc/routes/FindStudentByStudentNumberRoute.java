package com.saber.spring_camel_jdbc.routes;

import com.saber.spring_camel_jdbc.dto.ServiceErrorResponse;
import com.saber.spring_camel_jdbc.dto.StudentResponse;
import com.saber.spring_camel_jdbc.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
@Slf4j
public class FindStudentByStudentNumberRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();
        rest("/student")
                .get("/findByStudentNumber/{studentNumber}")
                .id("findByStudentNumber-route")
                .responseMessage().code(200).responseModel(StudentResponse.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServiceErrorResponse.class).endResponseMessage()
                .produces(MediaType.APPLICATION_JSON)
                .param().name("studentNumber").type(RestParamType.path).dataType("string").required(true).endParam()
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .route()
                .routeId("findByStudentNumber-route")
                .to("direct:find-by-studentNumber-route")
                .to("direct:find-student-by-studentNumber-response");

        from("direct:find-by-studentNumber-route")
                .log("find student by studentNumber  ===> ${in.header.studentNumber}  ")
                .process(exchange -> {
                    String studentNumber = exchange.getIn().getHeader(Headers.StudentNumber,String.class);
                   exchange.getIn().setBody(String.format("select * from students where studentNumber=%s", studentNumber));
                })
                .to("jdbc:studentDataSource");


        from("direct:find-student-by-studentNumber-response")
                .log("Response find-student-by-studentNumber ====> ${in.body}")
                .marshal().json(JsonLibrary.Jackson)
                .process(exchange -> {
                    String response = exchange.getIn().getBody(String.class);
                    String studentNumber = exchange.getIn().getHeader(Headers.StudentNumber,String.class);
                    if (response.trim().equals("[ ]")){
                        throw new ResourceNotFoundException(String.format("Student with studentNumber %s not found",studentNumber) ,"/camel/student/findByStudentNumber");
                    }
                    response = String.format("{\"response\":%s}",response);
                    exchange.getIn().setBody(response);
                })
                .log("Response  find-student-by-studentNumber ====> ${in.body}")
                .unmarshal().json(JsonLibrary.Jackson,StudentResponse.class)
                .removeHeader(Headers.StudentNumber)
                .setHeader(Exchange.HTTP_RESPONSE_CODE,constant(200));
    }
}
