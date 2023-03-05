package com.wyl.quartz.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 任务调度管理工具
 */
@Component
@Slf4j
public class QuartzManager {

    @Autowired
    private SchedulerFactoryBean sf;

    public void addJob(JobKey jobKey, TriggerKey triggerKey, Class<? extends QuartzJobBean> cls, int repeatIntevalTime,
                       JobDataMap jobDataMap, Date startTime) throws SchedulerException {

        Scheduler sched = sf.getScheduler();
        String jobType = jobDataMap.getString("jobType");
        JobDetail jd = JobBuilder.newJob(cls).withIdentity(jobKey).withDescription(jobType).setJobData(jobDataMap).build();// jobDetail

        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(repeatIntevalTime)
                                .repeatForever().withMisfireHandlingInstructionFireNow())
                .startAt(startTime).build();// triggerkey用来标识trigger身份
        sched.scheduleJob(jd, trigger);// 设置调度器

        try {

            if (!sched.isShutdown()) {
                sched.start();
            }

        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @Title:
     * @Description: 添加cronTrigger的定时器
     *
     * @param jobKey
     * @param triggerKey
     * @param jobClass
     * @param cron
     * @param jobDataMap
     *            jobDataMap
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addJob(JobKey jobKey, TriggerKey triggerKey, Class jobClass, String cron,
                       JobDataMap jobDataMap, Date startTime) {
        try {
            // 任务名，任务组，任务执行类;可以将自己需要的额外信息添加到jobdatamap中。
            String jobType = jobDataMap.getString("jobType");

            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey)
                    .withDescription(jobType)
                    .setJobData(jobDataMap).build();

            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();// 触发器

            triggerBuilder.withIdentity(triggerKey);// 触发器名,触发器组

            triggerBuilder.startAt(startTime);// 现在执行

            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));// 触发器执行规则

            CronTrigger trigger = (CronTrigger) triggerBuilder.build();// 创建CronTrigger对象

            Scheduler sched = sf.getScheduler();// 创建调度器

            sched.scheduleJob(jobDetail, trigger);// 调度容器设置JobDetail和Trigger

