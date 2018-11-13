package com.github.alfrice.service.acceleration;

        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import org.springframework.boot.builder.SpringApplicationBuilder;
        import org.springframework.boot.context.properties.EnableConfigurationProperties;
        import org.springframework.context.annotation.ComponentScan;
        import org.springframework.scheduling.annotation.EnableAsync;
        import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableAsync
//@EntityScan(basePackages = {"com.github.alfrice.service.acceleration.dao"})
//@EnableJpaRepositories(basePackages = {"com.github.alfrice.service.acceleration.dao"})
@ComponentScan(basePackages = {
        "com.github.alfrice.service.acceleration.config",
        "com.github.alfrice.service.acceleration.service",
        "com.github.alfrice.service.acceleration.dao",
        "com.github.alfrice.service.acceleration.controller"})
public class Application {
    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(Application.class)
                .run(args);
    }
}