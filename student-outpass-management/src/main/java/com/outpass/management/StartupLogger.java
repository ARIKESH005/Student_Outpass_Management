package com.outpass.management;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartupLogger implements ApplicationRunner {

    private final Environment env;
    private final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

    public StartupLogger(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        String url = env.getProperty("spring.datasource.url");
        String user = env.getProperty("spring.datasource.username");
        String pass = env.getProperty("spring.datasource.password");
        String maskedPass = (pass == null || pass.isEmpty()) ? "<not-set>" : "****";
        logger.info("Datasource connection: url='{}', user='{}', password='{}'", url, user, maskedPass);
    }
}
