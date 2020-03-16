package com.pzhu.spring.cloud.alibaba.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 博客(BgBlog)实体类
 *
 * @author Guo Huaijian
 * @since 2020-03-16 15:48:34
 */
@Data
public class BgBlog implements Serializable {
    private static final long serialVersionUID = 569360996276290187L;
    /**
    * 博客id

    */
    private Integer blogId;
    /**
    * 归类id
    */
    private Integer categoryId;
    /**
    * 博文标题
    */
    private String title;
    /**
    * 博文摘要
    */
    private String summary;
    /**
    * 博文状态，1表示已经发表，2表示在草稿箱，3表示在垃圾箱
    */
    private String status;
    /**
    * 权重
    */
    private Integer weight;
    /**
    * 是否推荐，1表示推荐，0表示不推荐
    */
    private String support;
    /**
    * 点击次数
    */
    private Integer click;
    /**
    * 标图展示地址
    */
    private String headerImg;
    /**
    * 博文类型，1表示markdown，2表示富文本
    */
    private String type;
    /**
    * 博文正文内容
    */
    private String content;
    /**
    * 创建者
    */
    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;



}
