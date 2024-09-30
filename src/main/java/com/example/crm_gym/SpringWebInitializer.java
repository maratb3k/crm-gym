package com.example.crm_gym;

import com.example.crm_gym.config.AppConfig;
import com.example.crm_gym.logger.RequestResponseLoggingFilter;
import com.example.crm_gym.config.SwaggerConfiguration;
import jakarta.servlet.Filter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class SpringWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class[] getServletConfigClasses() {
        return new Class[] { AppConfig.class, SwaggerConfiguration.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Class[] getRootConfigClasses() {
        return new Class[] {};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] { new RequestResponseLoggingFilter() };
    }
}
