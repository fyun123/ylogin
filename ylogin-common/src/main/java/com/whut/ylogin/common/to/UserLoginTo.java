package com.whut.ylogin.common.to;

import lombok.Data;

@Data
public class UserLoginTo {

    private String loginAccount;

    private String password;

    private String redirectURL;
}
