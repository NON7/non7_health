package com.non7.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.non7.dao.CheckItemDao;
import com.non7.entity.PageResult;
import com.non7.entity.QueryPageBean;
import com.non7.pojo.CheckItem;
import com.non7.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 检查项服务
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {
    //    注入dao对象
    @Autowired
    private CheckItemDao checkItemDao;
    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
//        完成分页查询，基于mybatis提供的分页助手插件完成

            PageHelper.startPage(currentPage, pageSize);


        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        long total = page.getTotal();
        List<CheckItem> rows = page.getResult();

        return new PageResult(total,rows);
    }

    //根据id删除检查项
    @Override
    public void deleteById(Integer id) throws RuntimeException {
//        判断当前检查项是否已经关联到检查组了
//        1、查询检查项和检查组的中间关系表是否有数据，如果没有则可以进行删除，如果有，则不能进行删除
        long count = checkItemDao.fnidCountByCheckItemId(id);
        if(count>0){
//            当前检查项已经被关联到检查组了，不允许删除
            throw new RuntimeException("当前检查项被引用，不能删除");
        }
            checkItemDao.deleteById(id);


    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
        CheckItem checkitem = checkItemDao.findById(id);
        return checkitem;
    }

    @Override
    public List<CheckItem> findAll() {
        List<CheckItem> checkItems=checkItemDao.findAll();
        return checkItems;
    }
}
