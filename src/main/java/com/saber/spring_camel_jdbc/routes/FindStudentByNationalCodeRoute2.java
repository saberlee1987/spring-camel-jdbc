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
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FindStudentByNationalCodeRoute2 extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();
        rest("/student")
                .get("/findByNationalCode2/{nationalCode}")
                .id("findByNationalCode2-route")
                .responseMessage().code(200).responseModel(StudentResponse.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServiceErrorResponse.class).endResponseMessage()
                .produces(MediaType.APPLICATION_JSON)
                .param().name("nationalCode").type(RestParamType.path).dataType("string").required(true).endParam()
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .route()
                .routeId("findByNationalCode2-route")
                .log("get all student from table ")
                .process(exchange -> {
                    String nationalCode = exchange.getIn().getHeader("nationalCode",String.class);
                    Map<String,Object> map = new HashMap<>();
                    map.put("nationalCode",nationalCode);
                    exchange.getIn().setBody(map);
                })
                .to("sql:select * from students where nationalCode=:#nationalCode")
                .log("Response ====> ${in.body}")
                .marshal().json(JsonLibrary.Jackson)
                .process(exchange -> {
                    String nationalCode = exchange.getIn().getHeader("nationalCode",String.class);
                    String response = exchange.getIn().getBody(String.class);
                    if (response.trim().equals("[ ]")){
                       throw new ResourceNotFoundException(String.format("Student with nationalCode %s not found",nationalCode));
                    }
                    response = String.format("{\"response\":%s}",response);
                    exchange.getIn().setBody(response);
                })
                .log("Response  studentResponse ====> ${in.body}")
                .unmarshal().json(JsonLibrary.Jackson,StudentResponse.class)
                .setHeader(Exchange.HTTP_RESPONSE_CODE,constant(200));
    }
}
