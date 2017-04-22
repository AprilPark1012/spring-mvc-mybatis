package com.qbian.common.plugin;

import com.qbian.common.annotation.Now;
import com.qbian.common.annotation.UUID;
import com.qbian.common.enums.MessageEnum;
import com.qbian.common.exception.ExternalServiceException;
import com.qbian.common.util.StringUtil;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * MyBatis 日期拦截器，拦截插入和更新语句
 * Created by qbian on 17/4/22.
 */
@Intercepts({@Signature(type = Executor.class, method ="update", args = {MappedStatement.class, Object.class})})
public class DatePlugin implements Interceptor {

    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] targets = invocation.getArgs();
        String packages[] = ((MappedStatement)invocation.getArgs()[0]).getId().split("\\.");
        String method = packages[packages.length - 1];
        if (targets != null && targets.length > 1) {
            Object obj = targets[1];
            if (obj instanceof HashMap) {
                for (Map.Entry<String, Object> entry : ((HashMap<String, Object>) obj).entrySet()) {
                    Object  params = entry.getValue();
                    if (params instanceof List) {
                        List<Object> objList = (List<Object>) params;
                        for (Object target : objList) {
                            setDate(target, method);
                        }
                    }
                }
            } else {
                setDate(obj, method);
            }
        }

        return invocation.proceed();
    }

    public Object plugin(Object target) {

        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties arg0) {

    }

    @SuppressWarnings("rawtypes")
    private void setDate(Object target, String method) {
        if (target == null) {
            return;
        }
        Class targetCls = target.getClass();
        // 当有now注解的时候，自动设定最新日期
        for (Field f : targetCls.getDeclaredFields()) {
            // UUID
            UUID uuid = f.getAnnotation(UUID.class);
            if (uuid != null && method.startsWith("insert")) {
                try {
                    f.setAccessible(true);
                    f.set(target, StringUtil.getUUIDString());
                } catch (Exception e) {
                    throw new ExternalServiceException(MessageEnum.DATABASE_DATE_ERROR);
                }
            }
            // 日期
            Now now = f.getAnnotation(Now.class);
            if (now != null) {
                String type = now.type();
                if (method.startsWith("update") && !"U".equals(type)) {
                    continue;
                }try {
                    f.setAccessible(true);
                    f.set(target, new Date());
                } catch (Exception e) {
                    throw new ExternalServiceException(MessageEnum.DATABASE_DATE_ERROR);
                }
            }
        }
    }



}
