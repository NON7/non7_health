package com.non7.service;


import com.non7.entity.PageResult;
import com.non7.entity.QueryPageBean;
import com.non7.entity.Result;
import com.non7.pojo.CheckGroup;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CheckGroupService {
    public void add(CheckGroup checkGroup, Integer[] checkitemIds);

    public PageResult pageQuery(QueryPageBean queryPageBean);

    public CheckGroup findById(Integer id);

    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    public void edit(CheckGroup checkGroup,Integer[] checkitemIds);

    public List<CheckGroup> findAll();
}
