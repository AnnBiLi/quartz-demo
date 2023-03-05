package com.wyl.quartz.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskJob {

    private String type;

    private String content;

    private String username;

    //@ApiModelProperty(value = "任务名称")
    private String jobName;

    //@ApiModelProperty(value = "任务分组")
    private String jobGroup;

    //@ApiModelProperty(value = "备注")
    private String description;

    //@ApiModelProperty(value = "cron表达式")
    private String cronExpression;

    //@ApiModelProperty(value = "已触发次数")
    private Long timesTriggered;

    ////@ApiModelProperty(value = "上一次执行时间")
    private Long prevFireTime;

    //@ApiModelProperty(value = "下一次执行时间")
    private Long nextFireTime;

    //@ApiModelProperty(value = "开始时间")
    private Long startTime;

    //@ApiModelProperty(value = "结束时间")
    private Long endTime;

    //@ApiModelProperty(value = "任务状态")
    private String triggerState;

    //@ApiModelProperty(value = "采集信息：采集间隔")
    private Integer collectorInterval;


}
