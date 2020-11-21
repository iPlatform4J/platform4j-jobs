package com.platform4j.jobs.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonJob {
    private final static Logger LOGGER = LogManager.getLogger(CommonJob.class);

    public void execute(){
        LOGGER.info("Job 启动！");
    }
}
