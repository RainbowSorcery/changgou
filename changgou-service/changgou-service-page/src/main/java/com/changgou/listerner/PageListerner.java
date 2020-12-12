package com.changgou.listerner;

import com.changgou.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RabbitListener(queues = {"page_create_queue"})
@Component
public class PageListerner {
    @Autowired
    private PageService pageService;

    @RabbitHandler
    public void msgHandle(String spuId) {
        pageService.createPageHtml(spuId);
    }
}
