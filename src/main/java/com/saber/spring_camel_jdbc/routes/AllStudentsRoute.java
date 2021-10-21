package com.saber.spring_camel_jdbc.routes;

import com.saber.spring_camel_jdbc.dto.ServiceErrorResponse;
import com.saber.spring_camel_jdbc.dto.StudentResponse;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
public class AllStudentsRoute  extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();
        rest("/student")
                .get("/findAll")
                .id("findAll-student-route")
                .responseMessage().code(200).responseModel(StudentResponse.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServiceErrorResponse.class).endResponseMessage()
                .produces(MediaType.APPLICATION_JSON)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true)
                .route()
                .routeId("findAll-student-route")
                .log("get all student from table ")
                .process(exchange -> {
                   exchange.getIn().setBody("select * from students");
                })
                .to("jdbc:studentDataSource")
                .log("Response ====> ${in.body}")
                .marshal().json(JsonLibrary.Jackson)
                .process(exchange -> {
                    String response = exchange.getIn().getBody(String.class);
                    response = String.format("{\"response\":%s}",response);
                    exchange.getIn().setBody(response);
                })
                .log("Response  studentResponse ====> ${in.body}")
                .unmarshal().json(JsonLibrary.Jackson,StudentResponse.class)
                .setHeader(Exchange.HTTP_RESPONSE_CODE,constant(200));
    }
}
