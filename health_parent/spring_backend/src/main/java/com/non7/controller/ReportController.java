package com.non7.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.non7.constant.MessageConstant;
import com.non7.entity.Result;
import com.non7.service.MemberService;

import com.non7.service.ReportService;
import com.non7.service.SetmealService;
import com.non7.utils.DateUtils;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private SetmealService setmealService;
    @Reference
    private ReportService reportService;

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

    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        try {
            Map<String,Object> data=reportService.getBusinessReportData();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,data);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);

        }
    }
//    运营数据导出
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        try {
            Map<String,Object> result=reportService.getBusinessReportData();
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");
            String filePath=request.getSession().getServletContext().getRealPath("template")+ File.separator+"report_template.xlsx";
            XSSFWorkbook excel=new XSSFWorkbook(new FileInputStream(new File(filePath)));
//            读取第一个工作表
            XSSFSheet sheet = excel.getSheetAt(0);
            XSSFRow row = sheet.getRow(2);
            XSSFCell cell = row.getCell(5);
            cell.setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数


            int rowNum = 12;
            for(Map map : hotSetmeal){//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

//            使用输出流进行表格下载,基于浏览器作为客户端下载
            OutputStream out=response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");//代表的是Excel文件类型
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");//指定以附件形式下载

            excel.write(out);
            out.flush();
            out.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
