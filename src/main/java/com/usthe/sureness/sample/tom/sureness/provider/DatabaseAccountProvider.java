package com.usthe.sureness.sample.tom.sureness.provider;

import com.usthe.sureness.provider.SurenessAccount;
import com.usthe.sureness.provider.SurenessAccountProvider;
import com.usthe.sureness.sample.tom.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 配置账号来源的数据源
 * the provider provides account info
 * load account info from database
 * @author tomsun28
 * @date 22:44 2020-03-02
 */
@Component
public class DatabaseAccountProvider implements SurenessAccountProvider {

    @Autowired
    AccountService accountService;

    /**
     * 重写方法加载账号数据（账号名称、密码、状态、角色）
     * @param appId
     * @return
     */
    @Override
    public SurenessAccount loadAccount(String appId) {
        return accountService.loadAccount(appId);
    }
}
