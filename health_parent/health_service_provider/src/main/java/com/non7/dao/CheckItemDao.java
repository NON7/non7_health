package com.non7.dao;

import com.github.pagehelper.Page;
import com.non7.pojo.CheckItem;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CheckItemDao {
    public void add(CheckItem checkItem) ;

    public Page<CheckItem> selectByCondition(String queryString);

    public long fnidCountByCheckItemId(Integer id);

    public void deleteById(Integer id);

    public void edit(CheckItem checkItem);

    public CheckItem findById(Integer id);

    public List<CheckItem> findAll();
}
