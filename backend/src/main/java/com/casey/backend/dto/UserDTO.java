package com.casey.backend.dto;

import lombok.Data;

@Data
// UserDto 代替 User 的好处是：1. 内存负荷不至于太高 2. 用户信息敏感不会泄露
public class UserDTO {
    private Long id;
    private String nickName;
    private String icon;
}
