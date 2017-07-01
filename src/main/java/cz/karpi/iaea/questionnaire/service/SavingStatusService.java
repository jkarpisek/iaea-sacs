package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Created by karpi on 30.6.17.
 */
@Service
public class SavingStatusService {

    private final ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public SavingStatusService(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void waitForSave() {
        while (taskExecutor.getActiveCount() > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer savingProgress() {
        return Long.valueOf(100 * taskExecutor.getThreadPoolExecutor().getCompletedTaskCount() / taskExecutor.getThreadPoolExecutor().getTaskCount()).intValue();
    }

    public boolean isSaving() {
        return taskExecutor.getActiveCount() != 0;
    }

    public void shutdown() {
        taskExecutor.shutdown();
    }
}
