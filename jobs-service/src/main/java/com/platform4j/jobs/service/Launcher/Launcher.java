package com.platform4j.jobs.service.Launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
    private final static Logger LOGGER = LogManager.getLogger();

    private static volatile boolean running = true;
    private static ApplicationContext ctx;

    public static void main(String[] args) {
        try {
            ctx = new ClassPathXmlApplicationContext(
                    new String[]{
                            "applicationContext-resources.xml",
                            "applicationContext-quartz.xml",
                            "dubbo-provider.xml",
                            "dubbo-consumer.xml"
                    }
            );
            LOGGER.info("==== Platforms-jobs 启动完成！====");

        } catch(RuntimeException e) {
            e.printStackTrace();
            running = false;
            LOGGER.error(e.getMessage(), e);
        }

        synchronized(Launcher.class) {
            while(running) {
                try {
                    Launcher.class.wait();
                } catch(Throwable e) {
                }
            }
        }
    }
}
