package com.usthe.sureness.sample.tom.sureness.provider;

import com.usthe.sureness.matcher.PathTreeProvider;
import com.usthe.sureness.sample.tom.service.ResourceService;
import com.usthe.sureness.util.SurenessCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 配置菜单资源来源
 * ths provider provides path resources
 * load sureness config resource form database
 * @author tomsun28
 * @date 16:00 2019-08-04
 */
@Component
public class DatabasePathTreeProvider implements PathTreeProvider {

    @Autowired
    private ResourceService resourceService;

    /**
     * 每个可用的菜单资源对应的多个角色（字符串拼接）
     * @return
     */
    @Override
    public Set<String> providePathData() {
        return SurenessCommonUtil.attachContextPath(getContextPath(), resourceService.getAllEnableResourcePath());

    }

    /**
     *  每个不可用的菜单资源对应的多个角色（字符串拼接）
     * @return
     */
    @Override
    public Set<String> provideExcludedResource() {
        return SurenessCommonUtil.attachContextPath(getContextPath(), resourceService.getAllDisableResourcePath());
    }

}
