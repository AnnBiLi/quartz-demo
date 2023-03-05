package com.wyl.quartz.controller;


import com.wyl.quartz.model.Detail;
import com.wyl.quartz.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/detail")
@RestController
public class DetailController {


    @Autowired
    private DetailService detailService;

    @RequestMapping("/add")
    public void add(){
//        Detail detail = new Detail();
//        for (int i = 0; i < 10; i++) {
//            detail.setContent("测试task"+1);
//        }
//        detail.setType("job-1");
//        detail.setInterval(20);
        detailService.add();
    }

}
