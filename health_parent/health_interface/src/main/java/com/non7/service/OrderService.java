package com.non7.service;

import com.non7.entity.Result;

import java.util.Map;

public interface OrderService {

    public Result order(Map map) throws Exception;
    public Map findById(Integer id) throws Exception;
}
