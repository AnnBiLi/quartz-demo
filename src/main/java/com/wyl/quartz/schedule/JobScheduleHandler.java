package com.wyl.quartz.schedule;


import com.wyl.quartz.model.Task;
import com.wyl.quartz.model.TaskJob;
import com.wyl.quartz.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 任务分发
 */
@Component
@Slf4j
public class JobScheduleHandler {

    @Autowired
    private QuartzManager quartzManager;

    @Autowired
    private TaskService taskService;

    /**
     * 初始化采集任务
     */
    public void initCollectJob() {
        try {
            List<Task> taskList = taskService.list();
            if (!taskList.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MINUTE, 0);
                for (Task task : taskList) {
                    if (!ObjectUtils.isArray(task)){
                        TaskJob taskJob = buildJob(task);
                        saveJob(taskJob, new Date(cal.getTimeInMillis()));
                    }
                }
            }
        } catch (Exception e) {
            //这个任务不允许出现异常终止
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }

    /**
     * 更新采集任务
     */
    public void updateCollectorJob(Task task) {
        try {
            TaskJob scheduleJob = buildJob(task);
            saveJob(scheduleJob, new Date());
        } catch (SchedulerException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 整理任务所需数据
     *
     * @return
     */
    private TaskJob buildJob(Task task) {
        TaskJob taskJob = new TaskJob();
        taskJob.setJobName(task.getId().toString());
        taskJob.setCollectorInterval(task.getCollectorInterval());
        taskJob.setContent(task.getContent());
        taskJob.setType(task.getType());

        return taskJob;
    }

    /**
     * 整合采集任务所需数据
     */
    private JobDataMap buildJobDataMap(TaskJob taskJob) {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("content", taskJob.getContent());
        jobDataMap.put("jobType", taskJob.getType());
        return jobDataMap;
    }

    /**
     * 添加更新任务
     *
     * @param taskJob
     * @throws SchedulerException
     */
    public void saveJob(TaskJob taskJob,
                        Date startTime
    ) throws SchedulerException {
        if (taskJob == null) {
            return;
        }

        JobKey _jobKey = JobKey.jobKey(taskJob.getJobName(), taskJob.getType());
        TriggerKey _triggerKey = TriggerKey.triggerKey(taskJob.getJobName(), taskJob.getType());
        quartzManager.addOrModify(_jobKey, _triggerKey, MyJob.class, taskJob.getCollectorInterval(),
                buildJobDataMap(taskJob), startTime);


    }


    /**
     * 删除当前任务
     *
     * @throws SchedulerException
     */
    public void deleteAllJob() {
        List<Task> monitorModuleList = taskService.list();
        monitorModuleList.forEach(it -> {
            JobKey jobKey = JobKey.jobKey(String.valueOf(it.getId()), it.getType().toString());
            TriggerKey triggerKey = TriggerKey.triggerKey(String.valueOf(it.getId()), it.getType().toString());
            quartzManager.removeJob(jobKey, triggerKey);
        });

    }

    /**
     * 删除任务
     *
     * @param
     * @param
     */
    public void deleteJob(Integer id, String taskType) {
        JobKey jobKey = JobKey.jobKey(id.toString(), taskType);
        TriggerKey triggerKey = TriggerKey.triggerKey(id.toString(), taskType);
        quartzManager.removeJob(jobKey, triggerKey);
    }

    /**
     * 停止任务
     *
     * @throws SchedulerException
     */
    public void unScheduleJob(Integer id, String taskType) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(id.toString(), taskType);
        quartzManager.unScheduleJob(triggerKey);
    }

    /**
     * 恢复任务
     *
     * @throws SchedulerException
     */
    public void rescheduleJob(Integer id, String taskType) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(id.toString(), taskType);
        SimpleTrigger trigger = quartzManager.getTrigger(triggerKey);
        if (trigger == null) {
            return;
        }
        quartzManager.rescheduleJob(triggerKey, trigger);
    }


    /**
     * 停止一个job任务
     *
     * @throws SchedulerException
     */
    public void pauseJob(Integer id, String taskType) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(id.toString(), taskType);
        quartzManager.pauseJob(jobKey);

    }

        /**
         * 停止一个触发器
         *
         * @throws SchedulerException
         */
        public void pauseTrigger(Integer id, String taskType) throws SchedulerException {
            TriggerKey triggerKey = TriggerKey.triggerKey(id.toString(), taskType);
            quartzManager.pauseTrigger(triggerKey);
        }

        /**
         * 恢复一个job任务
         *
         * @throws SchedulerException
         */
        public void resumeJob(Integer id, String taskType) throws SchedulerException {
            JobKey jobKey = JobKey.jobKey(id.toString(), taskType);
            quartzManager.resumeJob(jobKey);
        }

        /**
         * 恢复一个触发器
         *
         * @throws SchedulerException
         */
        public void resumeTrigger(Integer id, String taskType) throws SchedulerException {
            TriggerKey triggerKey = TriggerKey.triggerKey(id.toString(), taskType);
            quartzManager.resumeTrigger(triggerKey);
        }

        /**
         * 重置所有任务,先删除所有任务，再进行重新添加
         */
        public void resetAll() {
            try {
                List<Task> taskList = taskService.list();
                if (!taskList.isEmpty()) {
                    for (Task task : taskList) {
                        if (task.getId() != null) {
                            TaskJob taskJob = buildJob(task);
                            deleteJob(Integer.parseInt(taskJob.getJobName()), taskJob.getType());
                    }
                }
                Thread.sleep(1000);
                resetCollectJob();
            }
        } catch (Exception e) {
            //这个任务不允许出现异常终止
        }
    }

    public void resetCollectJob() {
        try {
            List<Task> taskList = taskService.list();
            if (!taskList.isEmpty()) {
                for (Task task : taskList) {
                    TaskJob taskJob = buildJob(task);
                    saveJob(taskJob, new Date());
                }
            }
        } catch (Exception e) {
            //这个任务不允许出现异常终止
        }
    }


    public void shutdown() {
        quartzManager.shutdownJobs();
    }

}
