package com.saber.spring_camel_jdbc.routes;

import com.saber.spring_camel_jdbc.dto.HelloDto;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
public class HelloRoute
        extends RouteBuilder
{
    @Override
    public void configure() throws Exception {


        rest("/hello")
                .get("/sayHello")
                .id("hello-route")
                .produces(MediaType.APPLICATION_JSON)
                .responseMessage().code(200).responseModel(HelloDto.class).endResponseMessage()
                .param().name("firstName").type(RestParamType.query).dataType("string").required(true).endParam()
                .param().name("lastName").type(RestParamType.query).dataType("string").required(true).endParam()
                .route()
                .routeId("hello-route")
                .process(exchange -> {
                    String firstName = exchange.getIn().getHeader("firstName", String.class);
                    String lastName = exchange.getIn().getHeader("firstName", String.class);

                    HelloDto helloDto = new HelloDto();
                    helloDto.setMessage(String.format("Hello %s %s", firstName, lastName));
                    exchange.getIn().setBody(helloDto);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

    }
}
