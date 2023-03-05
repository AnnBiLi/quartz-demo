package com.wyl.quartz.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;


/**
 * 定时任务执行
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MyJob extends QuartzJobBean {

    /**
     * 任务触发
     * @param context
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String content = dataMap.getString("content");
        System.out.println("任务执行："+content);
    }
}
