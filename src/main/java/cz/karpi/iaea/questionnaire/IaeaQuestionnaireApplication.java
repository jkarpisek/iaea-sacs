package cz.karpi.iaea.questionnaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
        webView.getEngine().load("http://localhost:8070");

        primaryStage.setScene(new Scene(webView));
        primaryStage.setTitle("IAEA");
        primaryStage.setOnCloseRequest(this::closeHandler);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void closeHandler(WindowEvent event) {
        final ThreadPoolTaskExecutor taskExecutor = appContext.getBean("asyncExecutor", ThreadPoolTaskExecutor.class);
        if (taskExecutor.getActiveCount() != 0) {
            event.consume();
            webView.getEngine().executeScript("savingInProgress()");
        } else {
            taskExecutor.shutdown();
            SpringApplication.exit(appContext, () -> 0);
        }
    }
}
