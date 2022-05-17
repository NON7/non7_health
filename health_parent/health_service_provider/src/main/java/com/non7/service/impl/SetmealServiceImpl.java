package com.non7.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.non7.constant.RedisConstant;
import com.non7.dao.SetmealDao;
import com.non7.entity.PageResult;
import com.non7.entity.QueryPageBean;
import com.non7.pojo.Setmeal;
import com.non7.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${out_put_path}")
    private String outPutPath;//从属性文件中读取要生成的html对应的目录
//    新增套餐信息，同时需要关联检查组
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        this.setSetAndCheckgroup(setmealId,checkgroupIds);
//        将图片名称保存到Redis集合中
        String fileName=setmeal.getImg();
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,fileName);

//        当重新添加页面后，需要重新生成静态页面（套餐列表页面，套餐详情页面）
        generateMobileStaticHtml();



    }
//    生成generageHtml（）方法所需的静态页面
    public void generateMobileStaticHtml(){
//        在生成静态页面之前需要查询数据
        List<Setmeal> list = setmealDao.findAll();
//        需要生成套餐列表静态页面
        generateMobileSetmealListHtml(list);
//        需要生成套餐详情静态页面
        generateMobileSetmealDetailHtml(list);
    }

//    生成套餐列表静态页面的方法
    public void generateMobileSetmealListHtml(List<Setmeal> setmeals){
//        为模板提供数据，用于生成静态页面
        Map map=new HashMap();
        map.put("setmealList",setmeals);
        generateHtml("mobile_setmeal.ftl","m_setmeal.html",map);

    }
    //    生成套餐详情页面的方法，可能有多个
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmeals){
        for (Setmeal setmeal : setmeals) {
            Map map=new HashMap();

            map.put("setmeal",setmealDao.findById(setmeal.getId()));
            generateHtml("mobile_setmeal_detail.ftl","setmeal_detail_"+setmeal.getId()+".html",map);
        }

    }



//    通用的方法，用于生成静态页面
    public void generateHtml(String teplateName,String htmlPageName,Map map){
        Configuration configuration = freeMarkerConfigurer.getConfiguration();//获得配置对象
        Writer out=null;
        try {
            Template template = configuration.getTemplate(teplateName);
//            构造输出流
            out=new FileWriter(new File(outPutPath+"/"+htmlPageName));
//            输出文件
            template.process(map,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> page=setmealDao.findByCondition(queryString);


        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Setmeal> findAll() {

        return setmealDao.findAll();
    }

    @Override
    public Setmeal findById(Integer id) {


        return setmealDao.findById(id);
    }

    @Override
    public List<Integer> findCheckSetmealIdsByCheckgroupId(Integer id) {
        return setmealDao.findCheckSetmealIdsByCheckgroupId(id);
    }

    @Override
    public void deleteById(Integer id) {
        long count=setmealDao.findCountByCheckGroupId(id);
        if(count>0){
            throw new RuntimeException("当前检查组被引用，不能删除");
        }
        setmealDao.deleteById(id);
    }

    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
//        1、修改检查组基本信息，操作检查组t_setmeal表
        setmealDao.edit(setmeal);
//        2、清理当前检查组关联的检查项，操作中间关系表t_setmeal_checkgroup表
        setmealDao.deleteAssociation(setmeal.getId());
//        3、重新建立当前检查组和检查项的关联关系
        Integer setmealId=setmeal.getId();
        this.setSetmealAndCheckGroup(setmealId,checkgroupIds);

    }

    /**
     * report_setmeal.html饼状图
     * @return
     */
    @Override
    public List<Map<String, Object>> findSetmealCount() {

        return setmealDao.findSetmealCount();
    }

    public void setSetmealAndCheckGroup(Integer setmealId,Integer[] checkgroupIds){
        if(setmealId!=null&&checkgroupIds.length>0){
            for(Integer checkgroupId:checkgroupIds){
                Map<String,Integer> map=new HashMap<>();
                map.put("setmealId",setmealId);
                map.put("checkgroupId",checkgroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }

    //    设置套餐和检查组多对多关系，操作t_setmeal_checkgroup
    public void setSetAndCheckgroup(Integer setmealId,Integer[] checkgroupIds){
        if(checkgroupIds!=null&&checkgroupIds.length>0){
            for(Integer checkgroupId:checkgroupIds){
                Map<String,Integer> map=new HashMap<>();
                map.put("setmeal_id",setmealId);
                map.put("checkgroup_id",checkgroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }
}
