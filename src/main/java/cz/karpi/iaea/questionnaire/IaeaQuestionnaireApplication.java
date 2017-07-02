package cz.karpi.iaea.questionnaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import cz.karpi.iaea.questionnaire.service.SavingStatusService;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@SpringBootApplication
public class IaeaQuestionnaireApplication extends Application {

    private ApplicationContext appContext;
    private WebView webView;

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
        final Service<Void> service = new Service<Void>() {
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
        service.addEventHandler(EventType.ROOT, this::eventHandler);
        service.start();
    }

    private void eventHandler(Event event) {
        if (event.getEventType().getName().equals("WORKER_STATE_SUCCEEDED")) {
            webView.getEngine().load("http://127.0.0.1:" + appContext.getEnvironment().getProperty("server.port"));
        } else if (event.getEventType().getName().equals("WORKER_STATE_FAILED")) {
            webView.getEngine().loadContent("Start application failed.");
        }
    }

    private void closeHandler(WindowEvent event) {
        final SavingStatusService savingStatusService = appContext.getBean(SavingStatusService.class);
        if (savingStatusService.isSaving()) {
            event.consume();
            webView.getEngine().executeScript("savingInProgress('" + savingStatusService.savingProgress() + "%')");
        } else {
            savingStatusService.shutdown();
            SpringApplication.exit(appContext, () -> 0);
        }

    }
}
