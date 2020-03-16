package com.pzhu.spring.cloud.alibaba.provider.dao;

import com.pzhu.spring.cloud.alibaba.common.domain.BgBlog;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 博客(BgBlog)表数据库访问层
 *
 * @author Guo Huaijian
 * @since 2020-03-16 15:48:35
 */
public interface BgBlogDao {

    /**
     * 通过ID查询单条数据
     *
     * @param blogId 主键
     * @return 实例对象
     */
    BgBlog queryById(Integer blogId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<BgBlog> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param bgBlog 实例对象
     * @return 对象列表
     */
    List<BgBlog> queryAll(BgBlog bgBlog);

    /**
     * 新增数据
     *
     * @param bgBlog 实例对象
     * @return 影响行数
     */
    int insert(BgBlog bgBlog);

    /**
     * 修改数据
     *
     * @param bgBlog 实例对象
     * @return 影响行数
     */
    int update(BgBlog bgBlog);

    /**
     * 通过主键删除数据
     *
     * @param blogId 主键
     * @return 影响行数
     */
    int deleteById(Integer blogId);

}
