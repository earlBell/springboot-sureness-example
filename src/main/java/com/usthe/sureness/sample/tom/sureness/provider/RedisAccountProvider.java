package com.usthe.sureness.sample.tom.sureness.provider;

import cn.hutool.core.util.IdUtil;
import com.usthe.sureness.provider.DefaultAccount;
import com.usthe.sureness.provider.SurenessAccount;
import com.usthe.sureness.provider.SurenessAccountProvider;
import com.usthe.sureness.sample.tom.pojo.cache.SurenessAccountCO;
import com.usthe.sureness.sample.tom.service.AccountService;
import com.usthe.sureness.sample.tom.sureness.processor.RefreshExpiredTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.lang.RegexPool.UUID;

/**
 * 配置已经登录账号 加载
 * the provider provides account info
 * load account info from redis
 * @author earl
 * @date  2022-01-14
 */
@Component
public class RedisAccountProvider implements SurenessAccountProvider {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean tokenExist(String token) {
        return redisTemplate.hasKey(token);
    }


    @Override
    public SurenessAccount loadAccount(String token) {
        SurenessAccountCO cacheUser = (SurenessAccountCO) redisTemplate.opsForValue().get(token);
        //固定时间内必须刷新token
        boolean needFresh = cacheUser.getExpiredTime().isBefore(LocalDateTime.now());
        if( needFresh ){
            String freshToken = IdUtil.fastUUID();
            //重新设置下一次过期时间
            cacheUser.setExpiredTime(LocalDateTime.now().plusMinutes(1));
            //获取当前剩余的最终失效分钟
            long lastMinutes = redisTemplate.getExpire(token, TimeUnit.MINUTES);
            //重新设置到redis
            redisTemplate.opsForValue().set(freshToken, cacheUser, lastMinutes, TimeUnit.MINUTES);
            //删除旧的token，防止被再次使用
            redisTemplate.delete(token);
            throw new RefreshExpiredTokenException(freshToken);
        }
        DefaultAccount.Builder accountBuilder = DefaultAccount.builder(cacheUser.getUsername())
                .setPassword(cacheUser.getPassword())
                .setSalt(cacheUser.getSalt())
                .setDisabledAccount(1 != cacheUser.getStatus())
                .setExcessiveAttempts(2 == cacheUser.getStatus());
        List<String> roles = cacheUser.getRoles();
        if (roles != null) {
            accountBuilder.setOwnRoles(roles);
        }
        return accountBuilder.build();
    }
}
