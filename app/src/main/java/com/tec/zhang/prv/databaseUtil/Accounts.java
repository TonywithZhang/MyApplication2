package com.tec.zhang.prv.databaseUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by zhang on 2017/6/2.
 */

public class Accounts extends DataSupport{
    private String userName;
    private String passWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
