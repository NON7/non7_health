package com.non7.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.non7.constant.MessageConstant;
import com.non7.entity.PageResult;
import com.non7.entity.QueryPageBean;
import com.non7.entity.Result;
import com.non7.pojo.CheckItem;
import com.non7.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 检查项
 */
@RestController
@RequestMapping("/checkitem")
public class CheckItemController {
//    定义服务接口
    /**Autowired:本地注入
     * Reference:远程注入
     *  1、从zookeeper注册中心获取userService的访问url
     *  2、进行远程调用RPC
     *  3、将结果封装为一个代理对象，给变量赋值
     */
    @Reference//查找服务
    private CheckItemService checkItemService;
//    新增检查项
    @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkItem){
//        System.out.println("数据已到达CheckItemController》》》》》》》》》》》》》》"+checkItemService);
        try {
           checkItemService.add(checkItem);
        }catch (Exception e){
            e.printStackTrace();
//            服务调用失败
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
        }

        return new Result(true,MessageConstant.ADD_CHECKITEM_SUCCESS);
    }

//    检查项分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        PageResult pageResult = checkItemService.pageQuery(queryPageBean);
        return pageResult;
    }

//    删除检查项
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")
    @RequestMapping("/delete")
    public Result delete(Integer id){
        try{
            checkItemService.deleteById(id);
            return new Result(true,MessageConstant.DELETE_CHECKITEM_SUCCESS);
        }catch (Exception e){

            e.printStackTrace();
//            服务调用失败
            return new Result(false,MessageConstant.DELETE_CHECKITEM_FAIL);
        }
    }

    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            CheckItem checkitem=checkItemService.findById(id);
            return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkitem);
        }catch (Exception e){
            return new Result(false,MessageConstant.QUERY_CHECKITEM_FAIL);
        }

    }

    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem){
        try{
            checkItemService.edit(checkItem);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
        return new Result(true,MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<CheckItem> checkItems=checkItemService.findAll();
            return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItems);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKITEM_FAIL);
        }

    }

}
