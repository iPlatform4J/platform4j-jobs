package com.platform4j.jobs.service.common;

import com.alibaba.dubbo.common.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Set;

public class SchedulerJob implements ApplicationContextAware {

    private final static Logger LOGGER = LogManager.getLogger();

    private static ApplicationContext applicationContext; // Spring应用上下文环境

    //记录是否修改过
    public static HashMap<String, String> DynamicMap = new HashMap<String, String>();

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取 SchedulerFactoryBean 对象
     *
     * @return
     */
    public static Scheduler getSchedulerFactoryBean() {
        return (Scheduler) applicationContext.getBean("schedulerFactoryBean");
    }

    public static void putMapBoolean(String key, boolean status) {
        if(StringUtils.isBlank(key)){
            return;
        }

        String source = DynamicMap.get(key);

        //之前没改过说明正在启用，若做启用操作则直接退出
        if(StringUtils.isEmpty(source) && status){
            return;
        }

        //之前没改过说明正在启用，若做停止操作则添加map信息
        if(StringUtils.isEmpty(source) && !status){
            DynamicMap.put(key, "off");
            return;
        }

        String target = "off";
        if(status){
            target = "on";
        }

        //之前改过看当前状态，若目标状态与当前状态不相等，且目标状态是启用，则将启用状态则添加map信息
        if(!StringUtils.isEmpty(source) && !source.equalsIgnoreCase(target) && "on".equalsIgnoreCase(target)){
            DynamicMap.put(key, "on");
        }

        //之前改过看当前状态，若目标状态与当前状态不相等，且目标状态是启用，则将停止状态则添加map信息
        if(!StringUtils.isEmpty(source) && !source.equalsIgnoreCase(target) && !"on".equalsIgnoreCase(target)){
            DynamicMap.put(key, "off");
        }
    }

    /**
     * 方法描述:  获取所有的TriggerKeys
     * @return
     * @throws Exception
     */
    public static Set<TriggerKey> getAllTriggerKeys() throws Exception {
        Scheduler scheduler = getSchedulerFactoryBean();
        return scheduler.getTriggerKeys(GroupMatcher.<TriggerKey>groupEquals(Scheduler.DEFAULT_GROUP));
    }

    /**
     * 方法描述:  获取所有的CronTriggers
     * @return
     * @throws Exception
     */
    public static CronTrigger getCronTriggerByKey(TriggerKey triggerKey) throws Exception {
        Scheduler scheduler = getSchedulerFactoryBean();
        return (CronTrigger) scheduler.getTrigger(triggerKey);
    }

    /**
     * 方法描述:  更改任务时间
     *
     * @param triggerName
     * @param cronExpression
     * @throws Exception 返回类型： void
     */
    public static void changeTrigger(String triggerName, String cronExpression) throws Exception {

        Scheduler scheduler = getSchedulerFactoryBean();
        TriggerKey triggerKey = new TriggerKey(triggerName, Scheduler.DEFAULT_GROUP);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        if (trigger == null) {
            return;
        }

        String oldCron = trigger.getCronExpression();
        //时间没有更新直接返回
        if (oldCron.equalsIgnoreCase(cronExpression)) {
            return;
        }

        LOGGER.info("changeTrigger triggerName={}, cronExpression={}", triggerName, cronExpression);
        //表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        //按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        //按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 方法描述:  暂停触发器
     *
     * @param triggerName
     * @throws Exception 返回类型： void
     */
    public static void pauseTrigger(String triggerName) throws Exception {
        LOGGER.info("pauseTrigger triggerName={}", triggerName);
        Scheduler scheduler = getSchedulerFactoryBean();
        TriggerKey triggerKey = new TriggerKey(triggerName, Scheduler.DEFAULT_GROUP);
        scheduler.pauseTrigger(triggerKey);
    }

    /**
     * 方法描述: 恢复触发器
     *
     * @param triggerName
     * @throws SchedulerException 返回类型： void
     */
    public static void resumeTrigger(String triggerName) throws SchedulerException {
        LOGGER.info("resumeTrigger triggerName={}", triggerName);
        Scheduler scheduler = getSchedulerFactoryBean();
        TriggerKey triggerKey = new TriggerKey(triggerName, Scheduler.DEFAULT_GROUP);
        scheduler.resumeTrigger(triggerKey);
    }
}
