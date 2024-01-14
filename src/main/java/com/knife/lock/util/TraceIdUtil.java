package com.knife.lock.util;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * traceId工具类
 */
public class TraceIdUtil {
    public static String createTraceId() {
        String traceId = null;

        // 如果使用了skywalking，则使用它的traceId
        String skywalkingTraceId = TraceContext.traceId();
        if (StringUtils.hasText(skywalkingTraceId)) {
            int length = skywalkingTraceId.length();
            if (length > 25) {
                traceId = skywalkingTraceId;
            }
        }

        // 通过uuid创建一个traceId
        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        return traceId;
    }
}
