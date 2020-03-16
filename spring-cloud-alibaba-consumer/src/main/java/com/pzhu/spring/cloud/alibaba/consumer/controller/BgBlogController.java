package com.pzhu.spring.cloud.alibaba.consumer.controller;

import com.pzhu.spring.cloud.alibaba.common.domain.BgBlog;

import com.pzhu.spring.cloud.alibaba.consumer.service.BgBlogServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Guo Huaijian
 * @Date 2020/3/16 19:03
 */
@RestController
public class BgBlogController {

    @Autowired
    private BgBlogServiceFeign serviceFeign;

    /**
     * 查询列表
     * @return
     */
    @GetMapping("list")
    public List<BgBlog> list(){
        return serviceFeign.list();
    }
}
