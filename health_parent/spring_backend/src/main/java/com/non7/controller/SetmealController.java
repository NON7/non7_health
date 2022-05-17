package com.non7.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.non7.constant.MessageConstant;
import com.non7.constant.RedisConstant;
import com.non7.entity.PageResult;
import com.non7.entity.QueryPageBean;
import com.non7.entity.Result;
import com.non7.pojo.Setmeal;
import com.non7.service.SetmealService;
import com.non7.utils.QiniuUtils;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 体检套餐管理
 */

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    //使用JedisPool操作Redis服务
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private SetmealService setmealService;
//    文件上传
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
//        获取原始文件名的图片类型
        String originalFilename = imgFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String extention = originalFilename.substring(i - 1);//.jpg

//        随机产生文件名
        String fileName= UUID.randomUUID().toString()+extention;//36位字符串

        try {
//            将文件上传到七牛云服务器
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){
        try {
//            System.out.println("SetmealController>>>>>>>>>>>>>>>>>>");
            setmealService.add(setmeal,checkgroupIds);
            return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }

    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        return setmealService.findPage(queryPageBean);
    }

    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);

        }
    }

    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<Setmeal> list=setmealService.findAll();
            return new Result(true,MessageConstant.QUERY_SETMEALLIST_SUCCESS,list);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEALLIST_FAIL);

        }
    }
    @RequestMapping("/findCheckSetmealIdsByCheckgroupId")
    public Result findCheckSetmealIdsByCheckgroupId(Integer id){
        try {
            List<Integer> checkgroupIds=setmealService.findCheckSetmealIdsByCheckgroupId(id);
            return new Result(true,MessageConstant.QUERY_SETMEALLIST_SUCCESS,checkgroupIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEALLIST_FAIL);
        }
    }


    @RequestMapping("/deleteById")
    public Result deleteById(Integer id){
        try {
            setmealService.deleteById(id);
            return new Result(true,MessageConstant.DELETE_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_SETMEAL_FAIL);
        }
    }



    @RequestMapping("/edit")
    public Result edit(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        try {
            setmealService.edit(setmeal,checkgroupIds);
            return new Result(true,MessageConstant.EDIT_SETMEAL_SUCCESS);//修改成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_SETMEAL_FAIL);
        }
    }


}
