package com.wyl.quartz;

import com.wyl.quartz.schedule.JobScheduleHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(value = {"com.wyl.quartz.**"})
@MapperScan({"com.wyl.quartz.mapper.**"})
public class QuartzApplication implements CommandLineRunner {

    @Autowired
    private JobScheduleHandler jobScheduleHandler;

    public static void main(String[] args) {
        SpringApplication.run(QuartzApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //初始化任务执行
        jobScheduleHandler.initCollectJob();
    }
}
