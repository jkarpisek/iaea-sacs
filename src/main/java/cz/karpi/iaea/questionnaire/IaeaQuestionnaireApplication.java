package cz.karpi.iaea.questionnaire;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import cz.karpi.iaea.questionnaire.service.SavingStatusService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@SpringBootApplication
public class IaeaQuestionnaireApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(IaeaQuestionnaireApplication.class);

    private ApplicationContext appContext;
    private WebView webView;
    private Service<Void> closeService;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        webView = new WebView();
        webView.setContextMenuEnabled(false);
        webView.getEngine().load(getClass().getClassLoader().getResource("static/images/sacs_logo_small.jpg").toString());
        primaryStage.setScene(new Scene(webView));
        primaryStage.setTitle("IAEA Assistance programme on Nuclear Security Response Capabilities");
        primaryStage.setOnCloseRequest(this::closeHandler);
        primaryStage.getIcons().add(new Image("/static/images/iaea_logo.png"));
        primaryStage.setMaximized(true);
        primaryStage.show();
        getStartService().start();
    }

    private Service<Void> getStartService() {
        final Service<Void> startService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override protected Void call() throws InterruptedException {
                        appContext = SpringApplication.run(IaeaQuestionnaireApplication.class);
                        return null;
                    }
                };
            }
        };
        startService.setOnSucceeded(event -> webView.getEngine().load("http://127.0.0.1:" + appContext.getEnvironment().getProperty("server.port")));
        startService.setOnFailed(event -> webView.getEngine().loadContent("Start application failed."));
        return startService;
    }

    private void closeHandler(WindowEvent event) {
        event.consume();
        if (!getCloseService().isRunning()) {
            try {
                webView.getEngine().executeScript("showSavingProgressAlert()");
            } catch (Exception e) {
                LOGGER.error("Execute script failed", e);
            }
            getCloseService().start();
        }
    }

    private Service<Void> getCloseService() {
        if (closeService == null) {
            closeService = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws InterruptedException {
                            final SavingStatusService savingStatusService = appContext.getBean(SavingStatusService.class);
                            savingStatusService.waitForSave();
                            return null;
                        }
                    };
                }
            };
            closeService.setOnSucceeded(this::exitApplication);
            closeService.setOnFailed(event1 -> webView.getEngine().loadContent("Close application failed."));
        }
        return closeService;
    }

    private void exitApplication(Event event) {
        final SavingStatusService savingStatusService = appContext.getBean(SavingStatusService.class);
        savingStatusService.shutdown();
        SpringApplication.exit(appContext, () -> 0);
        Platform.exit();
    }
}
