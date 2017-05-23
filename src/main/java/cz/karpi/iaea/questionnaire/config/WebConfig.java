package cz.karpi.iaea.questionnaire.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cz.karpi.iaea.questionnaire.web.interceptor.FlowInterceptor;

/**
 * Created by karpi on 2.5.17.
 */
@Configuration
@EnableCaching
public class WebConfig extends WebMvcConfigurerAdapter {
    private final FlowInterceptor flowInterceptor;

    @Autowired
    public WebConfig(FlowInterceptor flowInterceptor) {
        this.flowInterceptor = flowInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(flowInterceptor);
    }
}
