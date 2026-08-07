package com.algaworks.algafoodapi.core.squiggly;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nicklasw.squiggly.Squiggly;
import dev.nicklasw.squiggly.web.RequestSquigglyContextProvider;
import dev.nicklasw.squiggly.web.SquigglyRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SquigglyConfig {

    @Bean
    public FilterRegistrationBean<SquigglyRequestFilter> squigglyRequestFilter(ObjectMapper objectMapper) {
        Squiggly.init(objectMapper, new RequestSquigglyContextProvider("campos", null));

        var urlPatterns = Arrays.asList("/pedidos/*", "/restaurantes/*");

        var filterRegistration = new FilterRegistrationBean<SquigglyRequestFilter>();
        filterRegistration.setFilter(new SquigglyRequestFilter());
        filterRegistration.setOrder(1);
        filterRegistration.setUrlPatterns(urlPatterns);

        return filterRegistration;
    }
}
