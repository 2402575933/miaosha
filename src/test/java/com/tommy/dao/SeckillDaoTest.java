package com.tommy.dao;

import com.tommy.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;
import java.util.Date;

import static org.junit.Assert.*;

/*
配置
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void testQueryById() {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void testReduceNumber() {
        Date killTime = new Date();
        Integer updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println(updateCount);
    }

    @Test
    public void testQueryAll() {
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        System.out.println(seckills);
    }
}