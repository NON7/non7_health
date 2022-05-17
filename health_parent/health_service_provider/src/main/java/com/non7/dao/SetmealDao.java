package com.non7.dao;

import com.github.pagehelper.Page;
import com.non7.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    public void add(Setmeal setmeal);

    public void setSetmealAndCheckGroup(Map map);

    public Page<Setmeal> findByCondition(String queryString);

    public List<Setmeal> findAll();

    public Setmeal findById(Integer id);

    public List<Integer> findCheckSetmealIdsByCheckgroupId(Integer id);

    public Long findCountByCheckGroupId(Integer id);

    public void deleteById(Integer id);

    public void edit(Setmeal setmeal);

    public void deleteAssociation(Integer id);

    public List<Map<String,Object>> findSetmealCount();

}
