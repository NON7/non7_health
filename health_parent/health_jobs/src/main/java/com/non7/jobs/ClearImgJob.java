package com.non7.jobs;

import com.non7.constant.RedisConstant;
import com.non7.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 自定义job，实现定时清理垃圾图片
 */
public class ClearImgJob {
    @Autowired
    private JedisPool jedisPool;

    public void clearImg(){
//        根据Rediszhongb保存的两个set集合进行差值计算，获得垃圾图片名称结合
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if(set!=null){
            for(String picName:set){
//                删除七牛云服务器上的图片
                QiniuUtils.deleteFileFromQiniu(picName);
//                从redis中删除图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,picName);
                System.out.println("自定义任务执行，清理垃圾图片："+picName);
            }
        }

    }
}
