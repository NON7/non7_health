package com.non7.service;

import com.non7.entity.PageResult;
import com.non7.entity.QueryPageBean;
import com.non7.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    public void add(Setmeal setmeal, Integer[] checkgroupIds);

    public PageResult findPage(QueryPageBean queryPageBean);

    public List<Setmeal> findAll();

    public Setmeal findById(Integer id);

    public List<Integer> findCheckSetmealIdsByCheckgroupId(Integer id);

    public void deleteById(Integer id);

    public void edit(Setmeal setmeal,Integer[] checkgroupIds);

    public List<Map<String,Object>> findSetmealCount();
}
