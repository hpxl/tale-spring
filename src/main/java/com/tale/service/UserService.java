package com.tale.service;

import com.tale.model.entity.Users;

/**
 * Created by hpxl on 3/5/18.
 */
public interface UserService {

    public Users queryUserById(Integer uid);

    /**
     * 根据主键更新user对象
     * @param users
     * @return
     */
    public Integer updateByUid(Users users);

    /**
     * 登录
     * @param username
     * @param password
     */
    public Users login(String username, String password);

    public Integer save(Users users);
}
