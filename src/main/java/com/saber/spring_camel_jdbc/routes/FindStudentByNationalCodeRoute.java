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
public class FindStudentByNationalCodeRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();

        rest("/student")
                .get("/findByNationalCode/{nationalCode}")
                .id("findByNationalCode-route")
                .responseMessage().code(200).responseModel(StudentResponse.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServiceErrorResponse.class).endResponseMessage()
                .produces(MediaType.APPLICATION_JSON)
                .param().name("nationalCode").type(RestParamType.path).dataType("string").required(true).endParam()
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .route()
                .routeId("findByNationalCode-route")
                .to("direct:find-by-nationalCode-route")
                .to("direct:find-student-by-nationalCode-response");

        from("direct:find-by-nationalCode-route")
                .log("find student by nationalCode ===> ${in.header.nationalCode}")
                .process(exchange -> {
                    String nationalCode = exchange.getIn().getHeader(Headers.NationalCode, String.class);
                    exchange.getIn().setBody(String.format("select * from students where nationalCode=%s", nationalCode));
                })
                .to("jdbc:studentDataSource");


        from("direct:find-student-by-nationalCode-response")
                .log("Response find-student-by-nationalCode ====> ${in.body}")
                .marshal().json(JsonLibrary.Jackson)
                .process(exchange -> {
                    String nationalCode = exchange.getIn().getHeader(Headers.NationalCode, String.class);
                    String response = exchange.getIn().getBody(String.class);
                    if (response.trim().equals("[ ]")) {
                        throw new ResourceNotFoundException(String.format("Student with nationalCode %s not found", nationalCode) ,"/camel/student/findByNationalCode");
                    }
                    response = String.format("{\"response\":%s}", response);
                    exchange.getIn().setBody(response);
                })
                .log("Response  studentResponse ====> ${in.body}")
                .unmarshal().json(JsonLibrary.Jackson, StudentResponse.class)
                .removeHeader(Headers.NationalCode)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }
}