            if (!sched.isShutdown()) {// 启动
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @Title:
     * @Description: 修改cronTrigger定时器
     *
     * @param triggerKey
     *            trigger标识
     * @param cron
     * @throws SchedulerException
     */
    public void modifyJobTime(TriggerKey triggerKey, String cron, JobDataMap jobDataMap, Date startTime) throws SchedulerException {

        Scheduler sched = sf.getScheduler();

        try {
            // TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);

            if (trigger == null) {
                return;
            }

            String oldTime = trigger.getCronExpression();

            /** 方式一 ：调用 rescheduleJob 开始 */
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();// 触发器

            triggerBuilder.usingJobData(jobDataMap);

            triggerBuilder.withIdentity(triggerKey);// 触发器名,触发器组

            triggerBuilder.startAt(startTime);// 立即执行

            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));// 触发器时间设定

            trigger = (CronTrigger) triggerBuilder.build();// 创建Trigger对象

            sched.rescheduleJob(triggerKey, trigger);// 修改一个任务的触发时间

            /** 方式一 ：调用 rescheduleJob 结束 */

            /** 方式二：先删除，然后在创建一个新的Job */
            // JobDetail jobDetail = sched.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
            // Class<? extends Job> jobClass = jobDetail.getJobClass();
            // removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
            // addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron);
            /** 方式二 ：先删除，然后在创建一个新的Job */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @Title:
     * @Description: 只修改simpleTrigger触发器的触发时间，不更改trigger的triggerKey
     *
     * @param triggerKey
     *            trigger标识
     * @param repeatIntervalTime
     *            重复间隔时长
     * @throws SchedulerException
     */
    public void modifyJobTime(TriggerKey triggerKey, int repeatIntervalTime, JobDataMap jobDataMap,Date startTime) throws SchedulerException {

        Scheduler sched = sf.getScheduler();

        try {

            SimpleTrigger trigger = (SimpleTrigger) sched.getTrigger(triggerKey);

            if (trigger == null) {
                return;
            }

            long oldTime = trigger.getRepeatInterval();

            /** 方式一 ：调用 rescheduleJob 开始 */
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();// 触发器builder

            triggerBuilder.usingJobData(jobDataMap);

            triggerBuilder.withIdentity(triggerKey);// 触发器名,触发器组

            triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(repeatIntervalTime).repeatForever().withMisfireHandlingInstructionFireNow());// 更新触发器的重复间隔时间

            triggerBuilder.startAt(startTime);// 立即执行

            trigger = (SimpleTrigger) triggerBuilder.build();// 创建Trigger对象

            sched.rescheduleJob(triggerKey, trigger);// 修改一个任务的触发时间

            /** 方式一 ：调用 rescheduleJob 结束 */

            /** 方式二：先删除，然后在创建一个新的Job */
            // JobDetail jobDetail = sched.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
            // Class<? extends Job> jobClass = jobDetail.getJobClass();
            // removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
            // addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron);
            /** 方式二 ：先删除，然后在创建一个新的Job */

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @Title:
     * @Description: 根据job和trigger删除任务
     *
     * @param jobKey
     * @param triggerKey
     */
    public void removeJob(JobKey jobKey, TriggerKey triggerKey) {

        try {

            Scheduler sched = sf.getScheduler();

            sched.pauseTrigger(triggerKey);// 停止触发器

            sched.unscheduleJob(triggerKey);// 移除触发器

            sched.deleteJob(jobKey);// 删除任务

        } catch (Exception e) {

            throw new RuntimeException(e);

        }
    }

    /**
     * @Description:启动所有定时任务
     */
    public void startJobs(Scheduler sched) {
        try {
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            if (!sf.getScheduler().isShutdown()) {
                sf.getScheduler().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    /**
     *
     * @Title:
     * @Description: 新增或者修改
     *
     * @param jobKey
     * @param triggerKey
     * @param clz
     * @param intervalTime
     * @param jobDataMap
     * @throws SchedulerException
     */
    public void addOrModify(JobKey jobKey, TriggerKey triggerKey, @SuppressWarnings("rawtypes") Class clz,
                            int intervalTime, JobDataMap jobDataMap, Date startTime) throws SchedulerException {

        Scheduler sched = sf.getScheduler();

        if (sched.checkExists(triggerKey)) {
            // modify修改trigger
            modifyJobTime(triggerKey, intervalTime, jobDataMap, startTime);
        } else {
            // add新增
            addJob(jobKey, triggerKey, clz, intervalTime, jobDataMap, startTime);
        }
    }

    /**
     *
     * @Title:
     * @Description: 新增或者修改
     *
     * @param jobKey
     * @param triggerKey
     * @param clz
     * @param cron
     * @param jobDataMap
     * @throws SchedulerException
     */
    public void addOrModify(JobKey jobKey, TriggerKey triggerKey, @SuppressWarnings("rawtypes") Class clz,
                            String cron, JobDataMap jobDataMap, Date startTime) throws SchedulerException {

        Scheduler sched = sf.getScheduler();

        if (sched.checkExists(jobKey)) {
            // 如果存在，则modify一下job和trigger
        }
        if (sched.checkExists(triggerKey)) {
            // modify修改trigger
            modifyJobTime(triggerKey, cron, jobDataMap, startTime);
        } else {
            // add新增
            addJob(jobKey, triggerKey, clz, cron, jobDataMap, startTime);
        }
    }




    /**
     * 停止调度多个触发器相关的job
     * @param triggerKeyList
     * @return
     * @throws SchedulerException
     */
    public boolean unScheduleJobs(List<TriggerKey> triggerKeyList) throws SchedulerException{
        return sf.getScheduler().unscheduleJobs(triggerKeyList);
    }


    /**
     * 停止一个job任务
     * @param jobkey
     * @throws SchedulerException
     */
    public void pauseJob(JobKey jobkey) throws SchedulerException  {
        sf.getScheduler().pauseJob(jobkey);
    }
    /**
     * 停止使用相关的触发器
     * @param triggerkey
     * @throws SchedulerException
     */
    public void pauseTrigger(TriggerKey triggerkey)
            throws SchedulerException   {
        sf.getScheduler().pauseTrigger(triggerkey);
    }

    /**
     * 停止调度Job任务
     * @param triggerkey
     * @return
     * @throws SchedulerException
     */
    public  boolean unScheduleJob(TriggerKey triggerkey)
            throws SchedulerException{
        return sf.getScheduler().unscheduleJob(triggerkey);
    }

    /**
     * 重新恢复触发器相关的job任务
     * @param triggerkey
     * @param trigger
     * @return
     * @throws SchedulerException
     */
    public void rescheduleJob(TriggerKey triggerkey, Trigger trigger) throws SchedulerException{
        sf.getScheduler().rescheduleJob(triggerkey, trigger);
    }

    /**
     * 恢复相关的job任务
     * @param jobkey
     * @throws SchedulerException
     */
    public void resumeJob(JobKey jobkey) throws SchedulerException {
        sf.getScheduler().resumeJob(jobkey);
    }

    /**
     * 恢复相关的触发器
     * @param triggerkey
     * @throws SchedulerException
     */
    public void resumeTrigger(TriggerKey triggerkey)
            throws SchedulerException   {
        sf.getScheduler().resumeTrigger(triggerkey);
    }


    /**
     * 获取触发器
     * @param triggerKey
     * @return
     */
    public SimpleTrigger getTrigger(TriggerKey triggerKey) {
        Scheduler sched = sf.getScheduler();
        SimpleTrigger trigger = null;
        try {
            trigger = (SimpleTrigger) sched.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return trigger;
    }

    /**
     * 恢复调度中所有的job的任务，重置任务
     * @throws SchedulerException
     */
    public void resumeAll() throws SchedulerException {
        sf.getScheduler().resumeAll();
    }

}
