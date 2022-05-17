package com.non7.service;

import com.non7.entity.PageResult;
import com.non7.entity.QueryPageBean;
import com.non7.pojo.CheckItem;

import java.util.List;

/**
 *服务接口
 */

public interface CheckItemService {


    public void add(CheckItem checkItem);

    public PageResult pageQuery(QueryPageBean queryPageBean);

    public  void deleteById(Integer id);

    public void edit(CheckItem checkItem);

    public CheckItem findById(Integer id);

    public List<CheckItem> findAll();
}
