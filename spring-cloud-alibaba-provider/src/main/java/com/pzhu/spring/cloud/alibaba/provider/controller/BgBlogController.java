package com.pzhu.spring.cloud.alibaba.provider.controller;

import com.pzhu.spring.cloud.alibaba.common.domain.BgBlog;
import com.pzhu.spring.cloud.alibaba.provider.service.BgBlogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 博客(BgBlog)表控制层
 *
 * @author Guo Huaijian
 * @since 2020-03-16 15:48:36
 */
@RestController
public class BgBlogController {
    /**
     * 服务对象
     */
    @Resource
    private BgBlogService bgBlogService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public BgBlog selectOne(Integer id) {
        return this.bgBlogService.queryById(id);
    }

    /**
     * 查询文章列表
     * @return
     */
    @GetMapping("list")
    public List<BgBlog> list(){
        System.out.println(1);
        List<BgBlog> blogs = bgBlogService.queryAllByLimit(1, 10);
        return blogs;
    }
}
