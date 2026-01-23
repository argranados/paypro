package com.ciberaccion.paypro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.ciberaccion.paypro.security.ApiKeyFilter;

@Configuration
public class SecurityConfig {

    //     @Bean
    // public FilterRegistrationBean<ApiKeyFilter> apiKeyFilter() {
    //     FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new ApiKeyFilter());
    //     registrationBean.addUrlPatterns("/payments/*"); // protege endpoints de pagos
    //     return registrationBean;
    // }

}
