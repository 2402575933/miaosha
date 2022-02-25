package com.tommy.dao;

import com.tommy.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        Integer insertCount = successKilledDao.insertSuccessKilled(1001L, 13082623496L);
        System.out.println(insertCount);
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1001L;
        long phone = 13082623496L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
        System.out.println("===========================");
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());

//        System.out.println(successKilled.getSeckill());
    }

    @Test
    public void testPrint() {
        Date date = new Date();
        System.out.println(date);
    }
}