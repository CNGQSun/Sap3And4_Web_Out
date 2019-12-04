package com;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 4.3 ，4.4 自动job的启动入口
 * 4.3 job src\main\java\com\merck\job
 * 4.4 job src\main\java\com\sap
 */
@SpringBootApplication
@ComponentScan
@MapperScan("com.*.dao")
//@PropertySource(value = {"classpath:config.properties", "file:${spring.config.location}/config.properties"}, ignoreResourceNotFound = true)
public class AppMainAll extends SpringBootServletInitializer {
    private static Logger log = LoggerFactory.getLogger(AppMainAll.class);
    /**
     * 为打war包重写configure
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AppMainAll.class);
    }
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AppMainAll.class, args);
        log.info("springBoot容器已经启动");
    }
}
