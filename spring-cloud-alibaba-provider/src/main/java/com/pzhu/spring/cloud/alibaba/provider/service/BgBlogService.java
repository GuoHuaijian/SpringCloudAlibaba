package com.pzhu.spring.cloud.alibaba.provider.service;


import com.pzhu.spring.cloud.alibaba.common.domain.BgBlog;
import java.util.List;

/**
 * 博客(BgBlog)表服务接口
 *
 * @author Guo Huaijian
 * @since 2020-03-16 15:48:35
 */
public interface BgBlogService {

    /**
     * 通过ID查询单条数据
     *
     * @param blogId 主键
     * @return 实例对象
     */
    BgBlog queryById(Integer blogId);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<BgBlog> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param bgBlog 实例对象
     * @return 实例对象
     */
    BgBlog insert(BgBlog bgBlog);

    /**
     * 修改数据
     *
     * @param bgBlog 实例对象
     * @return 实例对象
     */
    BgBlog update(BgBlog bgBlog);

    /**
     * 通过主键删除数据
     *
     * @param blogId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer blogId);

}
