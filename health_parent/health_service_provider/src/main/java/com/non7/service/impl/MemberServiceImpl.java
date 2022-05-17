package com.non7.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.non7.dao.MemberDao;
import com.non7.pojo.Member;
import com.non7.service.MemberService;
import com.non7.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass=MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;


    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

//    保存会员信息
    @Override
    public void add(Member member) {
        String password = member.getPassword();

        if(password!=null){
//            使用MD5将明文密码加密
            password = MD5Utils.md5(password);
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonths(List<String> months) {//2022.05
        List<Integer> memberCount=new ArrayList<>();
        for (String month : months) {
            String date=month+".31";
            Integer count = memberDao.findMemberCountBeforeDate(date);
            memberCount.add(count);
        }
        return memberCount;
    }
}
