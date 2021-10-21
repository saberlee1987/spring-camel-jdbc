package com.saber.spring_camel_jdbc.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saber.spring_camel_jdbc.dto.ServiceErrorResponse;
import com.saber.spring_camel_jdbc.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
public class AbstractRestRouteBuilder extends RouteBuilder {
    @Autowired
    private ObjectMapper mapper;

    @Override
    public void configure() throws Exception {

        onException(SQLIntegrityConstraintViolationException.class)
                .maximumRedeliveries(0)
                .handled(true)
                .process(exchange -> {
                    SQLIntegrityConstraintViolationException exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
                            SQLIntegrityConstraintViolationException.class);
                    String errorBody = exception.getMessage();
                    int errorCode = exception.getErrorCode();
                    String sqlState = exception.getSQLState();

                    ServiceErrorResponse errorResponse = new ServiceErrorResponse();
                    errorResponse.setCode(2);
                    errorResponse.setMessage("خطای سرویس students");
                    errorResponse.setOriginalMessage(String.format("{\"code\":%d,\"text\":\"%s\",\"sqlState\":\"%s\"}",
                            errorCode, errorBody, sqlState));

                    log.error("Error when add to table ====> {}", mapper.writeValueAsString(errorResponse));
                    exchange.getIn().setBody(errorResponse);

                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(406));


        onException(ResourceNotFoundException.class)
                .maximumRedeliveries(0)
                .handled(true)
                .process(exchange -> {
                    ResourceNotFoundException exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
                            ResourceNotFoundException.class);
                    String errorBody = exception.getMessage();
                    int errorCode =406;
                    String sqlState ="406";


                    ServiceErrorResponse errorResponse = new ServiceErrorResponse();
                    errorResponse.setCode(2);
                    errorResponse.setMessage("خطای سرویس students");
                    errorResponse.setOriginalMessage(String.format("{\"code\":%d,\"text\":\"%s\",\"sqlState\":\"%s\"}",
                            errorCode, errorBody, sqlState));

                    log.error("Error when add to table ====> {}", mapper.writeValueAsString(errorResponse));
                    exchange.getIn().setBody(errorResponse);

                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(406));
    }
}
