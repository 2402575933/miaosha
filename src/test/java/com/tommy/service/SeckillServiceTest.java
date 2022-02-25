package com.tommy.service;

import com.tommy.dto.Exposer;
import com.tommy.dto.SeckillExecution;
import com.tommy.entity.Seckill;
import com.tommy.exception.RepeatKillException;
import com.tommy.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckills = seckillService.getSeckillList();
        logger.info("lists={}", seckills);
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("Seckill={}", seckill);
    }

    @Test
    public void testSeckillLogic() {
        long seckillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);

        if (exposer.isExposed()) {

        }
        logger.info("exposer={}", exposer);
//      md5: 3e785544d306410d15c15d02ee413167
//        Exposer{
//        exposed=true,
//        md5='3e785544d306410d15c15d02ee413167',
//        seckillId=1000,
//        now=0, start=0, end=0}
    }

    @Test
    public void executeSeckill() {
//        com.tommy.exception.RepeatKillException: seckill repeated
        long id = 1000;
        long phone = 17564789676L;
        String md5 = "3e785544d306410d15c15d02ee413167";
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
            logger.info("seckillExecution={}", seckillExecution);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        }
    }
}