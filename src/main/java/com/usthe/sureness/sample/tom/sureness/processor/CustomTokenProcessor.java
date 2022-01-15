package com.usthe.sureness.sample.tom.sureness.processor;

import com.usthe.sureness.processor.BaseProcessor;
import com.usthe.sureness.processor.exception.*;
import com.usthe.sureness.provider.SurenessAccount;
import com.usthe.sureness.sample.tom.sureness.provider.RedisAccountProvider;
import com.usthe.sureness.sample.tom.sureness.subject.CustomTokenSubject;
import com.usthe.sureness.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 自定义 通过token匹配出已登录用户信息的逻辑（需要传递缓存信息到SurenessFilterExample）
 * custom token processor, support CustomTokenSubject
 * when token Expired and can refresh, return refresh token value
 *
 * @author tomsun28
 * @date 2020-12-03 20:37
 */
public class CustomTokenProcessor extends BaseProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CustomTokenProcessor.class);

    @Autowired
    private RedisAccountProvider redisAccountProvider;

    @Override
    public boolean canSupportSubjectClass(Class<?> var) {
        return var == CustomTokenSubject.class;
    }

    @Override
    public Class<?> getSupportSubjectClass() {
        return CustomTokenSubject.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Subject authenticated(Subject var) throws SurenessAuthenticationException {
        //获取认证凭证
        String token = (String) var.getCredential();
        if ( redisAccountProvider.tokenExist(token) ) {
            SurenessAccount account = redisAccountProvider.loadAccount(token)  ;
            // attention: need to set subject own roles from account
            var.setPrincipal(account.getAppId());
            var.setOwnRoles(account.getOwnRoles());
            return var;

        }
        if (logger.isDebugEnabled()) {
            logger.debug("CustomTokenProcessor authenticated fail");
        }
        throw new IncorrectCredentialsException("the token authenticated error");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void authorized(Subject var) throws SurenessAuthorizationException {
        List<String> ownRoles = (List<String>) var.getOwnRoles();
        List<String> supportRoles = (List<String>) var.getSupportRoles();
        // if null, note that not config this resource
        if (supportRoles == null) {
            return;
        }
        // if config, ownRole must contain the supportRole item
        if (ownRoles != null && supportRoles.stream().anyMatch(ownRoles::contains)) {
            return;
        }
        throw new UnauthorizedException("custom authorized: do not have the role to access resource");
    }

    public CustomTokenProcessor setRedisAccountProvider(RedisAccountProvider redisAccountProvider) {
        this.redisAccountProvider = redisAccountProvider;
        return this;
    }
}
