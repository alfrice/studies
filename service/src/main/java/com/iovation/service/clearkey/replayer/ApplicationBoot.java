package com.iovation.service.clearkey.replayer;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan({"com.iovation.service.clearkey.replayer.config",
        "com.iovation.service.clearkey.replayer.rest",
        "com.iovation.service.clearkey.replayer.service"})

public class ApplicationBoot {
    public static void main(String[] args) throws Exception {


        new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.CONSOLE)
                .sources(ApplicationBoot.class)
                .run(args);
    }
}
