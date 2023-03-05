package com.wyl.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyl.quartz.mapper.TaskMapper;
import com.wyl.quartz.model.Task;
import com.wyl.quartz.schedule.JobScheduleHandler;
import com.wyl.quartz.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper,Task> implements TaskService {

}
