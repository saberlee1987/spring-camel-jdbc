package com.saber.spring_camel_jdbc.routes;

import com.saber.spring_camel_jdbc.dto.ServiceErrorResponse;
import com.saber.spring_camel_jdbc.dto.StudentAddResponse;
import com.saber.spring_camel_jdbc.dto.StudentDto;
import com.saber.spring_camel_jdbc.exceptions.ResourceNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Component
public class DeleteStudentRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();
        rest("/student")
                .delete("/deleteByNationalCode/{nationalCode}")
                .id("delete-student-route")
                .responseMessage().code(200).responseModel(StudentDto.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServiceErrorResponse.class).endResponseMessage()
                .produces(MediaType.APPLICATION_JSON)
                .enableCORS(true)
                .bindingMode(RestBindingMode.json)
                .param().name("nationalCode").type(RestParamType.path).dataType("string").required(true).endParam()
                .route()
                .routeId("delete-student-route")
                .log("Receive Request for delete student nationalCode ====> ${in.header.nationalCode}")
                .transacted("PROPAGATION_REQUIRED")
                .to("direct:find-by-nationalCode-route")
                .process(exchange -> {
                    String nationalCode = exchange.getIn().getHeader(Headers.NationalCode, String.class);
                    String response = exchange.getIn().getBody(String.class);
                    if (response.trim().equals("[]")) {
                        throw new ResourceNotFoundException(String.format("Student with nationalCode %s not found", nationalCode) ,"/camel/student/deleteByNationalCode");
                    }
                })
                .process(exchange -> {
                    String nationalCode = exchange.getIn().getHeader(Headers.NationalCode,String.class);
                    Map<String,Object> map = new HashMap<>();
                    map.put("nationalCode",nationalCode);
                    exchange.getIn().setBody(map);
                })
                .to("sql:delete from students where nationalCode=:#nationalCode")
                .process(exchange -> {
                    StudentAddResponse addResponse = new StudentAddResponse();
                    addResponse.setCode(0);
                    addResponse.setText("عملیات با موفقیت انجام شد");
                    exchange.getIn().setBody(addResponse);
                })
                .log("Response ====> ${in.body}")
                .removeHeader(Headers.NationalCode)
                .setHeader(Exchange.HTTP_RESPONSE_CODE,constant(200));
    }
}
