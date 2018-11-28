package com.iovation.service.clearkey.replayer.config;

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <tt>SwaggerConfiguration</tt> is a Swagger configuration class for enabling
 * Swagger while running Custard in 'swagger' profile. If application is not run
 * in 'swagger' profile, no json API is generated. It will throw a '404 Not
 * Found' error while trying to access http://&lthost&gt:&ltport&gt/v2/api-docs?group=auth.
 * However the Swagger UI page is still accessible but without any data,
 * http://&lthost&gt:&ltport&gt/swagger-ui.html
 * <p/>
 *
 * @author Alice martin
 * @since 4/6/17
 */
@Configuration
@Profile(value = {"swagger"})
@EnableSwagger2
public class SwaggerConfiguration {

    @Value("${info.version}")
    private String version;

    private static Predicate<RequestHandler> exactPackage(final String pkg) {
        return input -> declaringClass(input).getPackage().getName().equals(pkg);
    }

    private static Class<?> declaringClass(RequestHandler input) {
        return input.getHandlerMethod().getMethod().getDeclaringClass();
    }

    /**
     * Creates the Swagger Docket (configuration) bean.
     *
     * @return docket bean
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(exactPackage("com.iovation.service.replayer.rest"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo("Tlm-replayer API", "API TLM Transformation Service"));
    }

    /**
     * Creates an object containing API information including version name,
     * license, etc.
     *
     * @param title       API title
     * @param description API description
     * @return API information
     */
    private ApiInfo apiInfo(String title, String description) {
        Contact contact = new Contact("Iovation Service Team", "",
                "servicedev@iovation.com");
        return ApiInfo.DEFAULT;
    }
}