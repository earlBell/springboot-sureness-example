package com.usthe.sureness.sample.tom.controller;

import com.usthe.sureness.provider.annotation.RequiresRoles;
import com.usthe.sureness.provider.annotation.WithoutAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author tomsun28
 * @date 2021/1/19 14:53
 */
@RestController
public class AnnotationController {

    /**
     * 优先注解读取（不论角色是否和菜单绑定）
     * roles：必需为role角色表的代码
     */

    @GetMapping("/api/annotation/source1")
    @RequiresRoles(roles = {"role1", "role2","role_guest"}, mapping = "/api/annotation/source1", method = "get")
    public ResponseEntity<String> api1Mock1() {
        return ResponseEntity.ok("success");
    }

    /**
     * 不用鉴权
     */
    @GetMapping("/api/annotation/source2")
    @WithoutAuth(mapping = "/api/annotation/source2", method = "get")
    public ResponseEntity<String> api1Mock2() {
        return ResponseEntity.ok("success");
    }

    /**
     * 数据库鉴权方式
     * 需在资源表插入和URL路径一样的资源 ：/api/annotation/source3
     * 若/api/annotation/source 资源没在数据库中和任何角色绑定关系，就不能访问到该接口
     */
    @GetMapping("/api/annotation/source3")
    public ResponseEntity<String> api1Mock3() {
        return ResponseEntity.ok("success");
    }

}
