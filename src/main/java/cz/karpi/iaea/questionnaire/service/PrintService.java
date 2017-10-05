package cz.karpi.iaea.questionnaire.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerOverviewTo;
import cz.karpi.iaea.questionnaire.web.converter.ViewConverter;

@Service
public class PrintService {

    private final TemplateEngine templateEngine;

    private final ViewConverter viewConverter;

    @Autowired
    public PrintService(TemplateEngine templateEngine, ViewConverter viewConverter) {
        this.templateEngine = templateEngine;
        this.viewConverter = viewConverter;
    }

    public void print(CommonTo commonTo, PlannerOverviewTo plannerOverviewTo) {
        File file = createFile(commonTo.getCompanyName());
        generateContent(commonTo, plannerOverviewTo, file);
        openBrowserToPrint(file);
    }

    private File createFile(String companyName) {
        try {
            return File.createTempFile("IAEA-" + companyName + "-", ".html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateContent(CommonTo commonTo, PlannerOverviewTo plannerOverviewTo, File file) {
        final Context context = new Context();
        context.setVariable("common", viewConverter.toCommonVo(commonTo));
        context.setVariable("meta", viewConverter.toCdpMetaVo(plannerOverviewTo));
        final String content = templateEngine.process("print", context);
        try (OutputStream outputStream = FileUtils.openOutputStream(file)) {
            IOUtils.write(content, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openBrowserToPrint(File file) {
        try {
            String url = file.toURI() + "?print";
            if (Desktop.isDesktopSupported()) {
                // Windows
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                // Ubuntu
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("chromium-browser " + url);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}