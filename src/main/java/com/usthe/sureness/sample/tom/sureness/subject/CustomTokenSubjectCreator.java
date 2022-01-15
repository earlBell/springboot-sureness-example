package com.usthe.sureness.sample.tom.sureness.subject;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.usthe.sureness.subject.Subject;
import com.usthe.sureness.subject.SubjectCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 拦截请求中的token是否有效
 * custom token creator, get token from http request header - {"Token" : "tokenValue"}
 * @author tomsun28
 * @date 2020-12-03 22:08
 */
@Component
public class CustomTokenSubjectCreator implements SubjectCreate {

    private static final Logger logger = LoggerFactory.getLogger(CustomTokenSubjectCreator.class);

    private static final String HEADER_TOKEN = "Token";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 认证凭证是否有效
     * @param context
     * @return
     */
    @Override
    public boolean canSupportSubject(Object context) {
        if (context instanceof HttpServletRequest) {
            String authorization = ((HttpServletRequest)context).getHeader(HEADER_TOKEN);
            if (StrUtil.isEmpty(authorization)) {
                return false;
            }
            boolean isExpired = redisTemplate.hasKey(authorization);
            return  isExpired ;
        }
        return false;
    }

    /**
     * 返回请求凭证
     * @param context
     * @return
     */
    @Override
    public Subject createSubject(Object context) {
        String authorization = ((HttpServletRequest)context).getHeader(HEADER_TOKEN);
        String remoteHost = ((HttpServletRequest) context).getRemoteHost();
        String requestUri = ((HttpServletRequest) context).getRequestURI();
        String requestType = ((HttpServletRequest) context).getMethod();
        String targetUri = requestUri.concat("===").concat(requestType.toLowerCase());
        return CustomTokenSubject.builder(authorization)
                .setRemoteHost(remoteHost)
                .setTargetResource(targetUri)
                .build();
    }
}
