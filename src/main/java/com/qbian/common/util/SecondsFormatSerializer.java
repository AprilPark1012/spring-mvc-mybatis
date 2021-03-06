package com.qbian.common.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

/**
 * java.util.Date 类型转 秒时间戳后返回接口调用者
 * Created by qbian on 17/4/22.
 */
public final class SecondsFormatSerializer implements ObjectSerializer {

    /**
     * 日期型字段转换为秒后输出。
     *
     * @param serializer serializer
     * @param object 对象值
     * @param obj1 obj1对象
     * @param type type对象
     */
    public void write(JSONSerializer serializer, Object object, Object obj1,
                      Type type, int i) throws IOException {
        if (object == null) {
            serializer.getWriter().writeNull();
            return;
        } else {
            Date date = (Date) object;
            long seconds = date.getTime() / 1000;
            serializer.write(String.valueOf(seconds));
            return;
        }
    }
}
