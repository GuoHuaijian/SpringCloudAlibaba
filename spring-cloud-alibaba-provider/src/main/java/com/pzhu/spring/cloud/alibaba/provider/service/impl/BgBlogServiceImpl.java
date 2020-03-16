package com.pzhu.spring.cloud.alibaba.provider.service.impl;

import com.pzhu.spring.cloud.alibaba.common.domain.BgBlog;
import com.pzhu.spring.cloud.alibaba.provider.dao.BgBlogDao;
import com.pzhu.spring.cloud.alibaba.provider.service.BgBlogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 博客(BgBlog)表服务实现类
 *
 * @author Guo Huaijian
 * @since 2020-03-16 15:48:36
 */
@Service("bgBlogService")
public class BgBlogServiceImpl implements BgBlogService {
    @Resource
    private BgBlogDao bgBlogDao;

    /**
     * 通过ID查询单条数据
     *
     * @param blogId 主键
     * @return 实例对象
     */
    @Override
    public BgBlog queryById(Integer blogId) {
        return this.bgBlogDao.queryById(blogId);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<BgBlog> queryAllByLimit(int offset, int limit) {
        return this.bgBlogDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param bgBlog 实例对象
     * @return 实例对象
     */
    @Override
    public BgBlog insert(BgBlog bgBlog) {
        this.bgBlogDao.insert(bgBlog);
        return bgBlog;
    }

    /**
     * 修改数据
     *
     * @param bgBlog 实例对象
     * @return 实例对象
     */
    @Override
    public BgBlog update(BgBlog bgBlog) {
        this.bgBlogDao.update(bgBlog);
        return this.queryById(bgBlog.getBlogId());
    }

    /**
     * 通过主键删除数据
     *
     * @param blogId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer blogId) {
        return this.bgBlogDao.deleteById(blogId) > 0;
    }
}
