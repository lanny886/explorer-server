package com.xyz.browser.app.core.websocket;

import java.security.Principal;

/**
 *
 * @ClassName: User
 * @Description: 客户端用户
 * @author cheng
 * @date 2017年9月29日 下午3:02:54
 */
public final class User implements Principal {

    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}