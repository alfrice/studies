package com.iovation.service.clearkey.replayer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * Handles the Spring JMX Configuration
 *
 * @author Alice Martin
 */
@Configuration
public class ServiceJmxConfiguration {

    @Bean(name = "exporter")
    public MBeanExporter getExporter() {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setAssembler(getAssembler());
        exporter.setNamingStrategy(getNamingStrategy());
        exporter.setAutodetect(true);
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
        return exporter;
    }

    /**
     * Looks to JDK 5 annotations for attribute information
     */
    @Bean(name = "jmxAttributeSource")
    public AnnotationJmxAttributeSource getAttributeSource() {
        return new AnnotationJmxAttributeSource();
    }

    @Bean(name = "assembler")
    public MetadataMBeanInfoAssembler getAssembler() {
        MetadataMBeanInfoAssembler assembler = new MetadataMBeanInfoAssembler();
        assembler.setAttributeSource(getAttributeSource());
        return assembler;
    }

    @Bean(name = "namingStrategy")
    public MetadataNamingStrategy getNamingStrategy() {
        MetadataNamingStrategy strategy = new MetadataNamingStrategy();
        strategy.setAttributeSource(getAttributeSource());
        return strategy;
    }
}
