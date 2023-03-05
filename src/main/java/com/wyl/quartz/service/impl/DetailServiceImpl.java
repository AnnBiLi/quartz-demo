package com.wyl.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyl.quartz.mapper.DetailMapper;

import com.wyl.quartz.mapper.TaskMapper;
import com.wyl.quartz.model.Detail;

import com.wyl.quartz.model.Task;
import com.wyl.quartz.schedule.JobScheduleHandler;
import com.wyl.quartz.service.DetailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DetailServiceImpl  extends ServiceImpl<DetailMapper, Detail> implements DetailService {

    @Autowired
    private DetailMapper detailMapper;
//
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private JobScheduleHandler jobScheduleHandler;

    @Override
    public void add() {
        Detail detail = new Detail();
        detail.setTaskInterval(10);
        detail.setUsername("wyl");
        detail.setType("job-1");
        detail.setTest("test-job");
        for (int i = 0; i < 10; i++) {
            detail.setContent("job-"+i);
            detailMapper.insert(detail);
            Task task = new Task();
            task.setType(detail.getType());
            task.setCollectorInterval(detail.getTaskInterval());
            task.setContent(detail.getContent());
            taskMapper.insert(task);
            jobScheduleHandler.updateCollectorJob(task);
        }
    }
}
