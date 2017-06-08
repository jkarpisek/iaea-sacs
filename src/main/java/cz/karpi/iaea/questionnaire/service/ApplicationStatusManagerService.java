package cz.karpi.iaea.questionnaire.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by karpi on 8.6.17.
 */
@Component
public class ApplicationStatusManagerService implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStatusManagerService.class);

    private final ApplicationContext appContext;

    @Autowired
    public ApplicationStatusManagerService(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.info("Application IAEA started and ready to use");
        try {
            Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler http://localhost:8070");
        } catch (Exception e) {
            LOGGER.error("Start browser failed", e);
        }
    }

    public void closeApplication() {
        LOGGER.info("Application IAEA is ending");
        SpringApplication.exit(appContext, () -> 0);
    }
}
