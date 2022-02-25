package com.tommy.dao;

import com.tommy.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledDao {

    /**
     * 插入购买明细  可过滤重复
     * @return 插入的结果集数量（行数）
     */
    Integer insertSuccessKilled(@Param("seckillId") Long seckillId,
                                @Param("userPhone") Long userPhone);

    /**
     * 根据id查询successKilled 并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") Long seckillId,
                                       @Param("userPhone") Long userPhone);
}
