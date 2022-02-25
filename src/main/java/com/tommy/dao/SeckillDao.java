package com.tommy.dao;

import com.tommy.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数 > 1  表示更新的记录行数
     */
    Integer reduceNumber(@Param("seckillId") Long seckillId,
                         @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀库存对象
     * @param seckillId
     * @return
     */
    Seckill queryById(Long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offet
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") Integer offet,
                           @Param("limit") Integer limit);
}
