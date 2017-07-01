package cz.karpi.iaea.questionnaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;

import cz.karpi.iaea.questionnaire.service.SavingStatusService;
import javafx.application.Application;
import javafx.scene.Scene;
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
        appContext = SpringApplication.run(IaeaQuestionnaireApplication.class);

        webView = new WebView();
        webView.getEngine().load("http://" + InetAddress.getByName(null).getHostAddress() + ":" + appContext.getEnvironment().getProperty("server.port"));
        webView.setContextMenuEnabled(false);

        primaryStage.setScene(new Scene(webView));
        primaryStage.setTitle("IAEA");
        primaryStage.setOnCloseRequest(this::closeHandler);
        //primaryStage.getIcons().add(new Image("/images/iaea_logo.png"));
        primaryStage.setMaximized(true);
        primaryStage.show();
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

        /*Service<Void> service = new Service<Void>() {
            @Override protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override protected Void call() throws InterruptedException {
                        updateMessage("Message . . .");
                        updateProgress(0, 10);
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(300);
                            updateProgress(i + 1, 10);
                            updateMessage("Progress " + (i + 1) + " of 10");
                        }
                        updateMessage("End task");
                        return null;
                    }
                };
            }
        };*/
    }
}
