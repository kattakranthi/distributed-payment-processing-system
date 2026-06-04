package com.example.paymentservice.config;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbDebugConfig {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void printDb() throws Exception {
        System.out.println("🔥 DB URL = " +
                dataSource.getConnection().getMetaData().getURL());
    }
}