package com.platform4j.jobs.api;

import java.util.Map;

public interface JobManager {

    Map<String, String> listAllStatus();

    Map<String, String> listAllCrons();

    boolean pauseTrigger(String key);

    boolean resumeTrigger(String key);

    boolean pauseAll();

    boolean resumeAll();

    boolean changeTrigger(String job, String cron);
}
