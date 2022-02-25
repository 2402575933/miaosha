package com.tommy.service.impl;

import com.tommy.dao.SeckillDao;
import com.tommy.dao.SuccessKilledDao;
import com.tommy.dto.Exposer;
import com.tommy.dto.SeckillExecution;
import com.tommy.entity.Seckill;
import com.tommy.entity.SuccessKilled;
import com.tommy.enums.SeckillStatEnum;
import com.tommy.exception.RepeatKillException;
import com.tommy.exception.SeckillCloseException;
import com.tommy.exception.SeckillException;
import com.tommy.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Date;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 注入到service中
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    // 混淆盐值字符串
    private final String slat = "763g5t@#$%he9rh7gyh9e0qkg^&ief9ig";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        // id不存在
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        Date nowDate = new Date();
        Date startDate = seckill.getStartTime();
        Date endDate = seckill.getEndTime();

        // 秒杀已超时或秒杀未开始
        if (nowDate.getTime() < startDate.getTime()
        || nowDate.getTime() > endDate.getTime()) {
            return new Exposer(false, seckillId, nowDate.getTime(), startDate.getTime(),
                    endDate.getTime());
        }

        // 验证成功，暴露接口
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    /**
     * 生成md5字符串的方法
     * @param seckillId
     * @return
     */
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 1. 使用注解式事务方法
     * 2. 保证事务的执行时间尽可能的短，不要穿插其他的网络操作
     * 3. 不是所有的方法都需要事务,如只有一条修改操作，只读操作不需要事务控制
     *
     * 封装dto，用于表示Service返回格式SeckillExecution
     * 封装异常类，继承RuntimeException
     * 只要验证失败就throw xxxException
     * 最后的Exception 应该在处理后将包装为RuntimeException  （处理后即刻抛出自定义异常的父异常SeckillException）
     *
     * 方法的意义：执行逻辑不符合要求：抛出异常，回滚。
     * 执行逻辑符合要求：返回给Controller层消息
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {

        // 没有md5、md5解析失败 => 抛出异常
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        try {
            // 秒杀逻辑：减库存、添加购买记录
            // 执行秒杀逻辑: 减库存
            Date nowTime = new Date();
            // 将能否加入的逻辑判断写在sql中
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);

            // 数据库中数据更新失败  =>
            if (updateCount <= 0) {
                // 没有更新到记录中，超时了
                throw new SeckillCloseException("seckill is closed");
            } else {
                // 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有的异常转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
