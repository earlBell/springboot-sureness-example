package com.usthe.sureness.sample.tom.controller;

import cn.hutool.core.util.IdUtil;
import com.usthe.sureness.provider.SurenessAccount;
import com.usthe.sureness.sample.tom.pojo.cache.SurenessAccountCO;
import com.usthe.sureness.sample.tom.pojo.dto.Account;
import com.usthe.sureness.sample.tom.pojo.dto.Message;
import com.usthe.sureness.sample.tom.service.AccountService;
import com.usthe.sureness.util.JsonWebTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author tomsun28
 * @date 00:24 2019-08-01
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("/custom/token")
    public ResponseEntity<Message> issueCustomToken(@RequestBody @Validated Account account) {
        boolean authenticatedFlag = accountService.authenticateAccount(account);
        if (!authenticatedFlag) {
            Message message = Message.builder()
                    .errorMsg("username or password not incorrect").build();
            if (log.isDebugEnabled()) {
                log.debug("account: {} authenticated fail", account);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }
        //存放至Redis
        SurenessAccountCO cacheUser =  accountService.loadLoginSuccUser(account.getUsername());
        //设置token有效时间内，每次固定时间刷新token
        cacheUser.setExpiredTime(LocalDateTime.now().plusMinutes(1));
        String token =  IdUtil.fastUUID();
        redisTemplate.opsForValue().set(token ,cacheUser,100, TimeUnit.MINUTES );

        Map<String, String> responseData = Collections.singletonMap("customToken", token);
        Message message = Message.builder().data(responseData).build();
        if (log.isDebugEnabled()) {
            log.debug("issue token success, account: {} -- token: {}", account, token);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public ResponseEntity<Message> accountRegister(@RequestBody @Validated Account account) {
        if (accountService.registerAccount(account)) {
            Map<String, String> responseData = Collections.singletonMap("success", "sign up success, login after");
            Message message = Message.builder().data(responseData).build();
            if (log.isDebugEnabled()) {
                log.debug("account: {}, sign up success", account);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } else {
            Message message = Message.builder()
                    .errorMsg("username already exist").build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
    }
}
