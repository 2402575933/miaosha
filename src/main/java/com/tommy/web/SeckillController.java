package com.tommy.web;

import com.mchange.v2.c3p0.test.FreezableDriverManagerDataSource;
import com.tommy.dto.Exposer;
import com.tommy.dto.SeckillExecution;
import com.tommy.dto.SeckillResult;
import com.tommy.entity.Seckill;
import com.tommy.enums.SeckillStatEnum;
import com.tommy.exception.RepeatKillException;
import com.tommy.exception.SeckillCloseException;
import com.tommy.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(name = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        // 获取列表页
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("list", seckillList);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "false";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(Long seckillId) {
        SeckillResult<Exposer> result;

        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<>(true, exposer);
        } catch (Exception e) {
            logger.warn("error:{}", e.getMessage());
            result = new SeckillResult<>(true, e.getMessage());
        }
        return result;
    }

    /**
     * 参数：restful接口参数参数
     *
     * @param seckillId 秒杀物品id
     * @param md5       md5加密码
     * @param phone     用户手机号，用于校验用户身份
     * @return SeckillResult<SeckillExecution>
     *     返回值封装在dto 中的  SeckillResult封装返回json格式
     *     返回的承载数据 data 为 dto SeckillExecution
     *
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone) {
        // 如果手机号是空，直接返回
        if (phone == null) {
            return new SeckillResult<>(false, "未注册");
        }
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
            // 如果抛出异常了，则不会继续往下执行了，若能执行到下面，说明没有异常抛出，且是符合要求的结果
            return new SeckillResult<>(true, seckillExecution);
        } catch (RepeatKillException e) {
            // 给出提示信息，重复秒杀
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<>(false, seckillExecution);
        } catch (SeckillCloseException e) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<>(false, seckillExecution);
        } catch (Exception e) {
            // 在service业务逻辑中，如果执行出现了问题，会向Controller中抛出异常，所以没有异常时就是正确的时候，
            // 存在异常时是处理失败    细化异常的处理
            logger.error(e.getMessage(), e);
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<>(false, seckillExecution);
        }
    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    public SeckillResult<Long> time() {
        return new SeckillResult<>(true, new Date().getTime());
    }
}
