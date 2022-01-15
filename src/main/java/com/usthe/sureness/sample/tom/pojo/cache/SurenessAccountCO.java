package com.usthe.sureness.sample.tom.pojo.cache;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cache Object
 */
@Data
public class SurenessAccountCO {

    private Long id;

    private String username;

    private String password;

    private String salt;

    private String avatar;

    private String phone;

    private String email;

    private Integer sex;

    private Integer status;

    private Integer createWhere;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtUpdate;

    //过期时间
    private LocalDateTime expiredTime;

    private List<String> roles;

}
