package com.saber.spring_camel_jdbc.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import java.beans.PropertyVetoException;

@Configuration
public class AppConfig {

    @Bean(value = "studentDataSource")
    public ComboPooledDataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource =new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test2");
        dataSource.setUser("saber66");
        dataSource.setPassword("AdminSaber66");

        dataSource.setMinPoolSize(40);
        dataSource.setMaxPoolSize(100);
        dataSource.setAcquireIncrement(10);
        dataSource.setCheckoutTimeout(10000);
        dataSource.setInitialPoolSize(50);
        dataSource.setAcquireRetryDelay(600);
        dataSource.setMaxConnectionAge(30);
        dataSource.setMaxIdleTime(30);
        dataSource.setMaxStatementsPerConnection(100);
        dataSource.setIdleConnectionTestPeriod(180);
        dataSource.setTestConnectionOnCheckout(false);
        dataSource.setTestConnectionOnCheckin(true);

        dataSource.setNumHelperThreads(100);
        return dataSource;
    }

    @Bean(value = "mapper")
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper;
    }
    @Bean
    public PlatformTransactionManager transactionManager() throws PropertyVetoException {
        return new DataSourceTransactionManager(dataSource());
    }
    @Bean
    public SpringTransactionPolicy propagationRequired(PlatformTransactionManager transactionManager){
        SpringTransactionPolicy transactionPolicy = new SpringTransactionPolicy();
        transactionPolicy.setTransactionManager(transactionManager);
        transactionPolicy.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionPolicy;
    }
    @Bean
    public SqlComponent sql() throws PropertyVetoException {
        SqlComponent sql = new SqlComponent();
        sql.setDataSource(dataSource());
        return sql;
    }
}
