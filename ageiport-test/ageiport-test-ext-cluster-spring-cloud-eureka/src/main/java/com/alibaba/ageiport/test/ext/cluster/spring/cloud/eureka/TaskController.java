package com.alibaba.ageiport.test.ext.cluster.spring.cloud.eureka;

import com.alibaba.ageiport.processor.core.AgeiPort;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteParam;
import com.alibaba.ageiport.processor.core.spi.service.TaskExecuteResult;
import com.alibaba.ageiport.processor.core.spi.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
    @Autowired
    private AgeiPort ageiPort;

    @PostMapping("/task")
    public TaskExecuteResult run(@RequestBody TaskExecuteParam request) {
        TaskService taskService = ageiPort.getTaskService();
        return taskService.executeTask(request);
    }


    @GetMapping("/ping")
    public String ping() {
        return System.currentTimeMillis() + "";
    }

}
