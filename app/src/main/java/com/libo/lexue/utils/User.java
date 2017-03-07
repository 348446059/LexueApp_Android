package com.libo.lexue.utils;

import java.io.Serializable;

/**
 * Created by libo on 2017/2/28.
 */

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    //用户id
    private String id;

    //图片路径
    private String imgPath;
    //显示名称
    private String name;
    //密码
    private String password;
    //记住密码
    private boolean isRemeber;
    //自动登录
    private boolean isAutoLogin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemeber() {
        return isRemeber;
    }

    public void setRemeber(boolean remeber) {
        isRemeber = remeber;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        isAutoLogin = autoLogin;
    }
}
