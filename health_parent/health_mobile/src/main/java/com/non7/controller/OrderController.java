package com.non7.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.non7.constant.MessageConstant;
import com.non7.constant.RedisMessageConstant;
import com.non7.entity.Result;
import com.non7.pojo.Order;
import com.non7.service.OrderService;
import com.non7.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;
import sun.rmi.runtime.Log;

import java.io.Console;
import java.util.Map;

/**
 * 体检预约处理
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private OrderService orderService;

    //    在线体检预约
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){
//        从Redis中获取保存的验证码
        String telephone = (String)map.get("telephone");
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode = (String) map.get("validateCode");
//        将用户输入的验证码和Redis中保存的验证码进行比对
//        if(validateCodeInRedis!=null&&validateCode!=null&&validateCodeInRedis!=validateCode){
        if(true){
            //        如果比对成功，调用服务完成预约
            map.put("orderType", Order.ORDERTYPE_WEIXIN);//设置预约类型，分为微信预约，电话预约
            Result result = null;
            try {
                result = orderService.order(map);//通过Dubbo远程调用服务实现在线预约业务处理，有可能出现网络异常出现问题
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
            if(result.isFlag()){
//                预约成功，可以为用户发送短信
                System.out.println("预约成功，可以为用户发送短信");
//                try {
//                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,map.get("orderDate").toString());
//                } catch (ClientException e) {
//                    e.printStackTrace();
//                }
            }

            return result;

        }else{
            //        如果比对不成功，返回结果给页面
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

//    根据预约ID查询预约相关信息
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Map map=orderService.findById(id);//为了数据灵活，不使用实际的Order类，使用灵活的Map类
            System.out.println(map.get("mumber"));
            System.out.println(map.get("setmeal"));
            System.out.println(map.get("orderDate"));
            System.out.println(map.get("orderType"));
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);

        }
    }
}
