package com.tommy.service;

import com.tommy.dto.Exposer;
import com.tommy.dto.SeckillExecution;
import com.tommy.entity.Seckill;
import com.tommy.exception.RepeatKillException;
import com.tommy.exception.SeckillCloseException;
import com.tommy.exception.SeckillException;

import java.util.List;

/**
 * 接口设计：站在使用者的角度进行设计
 * 三个方面：
 * 1. 方法定义细度
 * 2. 参数
 * 3. 返回类型  return  类型/异常
 */
public interface SeckillService {

    /**
     * 获取到所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启是否输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;

}
