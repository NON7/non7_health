package com.non7.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.non7.constant.MessageConstant;
import com.non7.entity.Result;
import com.non7.service.MemberService;

import com.non7.service.SetmealService;
import com.non7.utils.DateUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private SetmealService setmealService;

    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
//        使用模拟数据测试对象格式是否能够转为echarts所需的数据格式
        Map<String,Object> map=new HashMap<>();
        List<String> months=new ArrayList<>();
//        计算过去1年的12个月月份
        Calendar calendar = Calendar.getInstance();//获得日历对象，模拟时间就是当前时间
//        计算过去一年的12个月
//        calendar.add(Calendar.MONDAY,-12);//获得当前时间往前推12个月的时间

        for(int i=0;i<12;i++){
            calendar.add(Calendar.MONDAY,-1);
            Date date = calendar.getTime();
            months.add(new SimpleDateFormat("yyyy.MM").format(date));
        }
        map.put("months",months);
//       查询会员注册数量
        List<Integer> memberCount = memberService.findMemberCountByMonths(months);
        map.put("memberCount",memberCount);


        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }


    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        Map<String,Object> data=new HashMap<>();//存储传输到html页面的数据
        List<String> setmealNames=new ArrayList<>();

        List<Map<String,Object>> setmealCount= null;
        try {
            setmealCount = setmealService.findSetmealCount();
            data.put("setmealCount",setmealCount);

            for (Map<String, Object> map : setmealCount) {
                String name = (String) map.get("name");//套餐名称
                setmealNames.add(name);
            }
            data.put("setmealNames",setmealNames);

            //SetmealCount数组，数组+map
            return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,data);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }



    }


}
