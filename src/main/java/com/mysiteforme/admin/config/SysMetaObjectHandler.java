package com.mysiteforme.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.mysiteforme.admin.base.MySysUser;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatisplus自定义填充公共字段 ,即没有传的字段自动填充
 * @author chen
 */
@Component
public class SysMetaObjectHandler implements MetaObjectHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //新增填充
    @Override
    public void insertFill(MetaObject metaObject) {
        logger.info("正在调用该insert填充字段方法");
        Object createDate = getFieldValByName("createDate",metaObject);
        Object createId = getFieldValByName("createId",metaObject);
        Object updateDate = getFieldValByName("updateDate",metaObject);
        Object updateId = getFieldValByName("updateId",metaObject);


        if (null == createDate) {
            setFieldValByName("createDate", new Date(),metaObject);
        }
        if (null == createId) {
            if(MySysUser.ShiroUser() != null) {
                setFieldValByName("createId", MySysUser.id(), metaObject);
            }
        }
        if (null == updateDate) {
            setFieldValByName("updateDate", new Date(),metaObject);
        }
        if (null == updateId) {
            if(MySysUser.ShiroUser() != null) {
                setFieldValByName("updateId", MySysUser.id(), metaObject);
            }
        }
    }

    //更新填充
    @Override
    public void updateFill(MetaObject metaObject) {
        logger.info("正在调用该update填充字段方法");
        setFieldValByName("updateDate",new Date(), metaObject);
        Object updateId = getFieldValByName("updateId",metaObject);
        if (null == updateId) {
            setFieldValByName("updateId", MySysUser.id(), metaObject);
        }
    }
}
