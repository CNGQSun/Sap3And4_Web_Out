package com.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyEnvironmentPostProcessor implements EnvironmentPostProcessor {
    //此路径可自己任意配置
    private final String path = "E:\\configs";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment configurableEnvironment, SpringApplication springApplication) {
        try {
            File directory = new File(path);
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        configurableEnvironment.getPropertySources().addFirst(loadProperties(file));
                    }
                }
            } else {
                configurableEnvironment.getPropertySources().addLast(loadProperties(directory));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PropertiesPropertySource loadProperties(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(inputStream);
        //读取多个配置文件的时候，每个PropertiesPropertySource的name必须不同，同名的会覆盖
        return new PropertiesPropertySource(file.getName(), properties);
    }
}

