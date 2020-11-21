package com.platform4j.jobs.service.jobmanager;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.platform4j.jobs.api.JobManager;
import com.platform4j.jobs.service.common.SchedulerJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component("jobManager")
public class JobManagerImpl implements JobManager {

    private final static Logger LOGGER = LogManager.getLogger();

    public Map<String, String> listAllStatus(){
        Map<String, String> result = new HashMap<String, String>();

        try {
            Set<TriggerKey> triggerKeys = SchedulerJob.getAllTriggerKeys();
            for (TriggerKey triggerKey : triggerKeys) {
                if(!SchedulerJob.DynamicMap.containsKey(triggerKey.getName())){
                    result.put(triggerKey.getName(), "on");
                }else{
                    result.put(triggerKey.getName(), SchedulerJob.DynamicMap.get(triggerKey.getName()));
                }
            }
        } catch(Exception e){
            LOGGER.error("JobManager listAllStatus error, {}", e);
        }

        return result;
    }

    public Map<String, String> listAllCrons(){
        Map<String, String> result = new HashMap<String, String>();

        try {
            Set<TriggerKey> triggerKeys = SchedulerJob.getAllTriggerKeys();
            for (TriggerKey triggerKey : triggerKeys) {
                CronTrigger cronTrigger = SchedulerJob.getCronTriggerByKey(triggerKey);
                result.put(triggerKey.getName(), cronTrigger.getCronExpression());
            }
        } catch(Exception e){
            LOGGER.error("JobManager listAllCrons error, {}", e);
        }

        return result;
    }

    public boolean pauseTrigger(String triggerName){
        try {
            SchedulerJob.pauseTrigger(triggerName);
        } catch(Exception e){
            LOGGER.error("JobManager pauseTrigger error, {}", e);
        }
        SchedulerJob.putMapBoolean(triggerName, false);
        return true;
    }

    public boolean resumeTrigger(String triggerName){
        try {
            SchedulerJob.resumeTrigger(triggerName);
        } catch(Exception e){
            LOGGER.error("JobManager resumeTrigger error, {}", e);
        }
        SchedulerJob.putMapBoolean(triggerName, true);
        return true;
    }

    public boolean pauseAll(){
        try {
            Set<TriggerKey> triggerKeys = SchedulerJob.getAllTriggerKeys();
            for (TriggerKey triggerKey : triggerKeys) {
                SchedulerJob.pauseTrigger(triggerKey.getName());
            }
        } catch(Exception e){
            LOGGER.error("JobManager pauseAll error, {}", e);
        }
        return true;
    }

    public boolean resumeAll(){
        try {
            Set<TriggerKey> triggerKeys = SchedulerJob.getAllTriggerKeys();
            for (TriggerKey triggerKey : triggerKeys) {
                SchedulerJob.resumeTrigger(triggerKey.getName());
            }
        } catch(Exception e){
            LOGGER.error("JobManager resumeAll error, {}", e);
        }
        return true;
    }

    public boolean changeTrigger(String triggerName, String cron){
        if(StringUtils.isBlank(triggerName) || StringUtils.isBlank(cron)){
            return false;
        }
        try {
            SchedulerJob.changeTrigger(triggerName, cron);
        } catch(Exception e){
            LOGGER.error("JobManager changeTrigger error, {}", e);
        }
        return true;
    }
}
