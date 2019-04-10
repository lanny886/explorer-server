package com.xyz.browser.app.modular.system.service.impl;

import com.xyz.browser.app.modular.system.model.Summary;
import com.xyz.browser.app.modular.system.dao.SummaryMapper;
import com.xyz.browser.app.modular.system.service.ISummaryService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * <p>
 * 概要信息 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-18
 */
@Service
public class SummaryServiceImpl extends ServiceImpl<SummaryMapper, Summary> implements ISummaryService {

    @Autowired
    private SummaryMapper summaryMapper;
    @Override
    public void upt(Summary summary) {
        try {
            if(!isAllFieldNull(summary))
                this.summaryMapper.upt(summary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isAllFieldNull(Object obj) throws Exception{
        Class stuCla = (Class) obj.getClass();// 得到类对象
        Field[] fs = stuCla.getDeclaredFields();//得到属性集合
        boolean flag = true;
        for (Field f : fs) {//遍历属性
            if(!Modifier.isStatic(f.getModifiers())){
                f.setAccessible(true);// 设置属性是可以访问的(私有的也可以)
                Object val = f.get(obj);// 得到此属性的值
                if(val!=null) {//只要有1个属性不为空,那么就不是所有的属性值都为空
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }
}
