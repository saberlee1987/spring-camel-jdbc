package com.saber.spring_camel_jdbc.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RouteDefinition extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration()
                .contextPath("/camel")
                .apiContextPath("/student-docs/v2/api-docs")
                .enableCORS(true)
                .apiProperty("api.title", "Student Service")
                .apiProperty("api.version", "version 1.0")
                .apiProperty("cors", "true")
                .component("servlet")
                .dataFormatProperty("prettyPrint", "true")
                .bindingMode(RestBindingMode.json);
    }
}
