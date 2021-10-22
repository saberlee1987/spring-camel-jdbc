package com.saber.spring_camel_jdbc.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saber.spring_camel_jdbc.dto.ServiceErrorResponse;
import com.saber.spring_camel_jdbc.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

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
                    Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
                            Exception.class);
                    ServiceErrorResponse errorResponse = new ServiceErrorResponse();
                    errorResponse.setCode(2);
                    errorResponse.setMessage("خطای سرویس students");
                    if (exception instanceof DuplicateKeyException) {

                        DuplicateKeyException e = (DuplicateKeyException) exception;
                        String errorBody = e.getMessage();
                        int errorCode = 406;
                        errorResponse.setOriginalMessage(String.format("{\"code\":%d,\"text\":\"%s\"}",
                                errorCode, errorBody));
                    } else if (exception instanceof SQLIntegrityConstraintViolationException) {
                        SQLIntegrityConstraintViolationException e = (SQLIntegrityConstraintViolationException) exception;
                        String errorBody = e.getMessage();
                        int errorCode = e.getErrorCode();
                        errorResponse.setOriginalMessage(String.format("{\"code\":%d,\"text\":\"%s\"}",
                                errorCode, errorBody));
                    } else {
                        String errorBody = exception.getMessage();
                        int errorCode = 406;
                        errorResponse.setOriginalMessage(String.format("{\"code\":%d,\"text\":\"%s\"}",
                                errorCode, errorBody));
                    }

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
                    int errorCode = 406;
                    String sqlState = "406";


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
